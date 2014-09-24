package todo;
import done.AbstractWashingMachine;

public class WashingProgram1 extends WashingProgram {

	protected WashingProgram1(AbstractWashingMachine mach, double speed,
			TemperatureController tempController,
			WaterController waterController, SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	@Override
	protected void wash() throws InterruptedException {
		myMachine.setLock(true);
		//Draining
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
		mailbox.doFetch();
		
		//Filling
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
		mailbox.doFetch();
		
		//Turn of water filling
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0));
		
		//Heating up to 60
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 60));
		mailbox.doFetch();
		
		//Spin for 30 min
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
		long sleep=(long) (System.currentTimeMillis() + (30*60*1000/mySpeed));
		while(sleep>System.currentTimeMillis())
			sleep(sleep-System.currentTimeMillis());
		
		
		//Turn of spin and drain
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0));
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
		mailbox.doFetch();
		
		//stop drain and heating
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0));
		

		
		//rinse 5 times
		for (int i =0;i<5;i++){
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
			mailbox.doFetch();
			
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0));
			sleep =(long) (System.currentTimeMillis() + (2*60*1000/mySpeed));
			while(sleep>System.currentTimeMillis())
				sleep(sleep-System.currentTimeMillis());
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
			mailbox.doFetch();
			
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0));
		}
		
		//Centrifuge for 5 min
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
		sleep =(long) (System.currentTimeMillis() + (5*60*1000/mySpeed));
		while(sleep>System.currentTimeMillis())
			sleep(sleep-System.currentTimeMillis());
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		myMachine.setLock(false);
		
		
	}

}
