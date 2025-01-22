import java.util.concurrent.Semaphore;

class ReadWriteLock {
    private Semaphore rw_mutex = new Semaphore(1); // Controls access between reading and writing (gives priority to writer)
    private Semaphore mutex = new Semaphore(1);   // Safely updates the reader count
    private int readCount = 0; // Active reader count

    // Read Lock
    public void readLock() {
        try {
            mutex.acquire(); // Safely update the reader count
            if (readCount == 0) { 
                rw_mutex.acquire(); // The first reader blocks the writer
            }
            readCount++;
            mutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Release Read Lock
    public void readUnLock() {
        try {
            mutex.acquire(); // Safely update the reader count
            readCount--;
            if (readCount == 0) { 
                rw_mutex.release(); // The last reader releases write access
            }
            mutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Write Lock
    public void writeLock() {
        try {
            rw_mutex.acquire(); // Acquires write access, blocking other writers and readers
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Release Write Lock
    public void writeUnLock() {
        rw_mutex.release(); // Releases write access
    }
}

// Test Class
public class Test {
    public static void main(String[] args) {
        final ReadWriteLock rwLock = new ReadWriteLock();

        // Reader Task
        Runnable reader = new Runnable() {
            @Override
            public void run() {
                rwLock.readLock();
                System.out.println(Thread.currentThread().getName() + " is reading...");
                try {
                    Thread.sleep(1000); // Reading time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " has finished reading.");
                rwLock.readUnLock();
                
            }
        };

        // Writer Task
        Runnable writer = new Runnable() {
            @Override
            public void run() {
                rwLock.writeLock(); 
                System.out.println(Thread.currentThread().getName() + " is writing...");
                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " has finished writing.");
                rwLock.writeUnLock();
                
            }
        };
        
        

        // Creating Reader and Writer Threads
        Thread t1 = new Thread(reader, "Reader 1");
        Thread t2 = new Thread(reader, "Reader 2");
        Thread t3 = new Thread(writer, "Writer 1");
        Thread t4 = new Thread(reader, "Reader 3");
        Thread t5 = new Thread(writer, "Writer 2");

        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        

        
    }
}

