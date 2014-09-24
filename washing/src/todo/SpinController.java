package todo;

import se.lth.cs.realtime.*;
import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class SpinController extends PeriodicThread {
	private RTEvent event;
	private AbstractWashingMachine machine;
	private int action, spinDirection; 
	private long period;
	private long spinTime, timer;

	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		machine = mach;
		action = SpinEvent.SPIN_OFF;
		spinDirection =0;
		spinTime = 0;
		period = (long) (60000/speed);
		timer = 0;
	}

	public void perform() {
		if ((event = mailbox.tryFetch()) != null && event instanceof SpinEvent) {
			switch (((SpinEvent) event).getMode()) {
			case SpinEvent.SPIN_OFF:
				action = SpinEvent.SPIN_OFF;
				machine.setSpin(AbstractWashingMachine.SPIN_OFF);
				break;
			case SpinEvent.SPIN_SLOW:
				action = SpinEvent.SPIN_SLOW;
				spinDirection = AbstractWashingMachine.SPIN_LEFT;
				machine.setSpin(spinDirection);
				spinTime = System.currentTimeMillis();
				timer = spinTime + period;
				break;
			case SpinEvent.SPIN_FAST:
				action = SpinEvent.SPIN_FAST;
				machine.setSpin(AbstractWashingMachine.SPIN_FAST);
				break;
			}
		}

		if (action == SpinEvent.SPIN_SLOW
				&& System.currentTimeMillis() >= timer) {
			if (spinDirection == AbstractWashingMachine.SPIN_LEFT) {
				spinDirection = AbstractWashingMachine.SPIN_RIGHT;
			} else {
				spinDirection = AbstractWashingMachine.SPIN_LEFT;
			}
			machine.setSpin(spinDirection);
			timer += period;
		}
	}
}
