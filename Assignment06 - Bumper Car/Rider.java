public class Rider implements Runnable {
    private Coordinator coord = null;
    private int rid = -1; // Rider id #
    private boolean stop;
    
    public Rider(int id, Coordinator c) { // Ask if you don't know.
        this.rid = id;
        this.coord = c;
        new Thread(this).start(); // Start the rider thread
    }
    
    public void run() {
        System.out.println("Rider " + rid + " is alive");
        while (!stop) {
            int wanderTime = coord.getWanderTime(); // Random wander time
            System.out.println("age() = " + SmallScheduler.age() + ", Rider " + rid + " is wandering the park for " + wanderTime + " ms");
            SmallScheduler.nap(wanderTime); // wander around the park

            System.out.println("age() = " + SmallScheduler.age() + ", Rider " + rid + " is joining the line.");
            coord.getInLine(rid); // rider waits until in line
            
            SmallScheduler.nap(1000); // actually take a second to take a seat
            coord.takeAseat(); // rider sits down
            
            coord.takeAride(rid); // rider takes a ride
            System.out.println("age() = " + SmallScheduler.age() + ", Rider " + rid + " has finished riding.");
        }

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
        stop = true; // Stop the rider thread
    }
}