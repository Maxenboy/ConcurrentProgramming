package lift;

public class Person extends Thread {
	private Monitor m;

	public Person(Monitor m) {
		this.m = m;
	}

	public int randomFloor() {
		return (int) (Math.random() * 7);
	}

	public void run() {
		while (true) {
			int from=randomFloor();
			int to=randomFloor();
			while(from==to){
				to=randomFloor();
			}
			try {
				Thread.sleep((int)(100*(Math.random()*46)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m.call(from, to);
			
		}
	}
}
