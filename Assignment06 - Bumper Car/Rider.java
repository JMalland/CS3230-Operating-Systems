class Rider implements Runnable {
    private Coordinator coord = null;
    private int rid = -1; // Rider id #
    private boolean stop;
    
    public Rider(int id, Coordinator c) { // Ask if you don't know.
        // ...
        // new Thread(this).start();
    }
    
    public void run() {
        // while ( !stop )
        //     0. Generate random wander - time & write out wandering message (see sample output)
        //     1. nap // wander around the park
        //     2. write out that Rider is joining the line. (see sample output)
        //     3. coord.getInLine(. . .)
        //     4. actually take a second to take a seat : nap 1000
        //     5. coord.takeAseat(. . .)
        //     6. coord.takeAride(. . .)
        //     7. write out that rider has finished riding. (see sample output)
    }

    public void stopIt() {

    }
}