package test;

import java.util.concurrent.Semaphore;

import se.lth.cs.realtime.semaphore.MutexSem;

public class A extends Thread{
	private Semaphore m1,m2,m3,m4;
	public A(){
		m1=new MutexSem();
	}
	public void perform(){
		
	}

}
