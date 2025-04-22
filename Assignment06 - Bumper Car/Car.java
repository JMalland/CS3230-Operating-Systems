public class Car implements Runnable {
    private Coordinator coord = null;
    private int cid = -1; // Car id #
    private boolean stop;
    
    public Car(int id, Coordinator c) { // Ask if you don't know.
        this.cid = id;
        this.coord = c;
        new Thread(this).start(); // Start the car thread
    }

    public void run() {
        System.out.println("Car " + cid + " is alive");
        while (!stop) {
            System.out.println("age() = " + SmallScheduler.age() + ", Car " + cid + " is in line to be loaded.");
            int rid = coord.load(); // car waits until loaded
            
            int bumpTime = coord.getBumpTime(); // get bumping time
            System.out.println("age() = " + SmallScheduler.age() + ", Car " + cid + " with Rider " + rid + " is bumping on the floor for " + bumpTime + " ms");
            SmallScheduler.nap(bumpTime); // bump on the floor
            
            SmallScheduler.nap(1000); // actually take a second to unload

            coord.unload(rid); // car waits until loaded
            System.out.println("age() = " + SmallScheduler.age() + ", Car " + cid + " has unloaded.");
        }

        // while ( !stop )
        //     0. Write out the Car + id "is in line to be loaded."
        //     1. rid = coord.load(. . .) // car waits until loaded
        //     2. get bumpingTime
        //     3. bump (nap for bumpingTime) // bump on the floor
        //     4. actually take a second to unload : nap 1000
        //     5. coord.unload(. . .) // car waits until loaded
        //     6. write out Car + id + " has unloaded." (see sample output)
    }

    public void stopIt() {
        stop = true; // Stop the car thread
    }
}