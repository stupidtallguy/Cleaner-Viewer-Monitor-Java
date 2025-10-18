import java.util.Random;

public class HallwayMonitor {

    private int viewersIn = 0;          // number of viewers currently in the hallway
    private int waitingViewers = 0;     // viewers waiting due to dirt
    private boolean dirtReported = false;
    private boolean cleanerIn = false;

    private final Random rng = new Random();


    public synchronized void enterViewer(String name) throws InterruptedException {
        // Block if hallway is dirty or cleaner is inside
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

    public void cleanWhenCalled(String cleanerName) throws InterruptedException {
        synchronized (this) {
            while (!dirtReported) {
                wait();
            }
            while (viewersIn > 0 || cleanerIn) {
                wait();
            }
            cleanerIn = true;
            System.out.println("🧹 " + cleanerName + " ENTERED to clean (exclusive).");
        }

        Thread.sleep(400 + (int)(Math.random() * 600));

        synchronized (this) {
            cleanerIn = false;
            dirtReported = false; // dirt cleared!
            System.out.println("✨ " + cleanerName + " FINISHED cleaning. Hallway is clear.");
            notifyAll(); // wake waiting viewers
        }
    }

    public synchronized void demoViewerDone(String name) {
        viewersIn--;
        System.out.println("👋 " + name + " DONE for demo | viewersIn=" + viewersIn);
        if (viewersIn == 0) notifyAll();
    }

}
