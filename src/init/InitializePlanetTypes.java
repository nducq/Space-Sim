package init;

import java.util.HashMap;
import java.util.LinkedList;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.resources.Mineral;

public class InitializePlanetTypes {
	/*
	 * Terrestrial Body Category:
	 * - Earthlike
	 * - Desert (low water)
	 * - Ferrous Surface (high iron)
	 * - Water World
	 * - Venusion
	 */
	public HashMap<Mineral, double[]> waterWorld;
	public HashMap<Mineral, double[]> earthlikeMinerals;
	public HashMap<Mineral, double[]> desertMinerals;
	public HashMap<Mineral, double[]> ironSurfaceMinerals;
	public HashMap<Mineral, double[]> barrenSurfaceMinerals;
	
	public HashMap<Mineral, double[]> earthlikeAtmosphere;	
	public HashMap<Mineral, double[]> barrenAtmosphere;
	public HashMap<Mineral, double[]> marslikeAtmosphere;
	public HashMap<Mineral, double[]> venusianAtmosphere;
	
	public LinkedList<PlanetMineralProfile> terrestrialPlanetProfiles;
	
	public InitializePlanetTypes(){
		InitializePlanetComponents(main.Main.getInitMinerals());
		InitializeProfiles();
	}
	
	private void InitializeProfiles(){
		terrestrialPlanetProfiles = new LinkedList<PlanetMineralProfile>();
		
		//"M - Class"
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(waterWorld, earthlikeAtmosphere, null, PlanetSubType.TERRESTRIAL, 1.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(earthlikeMinerals, earthlikeAtmosphere, null, PlanetSubType.TERRESTRIAL, 1.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(desertMinerals, earthlikeAtmosphere, null, PlanetSubType.TERRESTRIAL, 1.0));
		//"Iron Surface" (terrestrial sized marslike composition)
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(ironSurfaceMinerals, earthlikeAtmosphere, null, PlanetSubType.TERRESTRIAL, 1.2));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(ironSurfaceMinerals, barrenAtmosphere, null, PlanetSubType.TERRESTRIAL, 0.8));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(ironSurfaceMinerals, marslikeAtmosphere, null, PlanetSubType.TERRESTRIAL, 0.05));
		//"Barren"
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(ironSurfaceMinerals, null, null, PlanetSubType.TERRESTRIAL, 1.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(barrenSurfaceMinerals, barrenAtmosphere, null, PlanetSubType.TERRESTRIAL, 1.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(barrenSurfaceMinerals, venusianAtmosphere, null, PlanetSubType.TERRESTRIAL, 50.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(null, null, null, PlanetSubType.TERRESTRIAL, 6.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(null, null, null, PlanetSubType.TERRESTRIAL, 1.0));
		terrestrialPlanetProfiles.add(new PlanetMineralProfile(null, null, null, PlanetSubType.TERRESTRIAL, 0.1));
	}
	
	private void InitializePlanetComponents(InitializeMinerals initMin){
		//surface minerals
	
		waterWorld = new HashMap<Mineral, double[]>();
		earthlikeMinerals = new HashMap<Mineral, double[]>();
		desertMinerals = new HashMap<Mineral, double[]>();
		ironSurfaceMinerals = new HashMap<Mineral, double[]>();
		barrenSurfaceMinerals = new HashMap<Mineral, double[]>();
		
		waterWorld.put(initMin.water, new double[]{0.95, 0.99});
		waterWorld.put(initMin.silica, new double[]{0.01, 0.05});
		
		earthlikeMinerals.put(initMin.water, new double[]{0.50, 0.85});
		earthlikeMinerals.put(initMin.silica, new double[]{0.10, 0.25});
		
		desertMinerals.put(initMin.water, new double[]{0.10, 0.25});
		desertMinerals.put(initMin.silica, new double[]{0.50, 0.85});
		desertMinerals.put(initMin.copperOre, new double[]{0.0, 0.05});
		desertMinerals.put(initMin.ironOre, new double[]{0.0, 0.05});
		
		ironSurfaceMinerals.put(initMin.ironOre, new double[]{0.50, 0.75});
		ironSurfaceMinerals.put(initMin.copperOre, new double[]{0.10, 0.15});
		ironSurfaceMinerals.put(initMin.silica, new double[]{0.15, 0.25});
		ironSurfaceMinerals.put(initMin.water, new double[]{0.05, 0.10});
		
		barrenSurfaceMinerals.put(initMin.ironOre, new double[]{0.35, 0.50});
		barrenSurfaceMinerals.put(initMin.leadOre, new double[]{0.0, 0.10});
		barrenSurfaceMinerals.put(initMin.aluminumOre, new double[]{0.0, 0.10});
		barrenSurfaceMinerals.put(initMin.silica, new double[]{0.45, 0.50});
		barrenSurfaceMinerals.put(initMin.copperOre, new double[]{0.0, 0.25});
		barrenSurfaceMinerals.put(initMin.titaniumOre, new double[]{0.0, 0.05});
		
		//atmospheres
		
		earthlikeAtmosphere = new HashMap<Mineral, double[]>();
		barrenAtmosphere = new HashMap<Mineral, double[]>();
		marslikeAtmosphere = new HashMap<Mineral, double[]>();
		venusianAtmosphere = new HashMap<Mineral, double[]>();
		
		earthlikeAtmosphere.put(initMin.oxygen, new double[]{0.10, 0.30});
		earthlikeAtmosphere.put(initMin.carbonDioxide, new double[]{0.0, 0.01});
		earthlikeAtmosphere.put(initMin.nitrogen, new double[]{0.50, 0.80});
		earthlikeAtmosphere.put(initMin.argon, new double[]{0.005, 0.015});
		
		barrenAtmosphere.put(initMin.argon, new double[]{0.0, 0.05});
		barrenAtmosphere.put(initMin.hydrogen, new double[]{0.0, 0.25});
		barrenAtmosphere.put(initMin.chlorine, new double[]{0.0, 0.15});
		barrenAtmosphere.put(initMin.oxygen, new double[]{0.0, 0.50});
		barrenAtmosphere.put(initMin.nitrogen, new double[]{0.0, 0.85});
		barrenAtmosphere.put(initMin.carbonDioxide, new double[]{0.0, 0.95});
		
		marslikeAtmosphere.put(initMin.carbonDioxide, new double[]{0.90, 0.95});
		marslikeAtmosphere.put(initMin.argon, new double[]{0.025, 0.05});
		marslikeAtmosphere.put(initMin.oxygen, new double[]{0.0, 0.05});
		marslikeAtmosphere.put(initMin.nitrogen, new double[]{0.0, 0.05});
		
		venusianAtmosphere.put(initMin.carbonDioxide, new double[]{0.80, 0.95});
		venusianAtmosphere.put(initMin.nitrogen, new double[]{0.01, 0.05});
		venusianAtmosphere.put(initMin.argon, new double[]{0.0, 0.01});
	}
	
	public LinkedList<PlanetMineralProfile> planetProfiles(PlanetSubType type){
		switch(type){
			case SUPER_TERRESTRIAL:
			case TERRESTRIAL:
				return terrestrialPlanetProfiles;
			default:
				return terrestrialPlanetProfiles;
		}
	}
}
