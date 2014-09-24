package lift;

public class Lift extends Thread {
	private Monitor m;
	private LiftView liftV;

	public Lift(Monitor m, LiftView liftV) {
		this.liftV = liftV;
		this.m = m;
	}
	public void run(){
		int here =m.stop();
		while(true){
			liftV.moveLift(here, m.move());
			here=m.stop();
		}
	}
}
