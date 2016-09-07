package Terrestrial.bodies;

import governments.Faction;
import main.Main;
import main.Util;
import processing.core.PImage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;

import Terrestrial.resources.Mineral;

public class StellarObject {
	private String name;
	private HashMap<Faction, Integer> population;
	private HashMap<Mineral, Integer> minerals;
	private HashMap<Mineral, Double> surfaceMinerals;
	private HashMap<Mineral, Double> atmosphericMinerals;

	private Faction affiliation;
	private int diameter;
	private Type type;
	private SpobSubType subType;
	private int sizeClass;
	
	private long x;
	private long y;
	
	private long orbitalDistance;
	private long periapsis;
	private long apoapsis;
	private double orbitalVelocity;
	private double orbitalTheta;
	private StellarObject host;
	private LinkedList<StellarObject> orbitingBodies;
	private double surfaceAlbedo;
	private double atmosphericPressure;
	private double luminosity;
	private double mass;
	private int spectralClass;
		
	private PImage graphic;
	private double colonyCost = 0;
	private double cloudCoverage = 0;
	
	private double effectiveTemperature;
	private double surfaceTemperature;
	private double surfaceVolume;
	
	private double titiusCoefficient;
	private double bodeConstant;
	
	//industry
	private int numShipyards;
	private int numFactories;
	private int numMines;
	
	public StellarObject(String name, int diameter, long orbitalDistance, double orbitalVelocity, double orbitalTheta, Type type, SpobSubType subType){
		this.name = name;
		this.diameter = diameter;
		this.orbitalDistance = orbitalDistance;
		this.apoapsis = orbitalDistance;
		this.periapsis = orbitalDistance;
		this.orbitalVelocity = orbitalVelocity;
		this.orbitalTheta = orbitalTheta;
		this.type = type;
		this.subType = subType;
		
		x = (int)(Math.cos(orbitalTheta) * (orbitalDistance / 100));
    	y = (int)(Math.sin(orbitalTheta) * (orbitalDistance / 100));
    	
    	orbitingBodies = new LinkedList<StellarObject>();
		population = new HashMap<Faction, Integer>();
    	minerals = new HashMap<Mineral, Integer>();
    	surfaceMinerals = new HashMap<Mineral, Double>();
    	atmosphericMinerals = new HashMap<Mineral, Double>();
    	surfaceAlbedo = 0.5;
    	luminosity = 0;
    	atmosphericPressure = 0;
    	effectiveTemperature = 0;
    	surfaceTemperature = 0;
    	titiusCoefficient = 0;
    	bodeConstant = 0;
    	sizeClass = 5;
    	spectralClass = 1;
    	mass = 1;
    	
    	surfaceVolume = (Util.computeSurfaceVolume(this) / Util.EARTH_SURFACE_VOLUME);
    	
    	if (subType == Terrestrial.bodies.PlanetSubType.DWARF || subType == Terrestrial.bodies.PlanetSubType.CHUNK)
    		graphic = Main.dwarfList[Main.rand.nextInt(Main.dwarfList.length)];
    	if (subType == Terrestrial.bodies.PlanetSubType.TERRESTRIAL)
    		graphic = Main.terrestrialList[Main.rand.nextInt(Main.terrestrialList.length)];
    	if (subType == Terrestrial.bodies.PlanetSubType.SUPER_TERRESTRIAL)
    		graphic = Main.superTerrestrialList[Main.rand.nextInt(Main.superTerrestrialList.length)];
    	
    	if (subType == Terrestrial.bodies.PlanetSubType.JOVIAN)
    		graphic = Main.gasGiantList[Main.rand.nextInt(Main.gasGiantList.length)];
    	if (subType == Terrestrial.bodies.PlanetSubType.SUPER_JOVIAN)
    		graphic = Main.superGasGiantList[Main.rand.nextInt(Main.superGasGiantList.length)];
    	if (type == Terrestrial.bodies.Type.ASTEROID || type == Terrestrial.bodies.Type.MOON)
    		graphic = Main.asteroidList[Main.rand.nextInt(Main.asteroidList.length)];

    	if (subType == Terrestrial.bodies.StarSubType.RED || subType == Terrestrial.bodies.StarSubType.RED_GIANT)
    		graphic = Main.redStarList[Main.rand.nextInt(Main.redStarList.length)];
    	if (subType == Terrestrial.bodies.StarSubType.ORANGE || subType == Terrestrial.bodies.StarSubType.ORANGE_SUB_GIANT || subType == Terrestrial.bodies.StarSubType.ORANGE_GIANT)
    		graphic = Main.orangeStarList[Main.rand.nextInt(Main.orangeStarList.length)];
    	if (subType == Terrestrial.bodies.StarSubType.YELLOW || subType == Terrestrial.bodies.StarSubType.YELLOW_SUB_GIANT || subType == Terrestrial.bodies.StarSubType.YELLOW_GIANT)
    		graphic = Main.yellowStarList[Main.rand.nextInt(Main.yellowStarList.length)];
    	if (subType == Terrestrial.bodies.StarSubType.WHITE || subType == Terrestrial.bodies.StarSubType.WHITE_SUB_GIANT || subType == Terrestrial.bodies.StarSubType.WHITE_GIANT)
    		graphic = Main.whiteStarList[Main.rand.nextInt(Main.whiteStarList.length)];
    	if (subType == Terrestrial.bodies.StarSubType.BLUE_WHITE || subType == Terrestrial.bodies.StarSubType.BLUE_WHITE_SUB_GIANT || subType == Terrestrial.bodies.StarSubType.BLUE_WHITE_GIANT)
    		graphic = Main.blueWhiteStarList[Main.rand.nextInt(Main.blueWhiteStarList.length)];
    	}
	
