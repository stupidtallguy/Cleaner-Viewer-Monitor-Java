import java.util.Random;

public class HallwayMonitor {

    // --- STATE (guarded by 'this') ---
    private int viewersIn = 0;          // number of viewers currently in the hallway
    private int waitingViewers = 0;     // viewers waiting due to dirt
    private boolean dirtReported = false;
    private boolean cleanerIn = false;

    // Shutdown control
    private int liveViewers = 0;        // total viewer threads still alive
    protected volatile boolean shuttingDown = false;

    private final Random rng = new Random();

    // --- Setup ---
    public synchronized void initLiveViewers(int n) {
        liveViewers = n;
    }

    // === VIEWER API ===
    public synchronized void enterViewer(String name) throws InterruptedException {
        while (cleanerIn || dirtReported) {
            wait();
        }
        viewersIn++;
        System.out.println("✅ " + name + " ENTERED hallway | viewersIn=" + viewersIn);
    }

    public void walkAndMaybeSeeDirt(String name) throws InterruptedException {
        Thread.sleep(200 + (int)(Math.random() * 400));

        // 20% chance to see dirt
        boolean sawDirt = rng.nextInt(5) == 0;
        if (!sawDirt) return;

        reportDirtAndWait(name);
    }

    private void reportDirtAndWait(String name) throws InterruptedException {
        synchronized (this) {
            if (!dirtReported) {
                dirtReported = true;
                System.out.println("⚠️  " + name + " saw DIRT! Summoning cleaner. Evacuate viewers!");
                notifyAll();
            } else {
                System.out.println("⚠️  " + name + " also noticed dirt. Joining the waiting queue.");
            }
            viewersIn--;
            waitingViewers++;
            System.out.println("🚪 " + name + " EXITED to waiting queue | viewersIn=" + viewersIn
                    + " | waitingViewers=" + waitingViewers);

            if (viewersIn == 0) {
                notifyAll();
            }

            // Wait until cleaning is done and hallway is available
            while (dirtReported || cleanerIn) {
                wait();
            }

            waitingViewers--;
            viewersIn++;
            System.out.println("🔙 " + name + " RE-ENTERED after cleaning | viewersIn=" + viewersIn
                    + " | waitingViewers=" + waitingViewers);
        }
    }

    // === CLEANER API ===
    public void cleanWhenCalled(String cleanerName) throws InterruptedException {
        synchronized (this) {
            // Wait either for dirt or for shutdown
            while (!dirtReported && !shuttingDown) {
                wait();
            }
            if (shuttingDown) return; // end gracefully

            // Exclusivity for cleaning
            while (viewersIn > 0 || cleanerIn) {
                wait();
            }
            cleanerIn = true;
            System.out.println("🧹 " + cleanerName + " ENTERED to clean (exclusive).");
        }

        // Clean outside monitor
        Thread.sleep(400 + (int)(Math.random() * 600));

        synchronized (this) {
            cleanerIn = false;
            dirtReported = false; // dirt cleared!
            System.out.println("✨ " + cleanerName + " FINISHED cleaning. Hallway is clear.");
            notifyAll(); // wake waiting viewers
        }
    }

    // --- Demo helper: mark one viewer finished for the demo ---
    public synchronized void demoViewerDone(String name) {
        viewersIn--;
        liveViewers--;
        System.out.println("👋 " + name + " DONE for demo | viewersIn=" + viewersIn);
        if (liveViewers == 0) {
            // trigger shutdown: wake cleaner if it's waiting
            shuttingDown = true;
            notifyAll();
        } else if (viewersIn == 0) {
            notifyAll();
        }
    }
}
