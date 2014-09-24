package todo;

import se.lth.cs.realtime.*;
import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class WaterController extends PeriodicThread {
	AbstractWashingMachine machine;
	private RTEvent event;
	private int currentAction;
	private double targetLevel;
	private WashingProgram washingProgram;

	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		machine = mach;
	}

	public void perform() {
		if ((event = mailbox.tryFetch()) != null && event instanceof WaterEvent
				&& event.getSource() instanceof WashingProgram) {
			switch (((WaterEvent) event).getMode()) {
			case WaterEvent.WATER_IDLE:
				currentAction = WaterEvent.WATER_IDLE;
				machine.setDrain(false);
				machine.setFill(false);
				break;
			case WaterEvent.WATER_DRAIN:
				currentAction = WaterEvent.WATER_DRAIN;
				targetLevel = ((WaterEvent) event).getLevel();
				machine.setFill(false);
				machine.setDrain(true);
				washingProgram = ((WashingProgram) event.getSource());
				break;
			case WaterEvent.WATER_FILL:
				currentAction = WaterEvent.WATER_FILL;
				targetLevel = ((WaterEvent) event).getLevel();
				washingProgram = ((WashingProgram) event.getSource());
				if (machine.isLocked()) {
					machine.setFill(true);
					machine.setDrain(false);
				} else {
					System.err
							.println("Fill water command failed. Machine not locked.");
				}
			}
		}

		if (washingProgram != null
				&& ((currentAction == WaterEvent.WATER_FILL && machine
						.getWaterLevel() >= targetLevel) || (currentAction == WaterEvent.WATER_DRAIN && machine
						.getWaterLevel() <= targetLevel))) {
			washingProgram.putEvent(new AckEvent(this));
			washingProgram = null;
		}
	}
}
