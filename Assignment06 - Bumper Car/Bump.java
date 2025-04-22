public class Bump {
    private static int riders = 10;
    private static int cars = 5;
    private static int bumpTime = 5;
    private static int wanderTime = 10;
    private static int runTime = 40;

    public static void main(String[] args) {
        // Use GetOpt for argument parsing
        GetOpt getopt = new GetOpt(args, "Ur:c:b:w:R:");

        int opt;
        while ((opt = getopt.getopt()) != GetOpt.optEOF) {
            switch(opt) {
                case('r'):
                    Bump.riders = getopt.processArg(getopt.optArgGet(), 10);
                    break;
                case('c'):
                    Bump.cars = getopt.processArg(getopt.optArgGet(), 5);
                    break;
                case('b'):
                    Bump.bumpTime = getopt.processArg(getopt.optArgGet(), 5);
                    break;
                case('w'):
                    Bump.wanderTime = getopt.processArg(getopt.optArgGet(), 10);
                    break;
                case('R'):
                    Bump.runTime = getopt.processArg(getopt.optArgGet(), 40);
                    break;
                case('U'):
                    // Display usage information and exit
                    System.out.println("Usage: -r# -c# -b# -w# -R#");
                    System.out.println("   -r# = number of rider threads (default 10)");
                    System.out.println("   -c# = Number of car threads (default 5)");
                    System.out.println("   -b# = Max bump time in seconds (default 5)");
                    System.out.println("   -w# = Max wander time in seconds (default 10)");
                    System.out.println("   -R# = Total time in seconds (default 40)");
                    System.out.println("   -U = Print Usage");
                    return;
            }
        }

        // Validate inputs
        if (Bump.riders <= 0 || Bump.cars <= 0 || Bump.bumpTime <= 0 || Bump.wanderTime <= 0 || Bump.runTime <= 0) {
            System.out.println("All parameters must be positive integers.");
            return;
        }

        System.out.println("Bumper Cars: numRiders = " + Bump.riders + ", numCars = " + Bump.cars);
        System.out.println("timeBump = " + Bump.bumpTime + ", timeWander = " + Bump.wanderTime + ", runTime = " + Bump.runTime);

        // Initialize Coordinator
        Coordinator coordinator = new Coordinator(Bump.riders, Bump.cars, Bump.bumpTime, Bump.wanderTime);

        // Create and start rider threads
        Rider[] ridersThreads = new Rider[riders];
        for (int i = 0; i < riders; i++) {
            ridersThreads[i] = new Rider(i, coordinator); // Create rider thread
        }

        // Create and start car threads
        Car[] carThreads = new Car[Bump.cars];
        for (int i = 0; i < Bump.cars; i++) {
            carThreads[i] = new Car(i, coordinator); // Create car thread
        }

        System.out.println("All Car & Rider threads started.");

        SmallScheduler.nap(Bump.runTime * 1000); // Run the simulation for the specified time

        System.out.println("Calling stopIts; wait 15 seconds; do mini-report");

        for (int i = 0; i < Bump.riders; i++) {
            ridersThreads[i].stopIt(); // Stop rider threads
        }
        for (int i = 0; i < Bump.cars; i++) {
            carThreads[i].stopIt(); // Stop car threads
        }

        SmallScheduler.nap(15000); // Wait for 15 seconds

        System.out.println("Number of times a car was ridden " + coordinator.findTotalRider());
        
    }
}