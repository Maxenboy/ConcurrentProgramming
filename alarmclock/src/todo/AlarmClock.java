package todo;




import se.lth.cs.realtime.semaphore.Semaphore;
import done.ClockInput;
import done.ClockOutput;

public class AlarmClock extends Thread {

	private static ClockInput	input;
	private static ClockOutput	output;
	private static Semaphore	sem;
    private SharedData shareddata;

    public AlarmClock(ClockInput i, ClockOutput o) {
        input = i;
        output = o;
        sem = input.getSemaphoreInstance();
        shareddata = new SharedData(input, output);
        new Ticker(shareddata).start();
        new ButtonHandler(input, shareddata).start();
    }

    public void terminate() {
        shareddata.kill();
    }

    public void run() {

    }
}