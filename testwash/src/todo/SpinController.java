package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;
import se.lth.cs.realtime.event.RTEvent;

public class SpinController extends PeriodicThread {
    private RTEvent currentEvent;
    private AbstractWashingMachine machine;
    private long lastCall = System.currentTimeMillis();
    private int
            currentAction = SpinEvent.SPIN_OFF,
            directionChangeDelay = 60000,
            spinDirectionTimer = 0,
            spinDirection = AbstractWashingMachine.SPIN_LEFT;

	public SpinController(AbstractWashingMachine machine, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
        this.machine = machine;
        directionChangeDelay = (int) (directionChangeDelay / speed);
	}

	public void perform() {
        if ((currentEvent = mailbox.tryFetch()) != null && currentEvent instanceof SpinEvent && currentEvent.getSource() instanceof WashingProgram) {
            switch (((SpinEvent) currentEvent).getMode()) {
                case SpinEvent.SPIN_OFF:
                    currentAction = SpinEvent.SPIN_OFF;
                    machine.setSpin(AbstractWashingMachine.SPIN_OFF);
                    break;
                case SpinEvent.SPIN_SLOW:
                    currentAction = SpinEvent.SPIN_SLOW;
                    spinDirection = AbstractWashingMachine.SPIN_LEFT;
                    machine.setSpin(spinDirection);
                    lastCall = System.currentTimeMillis();
                    spinDirectionTimer = 0;
                    break;
                case SpinEvent.SPIN_FAST:
                    currentAction = SpinEvent.SPIN_FAST;
                    break;
            }
        }

        if (currentAction == SpinEvent.SPIN_SLOW && (spinDirectionTimer += System.currentTimeMillis() - lastCall) >= directionChangeDelay && machine.isLocked()) {
            spinDirectionTimer -= directionChangeDelay;
            spinDirection = (spinDirection == AbstractWashingMachine.SPIN_LEFT) ? AbstractWashingMachine.SPIN_RIGHT : AbstractWashingMachine.SPIN_LEFT;
            machine.setSpin(spinDirection);
            lastCall = System.currentTimeMillis();
        } else if (currentAction == SpinEvent.SPIN_FAST && machine.getWaterLevel() <= 0d && machine.isLocked()) {
            machine.setSpin(AbstractWashingMachine.SPIN_FAST);
            currentAction = -1;
        }
	}
}