package lift;

public class Monitor {
	private int here, next, load, direction;
	private int[] waitEntry, waitExit;
	private LiftView liftV;

	public Monitor(LiftView liftV) {
		direction=1;
		here = 0;
		next = 0;
		load = 0;
		waitEntry = new int[7];
		waitExit = new int[]{-1,-1,-1,-1};
		this.liftV = liftV;
	}

	public synchronized int move() {
		if(here==6){
			direction=-1;
		}else if(here==0){
			direction=1;
		}
		next+=direction;
		return next;
	}

	public synchronized int nbrWait() {
		int tot = 0;
		for (int p : waitEntry) {
			tot += p;
		}
		return tot;
	}

	public synchronized int stop() {
		here = next;
		notifyAll();
		boolean exit = false;
		for (int floor : waitExit) {
			if (floor == here) {
				exit = true;
			}
		}
		while ((load < 1 && nbrWait() == 0)
				|| (waitEntry[here] > 0 && load != 4) || (exit)) {
			try {
				exit=false;
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return here;
	}

	public synchronized void call(int from, int to) {
		waitEntry[from]++;
		liftV.drawLevel(from, waitEntry[from]);
		notifyAll();

		while (here != next || here != from || load == 4) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int index = 0;
		while (waitExit[index] != -1) {
			index++;
		}
		waitExit[index] = to;
		load++;
		liftV.drawLift(here, load);
		waitEntry[from]--;
		liftV.drawLevel(here, waitEntry[from]);
		notifyAll();

		while (here != next || here != to) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		waitExit[index] = -1;
		load--;
		liftV.drawLift(here, load);
		notifyAll();
	}
	
}

