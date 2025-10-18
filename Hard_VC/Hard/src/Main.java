//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("==== HALLWAY SIMULATION (monitor + wait/notify) ====");
        System.out.println("Viewers are always in the hallway until dirt is seen.");
        System.out.println("On dirt (20% chance), all viewers evacuate and a cleaner is summoned.\n");

        HallwayMonitor hallway = new HallwayMonitor();

        final int NUM_VIEWERS = 6;
        hallway.initLiveViewers(NUM_VIEWERS);

        // Cleaner thread: loops until shutdown is requested.
        Thread cleanerThread = new Thread(() -> {
            String name = "Cleaner";
            try {
                while (true) {
                    hallway.cleanWhenCalled(name);
                    synchronized (hallway) {
                        if (hallway.shuttingDown) break;
                    }
                }
            } catch (InterruptedException ignored) {}
            System.out.println("🛑 Cleaner exiting.");
        }, "Cleaner");
        cleanerThread.start();

        // Viewer threads that "live" in the hallway
        for (int i = 0; i < NUM_VIEWERS; i++) {
            final int idx = i;
            new Thread(() -> {
                String name = "Viewer-" + idx;
                try {
                    // initial enter
                    hallway.enterViewer(name);

                    // loop: walk and occasionally see dirt; after cleaning, they re-enter automatically
                    for (int iter = 0; iter < 20; iter++) {
                        hallway.walkAndMaybeSeeDirt(name);
                        Thread.sleep(150); // readability pause
                    }

                    // graceful end: viewer leaves simulation
                    hallway.demoViewerDone(name);

                } catch (InterruptedException ignored) {}
            }, "Viewer-" + i).start();
        }
    }

}