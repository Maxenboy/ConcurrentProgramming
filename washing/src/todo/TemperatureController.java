package todo;

import se.lth.cs.realtime.*;
import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class TemperatureController extends PeriodicThread {
	private AbstractWashingMachine machine;
	private RTEvent event;
	private int currentAction;
	private double targetTemperature;
	private WashingProgram washingProgram;
	private static final double MIN_WATER_LEVEL=0.1,TEMP_DIFF=1.9;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		machine = mach;
		currentAction=TemperatureEvent.TEMP_IDLE;
	}

	public void perform() {
		if ((event = mailbox.tryFetch()) != null && event instanceof TemperatureEvent && event.getSource() instanceof WashingProgram) {
            switch (((TemperatureEvent) event).getMode()) {
                case TemperatureEvent.TEMP_IDLE:
                    currentAction = TemperatureEvent.TEMP_IDLE;
                    machine.setHeating(false);
                    break;
			case TemperatureEvent.TEMP_SET:
				currentAction = TemperatureEvent.TEMP_SET;
                targetTemperature = ((TemperatureEvent) event).getTemperature();
                washingProgram = ((WashingProgram) event.getSource());
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
