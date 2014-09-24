 package todo;

import done.ClockInput;
import done.ClockOutput;
import se.lth.cs.realtime.semaphore.MutexSem;
import se.lth.cs.realtime.semaphore.Semaphore;

public class SharedData {
    private boolean alive = true;
    private Semaphore semaphore = new MutexSem();
    private ClockInput input;
    private ClockOutput output;
    private int time = 0,
                alarmTime = 0,
                alarmCounter = 0;

    public SharedData(ClockInput i, ClockOutput o) {
        input = i;
        output = o;
    }

    private void alarm() {
        if (input.getAlarmFlag() && time == alarmTime) {
            alarmCounter = 20;
        }

        if (alarmCounter > 0) {
            alarmCounter -= 1;
            output.doAlarm();
        }
    }

    public void setAlarm(int newTime) {
        semaphore.take();
        alarmTime = newTime;
        semaphore.give();
    }

    public void disableAlarm() {
        semaphore.take();
        alarmCounter = 0;
        semaphore.give();
    }

    public void setTime(int newTime) {
        semaphore.take();
        time = newTime;
        semaphore.give();
    }

    public void increment() {
        semaphore.take();
        time += 1;
        if (time % 100 > 59)
            time += 40;
        if (time % 10000 / 100 > 59)
            time += 4000;
        if (time > 235959)
            time -= 240000;

        output.showTime(time);
        alarm();
        semaphore.give();
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        semaphore.take();
        alive = false;
        semaphore.give();
    }
}