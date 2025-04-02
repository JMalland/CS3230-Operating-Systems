
class Racer implements Runnable {

   private String name;
   private int M = 0;              
   private volatile long sum = 0;  

   public Racer(String name, int M) {
      this.name = name;
      this.M = M;
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is alive, M=" + M);
   }

   private long fn(long j, int k) {
      long total = j;
      for (int i = 1;  i <= k; i++) total += (2 * i - 1) * (2 * i - 1);
      return total;
   }

   public void run() {
      System.out.println("\n");
	  System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is running ");
	  System.out.println("Name:" 
		 + Thread.currentThread().getName());
	  System.out.println("ID:" 
		 + Thread.currentThread().getId());
      for (int m = 1; m <= M; m++)
      {
         sum = fn(sum, m);
      }
      System.out.println("age()=" + Scheduler.age() + ", "
         + name + " is done, sum = " + sum);
	  System.out.println("\n");
   }
}

class RaceTwoThreads {

   private static int M = 100;
   private final static int numRacers = 1;

   public static void main(String[] args) {

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

      Racer racerObject = new Racer("RacerObject", M);
      Thread[] racer = new Thread[numRacers];
      for (int i = 0; i < numRacers; i++)
         racer[i] = new Thread(racerObject, "RacerThread" + i);
      for (int i = 0; i < numRacers; i++) {
         racer[i].start();
      }
      System.out.println("age()=" + Scheduler.age() + 
                       ", all Racer threads started");

      try {
            for (int i = 0; i < numRacers; i++) racer[i].join();
         } catch (InterruptedException e) {
            System.err.println("interrupted out of join");
         }

      System.out.println("RaceTwoThreads done");
      System.exit(0);
   }
}
