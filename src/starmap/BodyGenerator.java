package starmap;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import Terrestrial.resources.Mineral;
import init.InitializePlanetTypes;
import init.PlanetMineralProfile;
import main.Main;
import main.Util;

public class BodyGenerator {
	
	public static StellarObject generateEarthlike(String name, long minimumOrbit, StellarObject host, StarSystem system){		
		//Compute orbital velocity
		int orbitalVelocity = 0;
		
		//Choose a planet type
		PlanetSubType planetType = PlanetSubType.TERRESTRIAL;
		
		double typeIndex = Main.rand.nextDouble();
		
		for(int i = 0; i < PlanetSubType.values().length; i++){
			planetType = PlanetSubType.values()[i];
			if(typeIndex > PlanetSubType.odds[i])
				typeIndex -= PlanetSubType.odds[i];
			else
				break;
		}
		
		int numAsteroids = 0;
		
		if(planetType == PlanetSubType.ASTEROID_BELT){
			double beltMassRoll = Main.rand.nextInt(10) + 1;
			beltMassRoll += system.getRelativeAbundance();
			
			if(system.getAge() > 7)
				beltMassRoll -= 1;
			
			if(minimumOrbit >= Math.sqrt(host.getLuminosity()) * 4)
				beltMassRoll += 2;
			else
				beltMassRoll -= 1;
			
			double beltMass = 0;
			if(beltMassRoll <= 4)
				beltMass = ((double)(Main.rand.nextInt(10) + 1)) * 0.0001;
			else if(beltMassRoll >= 5 && beltMassRoll <= 6)
				beltMass = ((double)(Main.rand.nextInt(10) + 1)) * 0.001;
			else if(beltMassRoll >= 7 && beltMassRoll <= 8)
				beltMass = ((double)(Main.rand.nextInt(10) + 1)) * 0.01;
			else if(beltMassRoll >= 9 && beltMassRoll <= 10)
				beltMass = ((double)(Main.rand.nextInt(10) + 1)) * 0.1;
			else
				beltMass = ((double)(Main.rand.nextInt(10) + 1));
			beltMass *= Util.randomDouble(0.8, 1.2);
			numAsteroids = Math.min((int) Math.ceil((beltMass / 0.000218) * Util.randomDouble(0.75, 1.25)), 50);
		}
		
		for(int i = 0; i < numAsteroids; i++){
			StellarObject asteroid = new StellarObject("Asteroid", 1000, (long) (minimumOrbit * Util.randomDouble(0.8, 1.2)), 0, Util.randomDouble(0, 2 * Math.PI), Type.ASTEROID, PlanetSubType.CHUNK);
			system.addSpob(asteroid);
			host.getOrbitingBodies().add(asteroid);
		}
		
		//Generate an appropriate diameter
		double diameter = Util.randomDouble(PlanetSubType.minRadii[planetType.ordinal()], PlanetSubType.maxRadii[planetType.ordinal()]);
		//Compute the orbital distance relative to the body this object orbits
		//long orbitalDistance = (Main.rand.nextLong() % ((2L * minimumOrbit) - (long)(1.8 * minimumOrbit))) + (long)(1.8 * minimumOrbit);
		
		//long orbitalDistance = (2L * minimumOrbit) + (rand.nextLong() % (long)(1 + minimumOrbit / 10));
		double orbitalTheta = Main.rand.nextDouble() * 2 * Math.PI;
		
		//Body is rogue, orbit properties are zeroed out
		if(host == null){
			orbitalVelocity = 0;
			minimumOrbit = 0;
			//orbitalDistance = 0;
			orbitalTheta = 0;
		}
		
		//convert the diameter from Earth radii back into kilometers
		diameter *= Util.EARTH_RADIUS;
		
		//instantiate the new planet
		StellarObject ret = new StellarObject(name, (int)diameter, minimumOrbit, orbitalVelocity, orbitalTheta, Type.PLANET, PlanetSubType.values()[planetType.ordinal()]);
		
		PlanetMineralProfile desiredProfile = Main.getInitPlanetTypes().planetProfiles(planetType).get(Main.rand.nextInt(Main.getInitPlanetTypes().planetProfiles(planetType).size()));
		
		ret.setHost(host);
		ret.setAtmosphericPressure((Util.computeSurfaceVolume(ret) / Util.EARTH_SURFACE_VOLUME) * ((Main.rand.nextDouble() * desiredProfile.getAvgAtmosphericPressure()) + (desiredProfile.getAvgAtmosphericPressure() / 2)));
		//calculate the effective temperature
		ret.setAlbedo(0.6);
		ret.setEffectiveTemperature(Util.calculateTemperature(ret));
		
		generateOre(ret, desiredProfile.getSurfaceComponent(), desiredProfile.getAtmosphereComponent());
		
		double tempKelvin = Util.calculateTemperature(ret) + 273.15;
		
		//Aurora math - compute surface temperature accommodating for the greenhouse effect
		tempKelvin = tempKelvin * (1 + (ret.getAtmosphericPressure() / 10) + ret.computeGreenhousePressure());
		
		//ret.setSurfaceTemperature(tempKelvin - 273.15);
		
		ret.update();
		return ret;
	}
	
