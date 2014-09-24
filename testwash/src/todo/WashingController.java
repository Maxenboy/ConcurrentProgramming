package todo;

import done.*;
import se.lth.cs.realtime.RTThread;

public class WashingController implements ButtonListener {	
    private AbstractWashingMachine machine;
    private double speed;
    private SpinController spinController;
    private TemperatureController temperatureController;
    private WaterController waterController;
    private RTThread currentThread = new WashingProgram3(machine, speed, temperatureController, waterController, spinController);

    public WashingController(AbstractWashingMachine machine, double speed) {
        this.machine = machine;
        this.speed = speed;

        spinController = new SpinController(this.machine, this.speed);
        waterController = new WaterController(this.machine, this.speed);
        temperatureController = new TemperatureController(this.machine, this.speed);

        spinController.start();
        waterController.start();
        temperatureController.start();
    }

    public void processButton(int programButton) {
        if (programButton == 0 && currentThread.isAlive()) {
            currentThread.interrupt();
        }

        if (!currentThread.isAlive()) {
            switch (programButton)  {
                case 0:
                    currentThread = new WashingProgram0(machine, speed, temperatureController, waterController, spinController);
                    break;
                case 1:
                    currentThread = new WashingProgram1(machine, speed, temperatureController, waterController, spinController);
                    break;
                case 2:
                    currentThread = new WashingProgram2(machine, speed, temperatureController, waterController, spinController);
                    break;
                case 3:
                    currentThread = new WashingProgram3(machine, speed, temperatureController, waterController, spinController);
                    break;
            }

            currentThread.start();
        }
    }
}