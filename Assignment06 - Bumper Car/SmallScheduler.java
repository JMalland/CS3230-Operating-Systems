public class SmallScheduler
{
  private static final long startTime = System.currentTimeMillis();
  
   protected static final long age() {
      return System.currentTimeMillis() - startTime;
   }
   public static void nap(int napTimeMS)
   {
      try
      {
         Thread.sleep(napTimeMS);
      }
      catch (InterruptedException e)
      {
         System.out.println("Interrupted out of sleep");
      }
   }
   
}