	public void step(Main main){
		//orbitalTheta += Math.toRadians(0.05);

		for(StellarObject s: orbitingBodies)
			s.step(main);
	}
	
	public void draw(Main main){
		//if((int)(x - Main.viewXView) < -20000 || (int)(y - Main.viewYView) < -20000 || (int)(x - Main.viewXView) > Main.WIN_WIDTH + 20000 || (int)(y - Main.viewYView) > Main.WIN_HEIGHT + 20000)
		if(!Main.getCurrentSystem().getSpobs().contains(this))
			return;
		if(Util.pointDistance(x, y, Main.getPlayerShip().getX(), Main.getPlayerShip().getY()) >= 25000.0)
			return;
		
		main.image(graphic,(int)(x - Main.viewXView) - (graphic.width / 2), (int)(y - Main.viewYView) - (graphic.height / 2));

    	//check if the current planet is the one selected by the player ship and draw the target borders
    	if(Main.getCurrentSystem().getSpobs().get(Main.getPlayerShip().getTargetIndex()) == this){
    		main.image(Main.targetBorder[0], x - Main.viewXView - (graphic.width / 2), y - Main.viewYView - (graphic.height / 2));
    		main.image(Main.targetBorder[1], x - Main.viewXView + (graphic.width / 2), y - Main.viewYView - (graphic.height / 2));
    		main.image(Main.targetBorder[2], x - Main.viewXView - (graphic.width / 2), y - Main.viewYView + (graphic.height / 2));
    		main.image(Main.targetBorder[3], x - Main.viewXView + (graphic.width / 2), y - Main.viewYView + (graphic.height / 2));
    	}
	}
	
