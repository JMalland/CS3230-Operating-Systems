import java.util.concurrent.Semaphore;

// Racer.java
/**
 * The Racer class represents a thread that performs calculations in a critical section.
 * It extends the Thread class and includes methods for running the thread and calculating a sum.
 * Each Racer instance has a unique identifier and is associated with an Arbitrator to manage access
 * to the critical section.
 */
class Racer extends Thread {

   private String name; // The name of the racer
   private int M = 0; // The number of iterations for calculations       
   private static volatile long sum = 0; // The shared sum variable
   private int id; // The unique identifier for the racer
   private Arbitrator data; // The Arbitrator managing access to the critical section
   private Semaphore mutex; // The Semaphore managing access to the critical section (if not Arbitrator)

   /**
    * Constructs a Racer with the specified name, number of iterations, identifier, and Arbitrator.
    *
    * @param name The name of the racer
    * @param M The number of iterations for calculations
    * @param id The unique identifier for the racer
    * @param data The Arbitrator managing access to the critical section
    */
   public Racer(String name, int M, int id, Arbitrator data) {
      this.name = name;
      this.M = M;
      System.out.println("age()=" + Scheduler.age() + ", "
      + name + " is alive, M=" + M);
      this.id = id;
      this.data = data;
   }

   /**
    * Constructs a Racer with the specified name, number of iterations, identifier, and Semaphore.
    * @param name The name of the racer
    * @param M The number of iterations for calculations 
    * @param id The unique identifier for the racer
    * @param mutex The Semaphore managing access to the critical section
    */
   public Racer(String name, int M, int id, Semaphore mutex) {
      this.name = name;
      this.M = M;
      System.out.println("age()=" + Scheduler.age() + ", "
      + name + " is alive, M=" + M);
      this.id = id;
      this.mutex = mutex;
   }

   /**
    * Calculates the sum based on the given parameters.
    * @param j The initial value to be added to the sum
    * @param k The number of iterations for the calculation
    * @return The calculated sum
    */
   private long fn(long j, int k) {
      long total = j;
      for (int i = 1;  i <= k; i++) total += (2 * i - 1) * (2 * i - 1);
      return total;
   }

   /**
    * The run method is executed when the thread is started.
    * It performs calculations in the critical section managed by the Arbitrator.
    */
   @SuppressWarnings("deprecation")
   public void run() {
      System.out.println("\n");
	  System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is running ");
	  System.out.println("Name:" 
		 + Thread.currentThread().getName());
	  System.out.println("ID:" 
		 + Thread.currentThread().getId());
      
      if (mutex != null) {
         try {
             mutex.acquire(id);
         }
         catch (InterruptedException e) {
            System.err.println("unexpected interruption from acquire");
         }
      }
      else data.wantToEnterCS(id);
      for (int m = 1; m <= M; m++) {
        sum = fn(sum, m);
      }
      if (mutex != null) {
         mutex.release(id);
      }
      else data.finishedInCS(id);
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is done, sum = " + sum);
	  System.out.println("\n");
   }
}

/**
 * The RaceManyThreads class is the entry point for the racing application.
 * It initializes the application by parsing command-line arguments, creating
 * racer threads, and managing their execution.
 */
class RaceManyThreads {

   private static int M = 100; // The number of iterations for calculations
   private static int numRacers = 1; // The number of racer threads
   private static Arbitrator data; // The Arbitrator managing access to the critical section
   private static Semaphore mutex; // The Semaphore managing access to the critical section (if not Arbitrator)
   
   /**
    * Creates and simulates as many threads as specified by the user.
    * @param args
    */
   public static void main(String[] args) {

      GetOpt go = new GetOpt(args, "UtN:M:");
      go.optErr = true;
      String usage = "Usage: -t -M m";
      int ch = -1;
      boolean timeSlicingEnsured = false;
      while ((ch = go.getopt()) != go.optEOF) {
         if ((char)ch == 'U') {
            System.out.println(usage);  System.exit(0);
         }
         else if ((char)ch == 'M')
            M = go.processArg(go.optArgGet(), M);
         else if ((char)ch == 'N') // Added numRacers flag
            numRacers = go.processArg(go.optArgGet(), numRacers);
         else {
            System.err.println(usage);  System.exit(1);
         }
      }
      System.out.println("RaceTwoThreads: M=" + M + ", timeSlicingEnsured="
         + timeSlicingEnsured );

      //data = new Arbitrator(numRacers); // Create Arbitrator
      mutex = new Semaphore(numRacers); // Create Semaphore

      Racer[] racers = new Racer[numRacers]; // Declare Racers array
      for (int i = 0; i < racers.length; i++)
         racers[i] = new Racer("Racer " + i, M, i, mutex); // Initialize Racers
      for (Racer racer : racers)
         racer.start(); // Start Racers

      System.out.println("M=" + M + ", numRacers=" + numRacers); // Print M & number of racers
      System.out.println("age()=" + Scheduler.age() + 
                       ", all Racer threads started");

      try {
         for (int i = 0; i < racers.length; i++) racers[i].join();
      } catch (InterruptedException e) {
         System.err.println("interrupted out of join");
      }

      System.out.println("RaceTwoThreads done");
      System.exit(0);
   }
}
