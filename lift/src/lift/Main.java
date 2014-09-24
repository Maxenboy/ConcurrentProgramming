package lift;

public class Main {

	public static void main(String[] args) {
		Person[] persons = new Person[30];
		LiftView liftV = new LiftView();
		Monitor monitor = new Monitor(liftV);
		for (Person p : persons) {
			p = new Person(monitor);
			p.start();
		}
		Lift lift = new Lift(monitor, liftV);
		lift.start();

	}

}
