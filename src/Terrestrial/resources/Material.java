package Terrestrial.resources;

public class Material {
	private String name;
	private double baseCost;
	private double density;
	private State stateART;
	
	public Material(String name, double baseCost, double density, State stateART){
		this.name = name;
		this.baseCost = baseCost;
		this.density = density;
		this.stateART = stateART;
	}
	
	public String getName(){
		return name;
	}
	
	public double getBaseCost(){
		return baseCost;
	}
	
	public double getDensity(){
		return density;
	}
}