	public static void generateOre(StellarObject spob, HashMap<Mineral, double[]> planetType, HashMap<Mineral, double[]> atmosphereType){
		int surfaceMineralTotal = 0;
		double atmosphericMineralTotal = 0.0;
		double albedo = 0;
		double effectiveTemperature = spob.getEffectiveTemperature();
		
		for(Mineral m: Main.getInitMinerals()){
			if(spob.getEffectiveTemperature() < m.getPhaseChanges()[0] && Main.rand.nextInt(100) > 50){
				spob.setMineralDeposit(m, (int) (1000000 * spob.getDiameter() / Util.EARTH_RADIUS * Util.randomDouble(0.5, 1.5)));
			}
		}
		
		if(planetType != null){
			for(Mineral m: planetType.keySet()){
				//generate surface materials
				double[] surfaceProportion = planetType.get(m);
				
				int surfaceMineralAmt = (int) ((500000.0 * Main.rand.nextDouble() * (surfaceProportion[1] - surfaceProportion[0])) + (500000.0 * (surfaceProportion[0])));
				spob.setSurfaceMineralDeposit(m, (double)surfaceMineralAmt);
				surfaceMineralTotal += surfaceMineralAmt;
			}
		}
		else{
			for(Mineral m: Main.getInitMinerals()){
				if(Main.rand.nextInt(100) < m.getProbability()){
					int surfaceMineralAmt = Main.rand.nextInt((int)m.getRelativeAmount()) + (int)(m.getRelativeAmount() / 2);
					spob.setSurfaceMineralDeposit(m, (double)surfaceMineralAmt);
					surfaceMineralTotal += surfaceMineralAmt;
				}
			}
		}
		
		if(atmosphereType != null){
			for(Mineral m: atmosphereType.keySet()){
				double[] atmosphereProportion = atmosphereType.get(m);
				
				//generate atmosphere
				double atmAmt = ((Main.rand.nextDouble() * (atmosphereProportion[1] - atmosphereProportion[0])) + (atmosphereProportion[0]));
				atmosphericMineralTotal += atmAmt;
				spob.setAtmosphericGas(m, atmAmt);
			}
		}
		else{
			for(Mineral m: Main.getInitMinerals()){
				if(Main.rand.nextInt(100) < m.getProbability()){
					double atmAmt = (double)Main.rand.nextInt((int)m.getRelativeAmount()) + (int)(m.getRelativeAmount() / 2);
					atmosphericMineralTotal += atmAmt;
					spob.setAtmosphericGas(m, atmAmt);
				}
			}
		}
			
		for(Mineral m: spob.getSurfaceMinerals().keySet()){
			int phase = 0;
			double[] phaseChanges = m.getPhaseChanges();
			
			//System.out.println("Temperature: " + effectiveTemperature + " Melting point: " + phaseChanges[0] + " Boiling point: " + phaseChanges[1] + " ");
			//System.out.println("total minerals: " + surfaceMineralTotal);
			
			NumberFormat formatter = new DecimalFormat("#0.0000");
			double percentage = Double.parseDouble(formatter.format((spob.getSurfaceMinerals().get(m) / surfaceMineralTotal)));
			
			if(effectiveTemperature >= phaseChanges[0])
				phase = 1;
			if(effectiveTemperature >= phaseChanges[1])
				phase = 2;
			
			albedo += percentage * m.getAlbedos()[phase];
			
			spob.getSurfaceMinerals().put(m, percentage * (Util.computeSurfaceVolume(spob) / Util.EARTH_SURFACE_VOLUME));
			//System.out.println("*\t" + m.getNames()[phase] + " - " + (spob.getSurfaceMinerals().get(m) * 100) + "%");
		}
		
		for(Mineral m: spob.getAtmosphericMinerals().keySet()){
			NumberFormat formatter = new DecimalFormat("#0.0000");
			double percentage = Double.parseDouble(formatter.format((spob.getAtmosphericMinerals().get(m) / atmosphericMineralTotal)));
			
			spob.setAtmosphericGas(m, spob.getAtmosphericPressure() * percentage);
		}
		
		spob.setAlbedo(albedo);
		spob.setEffectiveTemperature(Util.calculateTemperature(spob));
	}
	