	public void update(){
		double surfaceTotal = 0;
		double atmosphericTotal = 0;
		
		double atmTotal = 0;
		for(double atm: atmosphericMinerals.values())
			atmTotal += atm;
		
		System.out.println("Pre update: " + name + " has " + atmTotal + " ATMs of atmospheric gases and a total pressure of " + atmosphericPressure + " ATMs");
		
		
		LinkedList<Mineral> keyDelete = new LinkedList<Mineral>();
		HashMap<Mineral, Double> newGaseousMaterials = new HashMap<Mineral, Double>();
		HashMap<Mineral, Double> newSolidMaterials = new HashMap<Mineral, Double>();
		
		
		effectiveTemperature = Util.calculateTemperature(0.0, host.getLuminosity(), orbitalDistance);
		surfaceTemperature = ((effectiveTemperature + 273.15) * computeGreenhouseFactor()) - 273.15;
		
		surfaceAlbedo = 0.0;
		colonyCost = 0.0;
		
		//check if surface materials boil at the current surface temperature
		for(Mineral m: surfaceMinerals.keySet()){
			if(surfaceTemperature >= m.getPhaseChanges()[1]){
				//atmosphericMinerals.put(m, surfaceMinerals.get(m));
				newGaseousMaterials.put(m, surfaceMinerals.get(m));
				//atmosphericPressure += surfaceMinerals.get(m);
				//surfaceVolume -= surfaceMinerals.get(m);
				
				//atmosphericTotal += surfaceMinerals.get(m);
				keyDelete.add(m);
			}
		}
		for(Mineral m: keyDelete)
			surfaceMinerals.remove(m);
		
		keyDelete.clear();
		
		//check if atmospheric materials freeze at the current surface temperature
		for(Mineral m: atmosphericMinerals.keySet()){
			if(surfaceTemperature < m.getPhaseChanges()[1]){
				//surfaceMinerals.put(m, atmosphericMinerals.get(m));
				newSolidMaterials.put(m, atmosphericMinerals.get(m));
				//surfaceVolume += atmosphericMinerals.get(m);
				//atmosphericPressure -= atmosphericMinerals.get(m);
			
				//surfaceTotal += atmosphericMinerals.get(m);
				keyDelete.add(m);
			}
		}
		for(Mineral m: keyDelete)
			atmosphericMinerals.remove(m);
		keyDelete.clear();
		
		for(Mineral m: newGaseousMaterials.keySet()){
			atmosphericMinerals.put(m, newGaseousMaterials.get(m));
			atmosphericPressure += newGaseousMaterials.get(m);
			surfaceVolume -= newGaseousMaterials.get(m);
		}
		
		for(Mineral m: newSolidMaterials.keySet()){
			surfaceMinerals.put(m, newSolidMaterials.get(m));
			surfaceVolume += newSolidMaterials.get(m);
			atmosphericPressure -= newSolidMaterials.get(m);
			System.out.println(" * removing " + newSolidMaterials.get(m) + " ATMs of " + m.getNames()[2] + " from the atmosphere of " + name);
		}
		
		atmTotal = 0;
		for(double atm: atmosphericMinerals.values())
			atmTotal += atm;
		
		System.out.println("Post update: " + name + " has " + atmTotal + " ATMs of atmospheric gases and a total pressure of " + atmosphericPressure + " ATMs");

		//strange things happen when atmospheric pressure drops below zero, use a low threshold to prevent negligible rounding errors from crashing the game
		if(atmosphericPressure < 0.0){
			System.out.println("WARNING: " + name + " has a negative atmospheric pressure!\nHas " + atmosphericPressure + " ATMs when " + atmTotal + " ATMs are needed!");
			atmosphericPressure = 0.0;
		}
		//update the proper phase and albedo for the updated surface and atmospheric materials
		surfaceAlbedo = 0;
		surfaceTotal = 0;
		atmosphericTotal = 0;
		
		for(Mineral m: surfaceMinerals.keySet())
			surfaceTotal += surfaceMinerals.get(m);
			
		for(Mineral m: atmosphericMinerals.keySet())
			atmosphericTotal += atmosphericMinerals.get(m);
		
		//set atmospheric albedo (i.e. simulate clouds reflecting light back into space)
		for(Mineral m: atmosphericMinerals.keySet()){
			surfaceAlbedo += (cloudCoverage * ((atmosphericMinerals.get(m) / atmosphericTotal) * m.getAlbedos()[2]));
		}
		
		//now the surface can only reflect the portion that made it through the cloud layer
		double availableLight = 1.0 - surfaceAlbedo;
		
		//set surface albedo (this will reflect a portion of the light that isn't reflected by the atmosphere)
		for(Mineral m: surfaceMinerals.keySet()){
			int phase = 0;
			if(surfaceTemperature >= m.getPhaseChanges()[0])
				phase = 1;
			if(surfaceTemperature >= m.getPhaseChanges()[1])
				phase = 2;
			
			surfaceAlbedo += (availableLight * ((surfaceMinerals.get(m) / surfaceTotal) * m.getAlbedos()[phase]));
		}
		
		//for(Mineral m: atmosphericMinerals.keySet())
		//	atmosphericMinerals.put(m, atmosphericMinerals.get(m) / atmosphericTotal);
		
		this.effectiveTemperature = Util.calculateTemperature(this);
		this.surfaceTemperature = ((this.effectiveTemperature + 273.15) * computeGreenhouseFactor()) - 273.15;

		double atmCC = 0.0;
		double pressureCC = 0.0;
		double tempCC = 0.0;
		double oxygenContent = (atmosphericMinerals.get(Main.getInitMinerals().oxygen) != null) ? atmosphericMinerals.get(Main.getInitMinerals().oxygen) : 0.0;
		
		if(surfaceTemperature < Main.MEAN_TEMP - Main.TEMP_DEVIATION)
			tempCC = ((Main.MEAN_TEMP - Main.TEMP_DEVIATION) - surfaceTemperature) / Main.TEMP_DEVIATION;
		else if(surfaceTemperature > Main.MEAN_TEMP + Main.TEMP_DEVIATION)
			tempCC = (surfaceTemperature - (Main.MEAN_TEMP + Main.TEMP_DEVIATION)) / Main.TEMP_DEVIATION;	
			
		if(atmosphericPressure > Main.MAX_PRESSURE)
			pressureCC = (atmosphericPressure / Main.MAX_PRESSURE);

		if(oxygenContent < Main.MEAN_OXYGEN - Main.OXYGEN_DEVIATION || oxygenContent > Main.MEAN_OXYGEN + Main.OXYGEN_DEVIATION)
			atmCC = 2.0;
		
		colonyCost = Math.max(atmCC, Math.max(pressureCC, tempCC));
		
		if(subType != PlanetSubType.TERRESTRIAL && subType != PlanetSubType.SUPER_TERRESTRIAL && subType != PlanetSubType.DWARF)
			colonyCost = 900;
	}
	
