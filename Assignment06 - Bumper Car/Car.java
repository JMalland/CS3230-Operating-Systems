class Car implements Runnable {
    private Coordinator coord = null;
    private int cid = -1; // Car id #
    private boolean stop;
    
    public Car(int id, Coordinator c) { // Ask if you don't know.
        // ...
        // new Thread(this).start();
    }

    public void run() {
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

    }
}