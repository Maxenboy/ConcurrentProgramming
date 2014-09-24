package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;
import se.lth.cs.realtime.event.RTEvent;

public class WaterController extends PeriodicThread {
    private AbstractWashingMachine machine;
    private RTEvent currentEvent;
    private WashingProgram washingProgram;
    private int currentAction = WaterEvent.WATER_IDLE;
    private double targetLevel = 0d;

	public WaterController(AbstractWashingMachine machine, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.machine = machine;
	}

	public void perform() {
		if ((currentEvent = mailbox.tryFetch()) != null && currentEvent instanceof WaterEvent && currentEvent.getSource() instanceof WashingProgram) {
            switch (((WaterEvent) currentEvent).getMode()) {
                case WaterEvent.WATER_IDLE:
                    currentAction = WaterEvent.WATER_IDLE;
                    machine.setDrain(false);
                    machine.setFill(false);
                    break;
                case WaterEvent.WATER_DRAIN:
                    currentAction = WaterEvent.WATER_DRAIN;
                    targetLevel = ((WaterEvent) currentEvent).getLevel();
                    machine.setFill(false);
                    machine.setDrain(true);
                    washingProgram = ((WashingProgram) currentEvent.getSource());
                    break;
                case WaterEvent.WATER_FILL:
                    currentAction = WaterEvent.WATER_FILL;
                    targetLevel = ((WaterEvent) currentEvent).getLevel();
                    washingProgram = ((WashingProgram) currentEvent.getSource());
                    if (machine.isLocked()) { machine.setFill(true); machine.setDrain(false); } else { System.err.println("Fill water command failed. Machine not locked."); }
            }
        }

        if (washingProgram != null && ((currentAction == WaterEvent.WATER_FILL && machine.getWaterLevel() >= targetLevel) || (currentAction == WaterEvent.WATER_DRAIN && machine.getWaterLevel() <= targetLevel))) {
            washingProgram.putEvent(new AckEvent(this));
            washingProgram = null;
        }
	}
}