	public void setSizeClass(int size){
		sizeClass = size;
	}
	
	public void setSpectralClass(int spectralClass){
		this.spectralClass = spectralClass;
	}
	
	public int getSpectralClass(){
		return spectralClass;
	}
	
	public void setMass(double mass){
		this.mass = mass;
	}
	
	public double getMass(){
		return mass;
	}
	
	public double computeGreenhouseFactor(){
		double greenhouseFactor = (1 + (atmosphericPressure / 10)) + computeGreenhousePressure();
		return Math.min(Main.MAX_GREENHOUSE_FACTOR, greenhouseFactor);
	}
	
	public double computeGreenhousePressure(){
		double greenhousePressure = 0;
		double atmTotal = 0;
		
		for(double m: atmosphericMinerals.values())
			atmTotal += m;
		
		for(Mineral m: atmosphericMinerals.keySet()){
			if(m.isGreenhouseGas())
				greenhousePressure += ((atmosphericMinerals.get(m) / atmTotal) * atmosphericPressure);
		}
		//System.out.println(atmTotal);
		return greenhousePressure;
	}
	
	public void moveToOrbit(){
		x = (int)(Math.cos(orbitalTheta) * (orbitalDistance / 100));
    	y = (int)(Math.sin(orbitalTheta) * (orbitalDistance / 100));
    	for(StellarObject spob: orbitingBodies)
    		spob.setHost(this);
	}
	
	public double getColonyCost(){
		return colonyCost;
	}
	
