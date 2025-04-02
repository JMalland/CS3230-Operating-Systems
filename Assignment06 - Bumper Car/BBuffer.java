/*
 * BBuffer.java
 * Classic bounded buffer implementation to be used in CAOS.
 *
 * Created on April 16, 2010, 2:21 PM
 */

/**
 * Classic bounded buffer implementation to be used in CAOS.
 * This class has an enter method to put something in the buffer.
 * It also has a method to remove from the buffer.
 * @author Thomas Scanlan
 */
public class BBuffer 
{
   private int[]  buffer;
   private int in, out;
   private int count ;
   private int size ;
   
    /** Creates a new instance of BBuffer 
      * @param sizeOfBuffer represents the size of the buffer.
      */
    public BBuffer( int sizeOfBuffer  ) 
    {
       buffer = new int[ sizeOfBuffer ];
       count = 0  ;
       in = 0;
       out = 0;
       size = sizeOfBuffer;
    }
   /**
    * Producer method.
    * When the buffer(count) is full, it waits; then it deposits an id in 
    * the buffer and notifies if the count is at exactly one.
    * @param id - identifier of rider.
   */
   public synchronized void enter ( int id )
   {
      while ( count == size  )
      {
         try
         {
            this.wait();
         }
         catch ( InterruptedException e )
         {
            System.out.println( " In enter" + e );
         }
      }
      buffer[in] = id ;
      in = (in + 1) % size;
      count ++;
      notifyAll();
   }
   /*
    * Consumer method 
    * When the buffer is empty, it waits; then id removes an id from 
    * the buffer and notifies if the count is at exactly size - 1.
    * @return the next element in the buffer.   
   */
   public synchronized int remove ()
   {
      int tempId;
      while ( count  == 0 )
      {
         try
         {
            this.wait();
         }
         catch ( InterruptedException e )
         {
            System.out.println( " In getInLine" + e );
         }
      }
      tempId = buffer[out] ;
      out = ( out + 1) % size ;
      count--;
      notifyAll();
      return tempId;
   }
}
