
class Coordinator extends Thread {
    private boolean[] rideBegin, rideOver;
    private int car_riders;
    private boolean finish_loading;
    private int bump;

    public Coordinator(int riders, int cars, int bumptime) {
        // - initialize most data to zero
        // - create buffer as an array of ints with size == cars
        // - create bool arrays, rideBegin and rideOver. size == riders
        // - initialize bool arrays to all false.
    }

    /* the following are public synchronized methods */
    public synchronized void getInLine(int rid) {
        // - put rid into the buffer
        // - set rideBegin[rid] to false
        // - wait while rideBegin[rid] is false
    }

    public synchronized int load() {
        // - Get curRider from the buffer
        // - set rideBegin[curRider] to true
        // - signal
        // - set finish_loading to false
        // - wait while finish_loading is false
        // - return curRider
        return 0;
    }
    
    public synchronized  void takeAseat() {
        // - set finish_loading to true
        // - signal
        // - increment car_riders
    }

    public synchronized void takeAride(int rid) {
        // - set rideOver[rid] to false
        // - wait while rideOver[rid] is false
    }
    
    public synchronized void unload(int rid) {
        // - set rideOver[rid] to true
        // - signal
    }

    /* the following are public helper methods that are NOT synchronized */
    public int getBumpTime() { //-- returns amount of time a car bumps
        return 0;
    }
    public int findTotalRider() { //-- returns the total number of unloaded cars
        return 0;
    }
}