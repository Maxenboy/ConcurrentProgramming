/*
 * Real-time and concurrent programming course, laboratory 3
 * Department of Computer Science, Lund Institute of Technology
 *
 * PP 980812 Created
 * PP 990924 Revised
 */

package todo;

import done.AbstractWashingMachine;

/**
 * Program 3 of washing machine. Does the following:
 * <UL>
 *   <LI>Switches off heating
 *   <LI>Switches off spin
 *   <LI>Pumps out water
 *   <LI>Unlocks the hatch.
 * </UL>
 */
class WashingProgram2 extends WashingProgram {

	// ------------------------------------------------------------- CONSTRUCTOR

	/**
	 * @param   mach             The washing machine to control
	 * @param   speed            Simulation speed
	 * @param   tempController   The TemperatureController to use
	 * @param   waterController  The WaterController to use
	 * @param   spinController   The SpinController to use
	 */
	public WashingProgram2(AbstractWashingMachine mach, double speed, TemperatureController tempController, WaterController waterController, SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	// ---------------------------------------------------------- PUBLIC METHODS

	/**
	 * This method contains the actual code for the washing program. Executed
	 * when the start() method is called.
	 */
	protected void wash() throws InterruptedException {
		myMachine.setLock(true);
        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0d));
        mailbox.doFetch();

        // Pre-wash
        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5d));
        mailbox.doFetch();

        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));
        myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 40d));
        mailbox.doFetch();

        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
        long sleepTime = System.currentTimeMillis () + (long) (15d * 60d * 1000d / mySpeed);
        while (sleepTime > System.currentTimeMillis()) { sleep(sleepTime - System.currentTimeMillis()); }

        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
        myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0d));
        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0d));
        mailbox.doFetch();

        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));

        // Main wash
        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5d));
        mailbox.doFetch();

        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));
        myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 90d));
        mailbox.doFetch();

        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
        sleepTime = System.currentTimeMillis () + (long) (30d * 60d * 1000d / mySpeed);
        while (sleepTime > System.currentTimeMillis()) { sleep(sleepTime - System.currentTimeMillis()); }

        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
        myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0d));
        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0d));
        mailbox.doFetch();

        myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));

        for (int i = 0; i < 5; i++) {
            myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.8d));
            mailbox.doFetch();

            myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));
            sleepTime = System.currentTimeMillis () + (long) (2d * 60d * 1000d / mySpeed);
            while (sleepTime > System.currentTimeMillis()) { sleep(sleepTime - System.currentTimeMillis()); }

            myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0d));
            mailbox.doFetch();

            myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0d));
        }

        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
        sleepTime = System.currentTimeMillis () + (long) (2d * 60d * 1000d / mySpeed);
        while (sleepTime > System.currentTimeMillis()) { sleep(sleepTime - System.currentTimeMillis()); }
        mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		myMachine.setLock(false);
	}
}