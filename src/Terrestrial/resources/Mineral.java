package Terrestrial.resources;

public class Mineral {
	private String[] names;
	private double[] albedos;
	private double[] phaseChanges;
	private double baseCost;
	private double density;
	private State stateART;
	private double surfaceProbability;
	private double relativeAmount;
	private boolean greenhouseGas;
	
	public Mineral(String[] names, double[] albedos, double[] phaseChanges, double density, double relativeAmount, double surfaceProbability, State stateART, boolean greenhouseGas){
		this.names = names;
		this.albedos = albedos;
		this.phaseChanges = phaseChanges;
		this.density = density;
		this.relativeAmount = relativeAmount;
		this.surfaceProbability = surfaceProbability;
		this.stateART = stateART;
		this.greenhouseGas = greenhouseGas;
		
		baseCost = 0;
	}
	
	public int getStateIndexAtTemp(double temperature){
		int ret = 0;
		if(temperature >= phaseChanges[0])
			ret = 1;
		if(temperature >= phaseChanges[1])
			ret = 2;
		return ret;
	}
	
	public String[] getNames(){
		return names;
	}
	
	public double[] getAlbedos(){
		return albedos;
	}
	
	public double[] getPhaseChanges(){
		return phaseChanges;
	}
	
	public double getBaseCost(){
		return baseCost;
	}
	
	public double getDensity(){
		return density;
	}
	
	public State getState(){
		return stateART;
	}
	
	public double getProbability(){
		return surfaceProbability;
	}
	
	public double getRelativeAmount(){
		return relativeAmount;
	}
	
	public boolean isGreenhouseGas(){
		return greenhouseGas;
	}
}