	public static StellarObject generateGeneric(String name, long minimumOrbit, StellarObject host){
		//Compute orbital velocity
		int orbitalVelocity = Main.rand.nextInt(100) + 5;
		//Choose a planet type
		int planetType = Main.rand.nextInt(PlanetSubType.values().length);
		//Generate an appropriate diameter
		double diameter = (Main.rand.nextDouble() * (PlanetSubType.maxRadii[planetType] - PlanetSubType.minRadii[planetType]) + PlanetSubType.minRadii[planetType]) * 2;
		
		//Compute the orbital distance relative to the body this object orbits
		long orbitalDistance = (Main.rand.nextLong() % ((2L * minimumOrbit) - (long)(1.8 * minimumOrbit))) + (long)(1.8 * minimumOrbit);
		
		//long orbitalDistance = (2L * minimumOrbit) + (rand.nextLong() % (long)(1 + minimumOrbit / 10));
		double orbitalTheta = Main.rand.nextDouble() * 2 * Math.PI;
		
		//Body is rogue, orbit properties are zeroed out
		if(host == null){
			orbitalVelocity = 0;
			minimumOrbit = 0;
			orbitalDistance = 0;
			orbitalTheta = 0;
		}
		
		//convert the diameter from Earth radii back into kilometers
		diameter *= Util.EARTH_RADIUS;
		
		//instantiate the new planet
		StellarObject ret = new StellarObject(name, (int)diameter, orbitalDistance, orbitalVelocity, orbitalTheta, Type.PLANET, PlanetSubType.values()[planetType]);
		ret.setHost(host);
		ret.setAtmosphericPressure(Math.abs(Main.rand.nextGaussian()*7 + 5));
		//calculate the effective temperature
		ret.setAlbedo(0.6);
		ret.setEffectiveTemperature(Util.calculateTemperature(ret));
		
		double tempKelvin = Util.calculateTemperature(ret) + 273.15;
		
		//Aurora math - compute surface temperature accomodating for the greenhouse effect
		tempKelvin = tempKelvin * (1 + (ret.getAtmosphericPressure() / 10));
		
		//ret.setSurfaceTemperature(tempKelvin - 273.15);
		
		ret.update();
		
		//print stats		
		System.out.println(name + " distance: " + orbitalDistance + " kilometers");
		System.out.println("\t" + (orbitalDistance / Util.AU) + " AU");
		System.out.println("Classification: " + planetType);
		System.out.println("effective temperature: " + ret.getEffectiveTemperature() + " C");
		System.out.println("albedo: " + ret.getAlbedo());
		System.out.println("atmospheric pressure: " + ret.getAtmosphericPressure());
		System.out.println("surface temperature: " + ret.getSurfaceTemperature() + " C");
		
		System.out.println("surface materials: ");
			for(Mineral m: ret.getSurfaceMinerals().keySet()){
				int phase = 0;
				double[] phaseChanges = m.getPhaseChanges();
				
				if(ret.getEffectiveTemperature() >= phaseChanges[0])
					phase = 1;
				if(ret.getEffectiveTemperature() >= phaseChanges[1])
					phase = 2;
				System.out.println("*\t" + m.getNames()[phase] + ": " + ret.getSurfaceMinerals().get(m) * 100 +"%");
			}

		System.out.println("atmospheric composition: ");
			for(Mineral m: ret.getAtmosphericMinerals().keySet()){
				int phase = 0;
				double[] phaseChanges = m.getPhaseChanges();
				
				if(ret.getEffectiveTemperature() >= phaseChanges[0])
					phase = 1;
				if(ret.getEffectiveTemperature() >= phaseChanges[1])
					phase = 2;
				System.out.println("*\t" + m.getNames()[phase] + ": " + ret.getAtmosphericMinerals().get(m) * 100 +"%");
			}			
			
		return ret;
	}
}

