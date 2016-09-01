package init;

import java.util.HashMap;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.resources.Mineral;

public class PlanetMineralProfile {
	private HashMap<Mineral, double[]> surfaceComponent;
	private HashMap<Mineral, double[]> atmosphereComponent;
	private HashMap<Mineral, double[]> lodeComponent;
	private PlanetSubType applicableType;
	private double avgAtmosphericPressure;
	
	public PlanetMineralProfile(HashMap<Mineral, double[]> surfaceComponent, HashMap<Mineral, double[]> atmosphereComponent, HashMap<Mineral, double[]> lodeComponent, PlanetSubType applicableType, double avgAtmosphericPressure){
		this.surfaceComponent = surfaceComponent;
		this.atmosphereComponent = atmosphereComponent;
		this.lodeComponent = lodeComponent;
		this.applicableType = applicableType;
		this.avgAtmosphericPressure = avgAtmosphericPressure;
	}
	
	public double getAvgAtmosphericPressure(){
		return avgAtmosphericPressure;
	}
	
	public HashMap<Mineral, double[]> getSurfaceComponent(){
		return surfaceComponent;
	}
	
	public HashMap<Mineral, double[]> getAtmosphereComponent(){
		return atmosphereComponent;
	}
	
	public HashMap<Mineral, double[]> getLodeComponent(){
		return lodeComponent;
	}
	
	public PlanetSubType getApplicableType(){
		return applicableType;
	}
}
