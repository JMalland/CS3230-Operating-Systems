public class Scheduler
{
   private static final long startTime = System.currentTimeMillis();
   protected static final long age()
   {
      return System.currentTimeMillis() - startTime;
   }
}
