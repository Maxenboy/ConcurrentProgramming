package todo;

import se.lth.cs.realtime.RTInterrupted;

public class Ticker extends Thread {
    private SharedData shareddata;
    private long waitTime = System.currentTimeMillis();

    public Ticker(SharedData m) {
        shareddata = m;
    }

    
    public void run() {
        long diff;

        while (shareddata.isAlive()) {
            while ((diff = waitTime - System.currentTimeMillis()) > 0) {
                try {
                    Thread.sleep(diff);
                } catch (InterruptedException e) {
                    throw new RTInterrupted(e.toString());
                }
            }

            waitTime += 1000 + diff;
            shareddata.increment();
        }
    }
}