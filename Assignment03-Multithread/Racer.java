
class Racer implements Runnable { // input the interface name

   private String name;
   private int M = 0;              // these fields are shared by both
   private volatile long sum = 0;  // threads since there is one object

   public Racer(String name, int M) {
      this.name = name;
      this.M = M;
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is alive, M=" + M);
   }

   private long fn(long j, int k) {
      long total = j;
      for (int i = 1;  i <= k; i++) {
         total += (2 * i - 1) * (2 * i - 1);
      }
      return total;
   }

   public void run() {  // input the abstrac function name
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is running");
      for (int m = 1; m <= M; m++)
      {
         /*
          * "N = N + 1" type lost update (race condition) in following line
          */
         sum = fn(sum, m);
      }
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is done, sum = " + sum);
   }
}

class RaceTwoThreads {

   private static int M = 10;
   private final static int numRacers = 4; // fill in a number that how many threads you want to run

   public static void main(String[] args) {

      // parse command line arguments, if any, to override defaults
      GetOpt go = new GetOpt(args, "UtM:");
      go.optErr = true;
      String usage = "Usage: -t -M m";
      int ch = -1;
      boolean timeSlicingEnsured = false;
      while ((ch = go.getopt()) != go.optEOF) {
         if      ((char)ch == 'U') {
            System.out.println(usage);  System.exit(0);
         }
         else if ((char)ch == 'M')
            M = go.processArg(go.optArgGet(), M);
         else {
            System.err.println(usage);  System.exit(1);
         }
      }
      System.out.println("RaceTwoThreads: M=" + M + ", timeSlicingEnsured="
         + timeSlicingEnsured );

      // start the two threads, both in the same object
      // so they share one instance of its variable sum
      
	   Racer racerObject = new Racer("racerObject", M); // create a thread function object
      Thread[] workers = new Thread[numRacers]; // create an array of threads
      for (int i = 0; i < numRacers; i++)
         workers[i] = new Thread(racerObject, "Thread" + i); // create a racer thread 
      for (int i = 0; i < numRacers; i++) {
         workers[i].start(); // start each thread
      }
      System.out.println("age()=" + Scheduler.age() + 
                       ", all Racer threads started");

      // wait for them to finish if not forced consecutive
      try {
         for (int i = 0; i < numRacers; i++) workers[i].join(); // synchronize threads using join()
      } catch (InterruptedException e) {
            System.err.println("interrupted out of join");
      }

      // correct race-free final value of sum is 2*220 = 440 for M of 10
      // and 2*1335334000 = 2670668000 for M of 2000 (so `long sum' needed)
      System.out.println("RaceTwoThreads done");
      System.exit(0);
   }
}
