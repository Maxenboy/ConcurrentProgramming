package todo;

import se.lth.cs.realtime.*;
import done.*;

public class WashingController implements ButtonListener {
	private AbstractWashingMachine theMachine;
	private double theSpeed;
	private RTThread thread;
	private WaterController waterController;
	private TemperatureController tempController;
	private SpinController spinController;

	public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
		this.theMachine = theMachine;
		this.theSpeed = theSpeed;
		waterController = new WaterController(theMachine, theSpeed);
		tempController = new TemperatureController(theMachine, theSpeed);
		spinController = new SpinController(theMachine, theSpeed);
		thread = new WashingProgram3(theMachine, theSpeed, tempController,
				waterController, spinController);
		waterController.start();
		tempController.start();
		spinController.start();
	}

	public void processButton(int theButton) {
		if (thread.isAlive()) {
			thread.interrupt();
		} else {
			switch (theButton) {

			case 0:
				thread = new WashingProgram0(theMachine, theSpeed,
						tempController, waterController, spinController);
				break;
			case 1:
				thread = new WashingProgram1(theMachine, theSpeed,
						tempController, waterController, spinController);
				break;
			case 2:
				thread = new WashingProgram2(theMachine, theSpeed,
						tempController, waterController, spinController);
				break;
			case 3:
				thread = new WashingProgram3(theMachine, theSpeed,
						tempController, waterController, spinController);
				break;
			}
			thread.start();

		}
	}
}
