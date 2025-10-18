public class CleanerViewerMonitor {

    private int viewersIn = 0;
    private int cleanersWaiting = 0;
    private boolean cleanerIn = false;

    // --- Viewer methods ---
    public synchronized void enterViewer(String name) throws InterruptedException {
        System.out.println(name + " wants to ENTER hallway (viewer)");
        while (cleanerIn || cleanersWaiting > 0) {
            wait();  // wait until no cleaner is inside or waiting
        }
        viewersIn++;
        System.out.println("✅ " + name + " ENTERED hallway | viewersIn=" + viewersIn);
    }

    public synchronized void exitViewer(String name) {
        viewersIn--;
        System.out.println("🚪 " + name + " EXITED hallway | viewersIn=" + viewersIn);
        if (viewersIn == 0) {
            notifyAll(); // wake up waiting cleaners (or viewers)
        }
    }

    // --- Cleaner methods ---
    public synchronized void enterCleaner(String name) throws InterruptedException {
        cleanersWaiting++;
        System.out.println(name + " wants to ENTER hallway (cleaner)");
        while (cleanerIn || viewersIn > 0) {
            wait(); // wait until hallway empty
        }
        cleanersWaiting--;
        cleanerIn = true;
        System.out.println("🧹 " + name + " ENTERED hallway to clean!");
    }

    public synchronized void exitCleaner(String name) {
        cleanerIn = false;
        System.out.println("✨ " + name + " EXITED hallway (cleaning done)");
        notifyAll(); // wake up all waiting threads
    }
}
