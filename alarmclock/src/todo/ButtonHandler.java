package todo;

import done.ClockInput;
import se.lth.cs.realtime.semaphore.Semaphore;

public class ButtonHandler extends Thread {
    private ClockInput input;
    private Semaphore inputSemaphore;
    private SharedData shareddata;
    private int setTime = -1;

    public ButtonHandler(ClockInput i, SharedData m) {
        input = i;
        inputSemaphore = input.getSemaphoreInstance();
        shareddata = m;
    }

    
    public void run() {
        while (shareddata.isAlive()) {
            inputSemaphore.take();
            switch (input.getChoice()) {
                case ClockInput.SET_TIME:
                    setTime = input.getValue();
                    break;
                case ClockInput.SET_ALARM:
                    shareddata.setAlarm(input.getValue());
                    setTime = -1;
                    break;
                case ClockInput.SHOW_TIME:
                    if (setTime > -1) {
                        shareddata.setTime(setTime);
                        setTime = -1;
                    }
                    shareddata.disableAlarm();
                    break;
            }
        }
    }
}
