package main;

abstract class Steppable {
	int[] alarm;
	Main device;
	
	public Steppable(Main device){
		this.device = device;
		alarm = new int[10];
	}
	
	abstract void step();
	
	abstract void draw();
	
}
