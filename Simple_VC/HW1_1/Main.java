//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        CleanerViewerMonitor hallway = new CleanerViewerMonitor();

        Runnable viewer = () -> {
            String name = "Viewer-" + Thread.currentThread().getId();
            try {
                Thread.sleep((int)(Math.random() * 300)); // random delay before entering
                hallway.enterViewer(name);
                Thread.sleep((int)(Math.random() * 600 + 300)); // crossing time
                hallway.exitViewer(name);
            } catch (InterruptedException ignored) {}
        };

        Runnable cleaner = () -> {
            String name = "Cleaner-" + Thread.currentThread().getId();
            try {
                Thread.sleep((int)(Math.random() * 800)); // random delay before entering
                hallway.enterCleaner(name);
                Thread.sleep((int)(Math.random() * 600 + 300)); // cleaning time
                hallway.exitCleaner(name);
            } catch (InterruptedException ignored) {}
        };

        System.out.println("==== HALLWAY MONITOR SIMULATION STARTED ====");
        System.out.println("Viewers may share the hallway; cleaners need exclusive access.\n");

        // spawn threads
        for (int i = 0; i < 5; i++) new Thread(viewer).start();
        for (int i = 0; i < 2; i++) new Thread(cleaner).start();
        for (int i = 0; i < 5; i++) new Thread(viewer).start();
    }
}