	public void setCloudCoverage(double cloudCoverage){
		this.cloudCoverage = cloudCoverage;
	}
	
	public double getCloudCoverage(){
		return cloudCoverage;
	}

	public void setPeriapsis(long periapsis){
		this.periapsis = periapsis;
	}
	
	public void setApoapsis(long apoapsis){
		this.apoapsis = apoapsis;
	}
	
	public long getPeriapsis(){
		return periapsis;
	}
	
	public long getApoapsis(){
		return apoapsis;
	}
	
	public void setMineralDeposit(Mineral type, int amount){
		minerals.put(type, amount);
	}
	
	public void setSurfaceMineralDeposit(Mineral type, double amount){
		surfaceMinerals.put(type, amount);
	}
	
	public void setAtmosphericGas(Mineral type, double amount){
		atmosphericMinerals.put(type, amount);
	}
	
	public void setHost(StellarObject host){
		this.host = host;

		if(host == null)
			return;
		
		x = (long) ((Math.cos(orbitalTheta) * (orbitalDistance / 100)) + host.getX());
    	y = (long) ((Math.sin(orbitalTheta) * (orbitalDistance / 100)) + host.getY());
	}
	
	public LinkedList<StellarObject> getOrbitingBodies(){
		return orbitingBodies;
	}
	
	public void setLuminosity(double luminosity){
		this.luminosity = luminosity;
		//titiusCoefficient = Main.TITIUS_COEFFICIENT * luminosity * Util.randomDouble(0.5, 1.5);
		//bodeConstant = Main.BODE_CONSTANT * luminosity * Util.randomDouble(0.5, 1.5);
	}
	
	public void setAlbedo(double albedo){
		surfaceAlbedo = albedo;
	}
	
	public void setAtmosphericPressure(double pressure){
		this.atmosphericPressure = pressure;
	}
	
	public void setEffectiveTemperature(double effectiveTemperature){
		this.effectiveTemperature = effectiveTemperature;
	}
	
	public void setSurfaceTemperature(double surfaceTemperature){
		this.surfaceTemperature = surfaceTemperature;
	}
	
	public void setOrbitalDistance(long orbitalDistance){
		this.orbitalDistance = orbitalDistance;
	}
	
	public double getTitiusCoefficient(){
		return titiusCoefficient;
	}

	public double getBodeConstant(){
		return bodeConstant;
	}	
	
	public double getEffectiveTemperature(){
		return effectiveTemperature;
	}
	
	public double getSurfaceTemperature(){
		return surfaceTemperature;
	}
	
	public double getAtmosphericPressure(){
		return atmosphericPressure;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public double getLuminosity(){
		return luminosity;
	}
	
	public double getAlbedo(){
		return surfaceAlbedo;
	}
	
	public HashMap<Faction, Integer> getPopulation(){
		return population;
	}
	
	public HashMap<Mineral, Integer> getMinerals(){
		return minerals;
	}
	
	public HashMap<Mineral, Double> getSurfaceMinerals(){
		return surfaceMinerals;
	}
	
	public HashMap<Mineral, Double> getAtmosphericMinerals(){
		return atmosphericMinerals;
	}
	
	public Faction getAffiliation(){
		return affiliation;
	}
	
	public int getDiameter(){
		return diameter;
	}
	
	public Type getType(){
		return type;
	}
	
	public SpobSubType getSubType(){
		return subType;
	}
	
	public long getOrbitalDistance(){
		return orbitalDistance;
	}
	
	public double getOrbitalTheta(){
		return orbitalTheta;
	}
	
	public double getOrbitalVelocity(){
		return orbitalVelocity;
	}
	
	public StellarObject getHost(){
		return host;
	}
	
	public long getX(){
		return x;
	}
	
	public long getY(){
		return y;
	}

	public void setX(long x){
		this.x = x;
	}

	public void setY(long y){
		this.y = y;
	}
	
	public PImage getGraphicIndex(){
		return graphic;
	}
}
