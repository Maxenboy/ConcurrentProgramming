package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;
import se.lth.cs.realtime.event.RTEvent;


public class TemperatureController extends PeriodicThread {
    private AbstractWashingMachine machine;
    private WashingProgram washingProgram;
    private RTEvent currentEvent;
    private int currentAction = TemperatureEvent.TEMP_IDLE;
    private double targetTemperature = 0d;
    private final static double
        TEMP_DIFF = 1.9d,
        MIN_WATER_LEVEL  = 0.1d;

	public TemperatureController(AbstractWashingMachine machine, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.machine = machine;
	}

	public void perform() {
        if ((currentEvent = mailbox.tryFetch()) != null && currentEvent instanceof TemperatureEvent && currentEvent.getSource() instanceof WashingProgram) {
            switch (((TemperatureEvent) currentEvent).getMode()) {
                case TemperatureEvent.TEMP_IDLE:
                    currentAction = TemperatureEvent.TEMP_IDLE;
                    machine.setHeating(false);
                    break;
                case TemperatureEvent.TEMP_SET:
                    currentAction = TemperatureEvent.TEMP_SET;
                    targetTemperature = ((TemperatureEvent) currentEvent).getTemperature();
                    washingProgram = ((WashingProgram) currentEvent.getSource());
                    break;
            }
        }

        if (currentAction == TemperatureEvent.TEMP_SET && machine.getWaterLevel() > MIN_WATER_LEVEL) {
            if (machine.getTemperature() < targetTemperature - TEMP_DIFF) {
                 machine.setHeating(true);
            } else if (machine.getTemperature() >= targetTemperature) {
                machine.setHeating(false);
                if (washingProgram != null) {
                    washingProgram.putEvent(new AckEvent(this));
                    washingProgram = null;
                }
            }
        }
	}
}