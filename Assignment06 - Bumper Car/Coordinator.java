public class Coordinator extends Thread {
    private boolean[] rideBegin, rideOver;
    private int car_riders;
    private boolean finish_loading;
    private int bump, wander;
    // BBuffer private data
    private int[] buffer;
    private int in, out;
    private int count;
    private int size;

    public Coordinator(int riders, int cars, int bumptime, int wanderTime) {
        this.in = this.out = this.count = this.car_riders = 0;
        this.finish_loading = false;
        this.bump = bumptime;
        this.wander = wanderTime;
        this.size = cars;
        this.buffer = new int[cars];
        this.rideBegin = new boolean[riders];
        this.rideOver = new boolean[riders];
        for (int i = 0; i < riders; i++) {
            this.rideBegin[i] = false;
            this.rideOver[i] = false;
        }
    }

    /* the following are public synchronized methods */
    public synchronized void getInLine(int rid) {
        enter(rid); // Add rider ID to the buffer
        rideBegin[rid] = false;
        while (!rideBegin[rid]) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized int load() {
        int curRider = remove(); // Get rider ID from the buffer
        rideBegin[curRider] = true;
        notifyAll(); // Notify riders waiting to start the ride
        finish_loading = false;
        while (!finish_loading) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return curRider;
    }

    public synchronized void takeAseat() {
        finish_loading = true;
        notifyAll(); // Notify that loading is complete
        car_riders++;
    }

    public synchronized void takeAride(int rid) {
        rideOver[rid] = false;
        while (!rideOver[rid]) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void unload(int rid) {
        rideOver[rid] = true;
        notifyAll(); // Notify that the ride is over for the rider
    }

    /* the following are public helper methods that are NOT synchronized */
    public int getBumpTime() {
        return (int) (Math.random() * bump * 1000); // Returns the amount of time a car bumps
    }

    public int getWanderTime() {
        return (int) (Math.random() * wander * 1000); // Returns the amount of time a rider wanders
    }

    public int findTotalRider() {
        return car_riders; // Returns the total number of unloaded cars
    }

    /** BBuffer Public Methods */
    public synchronized void enter(int id) {
        while (count == size) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("In enter: " + e);
            }
        }
        buffer[in] = id;
        in = (in + 1) % size;
        count++;
        notifyAll();
    }

    public synchronized int remove() {
        int tempId;
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("In remove: " + e);
            }
        }
        tempId = buffer[out];
        out = (out + 1) % size;
        count--;
        notifyAll();
        return tempId;
    }
}