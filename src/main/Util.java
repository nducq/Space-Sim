package main;

import java.util.LinkedList;
import java.util.List;
//import java.util.LinkedList;
import java.util.Random;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import starmap.StarSystem;

public class Util {
	public static final double AU = 150000000.0;
	public static final double SOLAR_LUMINOSITY = 3.846 * Math.pow(10.0, 26.0);
	public static final double SOLAR_RADIUS = 695700.0;
	public static final double SOLAR_TEMPERATURE = 5778;
	
	public static final double MIN_SOLAR_HABITABLE_ZONE = 0.998;
	public static final double MAX_SOLAR_HABITABLE_ZONE = 1.688;
	
	public static final double BOLTZMANN_CONSTANT = 5.6703 * Math.pow(10.0, -8.0);
	
	public static final double SOLAR_LUMEN_MAGIC_NUMBER = 17270.0146221;
	
	public static final double EARTH_RADIUS = 6371.0;
	public static final double EARTH_VOLUME = (4.0 / 3.0) * Math.PI * Math.pow(EARTH_RADIUS, 3);
	//This the volume of a kilometer thick "shell" with an apparent radius of that of the Earth
	
	//This value will be used to convert surface materials to atmospheric materials 1 ESV of a substance is equivalent to 1 ATM
	public static final double EARTH_SURFACE_VOLUME = EARTH_VOLUME - ((4.0 / 3.0) * Math.PI * Math.pow(EARTH_RADIUS - 1, 3));
	
	public static double computeVolume(StellarObject spob){
		return ((4.0 / 3.0) * Math.PI * Math.pow(spob.getDiameter() / 2, 3));
	}
	
	public static double computeSurfaceVolume(StellarObject spob){
		return computeVolume(spob) - ((4.0 / 3.0) * Math.PI * Math.pow((spob.getDiameter() / 2) - 1, 3));
	}
	
	public static double angleDifference(double angleOne, double angleTwo){
		return Math.abs(((((angleOne - angleTwo) % 360.0) + 540.0) % 360.0) - 180);
	}

	public static double angleDifference(double angleOne, double angleTwo, boolean clockwise){
		double theta = Math.abs(((((angleOne - angleTwo) % 360.0) + 540.0) % 360.0) + 180) % 360;
		if(clockwise)
			theta = 360 - theta;
		return theta;
	}
	
	public static double randomDouble(double min, double max){
		double rand = Main.rand.nextDouble();
		double range = max - min;
		return (rand * range) + min;
	}
	
	public static double calculateTemperature(StellarObject spob){
		double dist = ((double)(spob.getOrbitalDistance())) / ((double)AU);
		double multiplier = Math.pow((SOLAR_LUMEN_MAGIC_NUMBER * spob.getHost().getLuminosity()) / (16 * Math.PI * BOLTZMANN_CONSTANT), 0.25);
		
		if(spob.getType() == Terrestrial.bodies.Type.PLANET)
			return ((multiplier) * (Math.pow(1.0 - spob.getAlbedo(), 0.25)) * Math.pow(dist, -0.5)) - 273.15;
			//return 273.0 * Math.sqrt(((1.0 - (spob.getAlbedo() * albedoNerf)) * spob.getHost().getLuminosity()) / (dist * dist)) - 273.15;
		else
			return calculateTemperature(spob.getHost());
	}
	
	public static double calculateTemperature(double albedo, double luminosity, double distance){
		double dist = (double)(distance) / AU;
		double multiplier = Math.pow((SOLAR_LUMEN_MAGIC_NUMBER * luminosity) / (16 * Math.PI * BOLTZMANN_CONSTANT), 0.25);
		
		return ((multiplier) * (Math.pow(1.0 - albedo, 0.25)) * Math.pow(dist, -0.5)) - 273.15;
		//return 273.0 * Math.sqrt(((1.0 - albedo) * luminosity) / (dist * dist)) - 273.15;
	}
	
	public static long distanceOfEffectiveTemperature(double temperatureThreshold, double luminosity){
		return distanceOfEffectiveTemperature(temperatureThreshold, luminosity, 0.5);
	}
	
	public static long distanceOfEffectiveTemperature(double temperatureThreshold, double luminosity, double albedo){
		double dist = Math.sqrt(((1.0 - albedo) * luminosity) / Math.pow(((temperatureThreshold + 273.15) / 273), 2));

		return (long)(dist * AU);
	}
	
	public static double calculateLuminosity(StellarObject spob){
		double luminosity = 0.0;
		
		double solarRadii = ((double)(spob.getDiameter() / 2.0) / (SOLAR_RADIUS));
		double solarTemperature = ((double)spob.getEffectiveTemperature()) / (SOLAR_TEMPERATURE);
		
		luminosity = Math.pow(solarRadii, 2) * Math.pow(solarTemperature, 4);
		
		//System.out.println("Answer: " + luminosity);
		
		return luminosity;
	}
	
	public static double pointDistance(double x1, double y1, double x2, double y2){
		double xComp = Math.abs(x2 - x1);
		double yComp = Math.abs(y2 - y1);
		
		return Math.sqrt((xComp * xComp) + (yComp * yComp));
	}

	public static double longPointDistance(long x1, long y1, long x2, long y2){
		double xComp = Math.abs(x2 - x1);
		double yComp = Math.abs(y2 - y1);
		
		return Math.sqrt((xComp * xComp) + (yComp * yComp));
	}
	
	public static double stoppingDistance(double velocity, double acceleration){
		double dist = 0;
		
		dist = (velocity * velocity) / (2 * acceleration);
		
		return dist;
	}
	
	public static void shuffleList(LinkedList<String> nameList){
		int unshuffled = nameList.size() - 1;
		while(unshuffled > 0){
			int index = Main.rand.nextInt(unshuffled);
			String data = nameList.get(index);
			nameList.remove(index);
			nameList.add(data);
			unshuffled--;
		}
	}
	
	public static char intToLetter(int num){
		num--;
		return (char)(((int)'A') + num);
	}
	
	public static String intToNumerals(int num){
		int ones = 0;
		int tens = 0;
		int hundreds = 0;
		int thousands = 0;
		String ans = "";
		
		thousands = num / 1000;
		num -= 1000 * thousands;
		
		hundreds = num / 100;
		num -= 100 * hundreds;
		
		tens = num / 10;
		num -= 10 * tens;
		
		ones = num;
		
		//thousands position
		for(int i = 0; i < thousands; i++)
			ans += "M";
		if(hundreds == 9){
			ans += "CM";
			hundreds = 0;
		}
		if(hundreds >= 5){
			ans += "D";
			hundreds -= 5;
		}
		if(hundreds == 4){
			ans += "CD";
			hundreds = 0;
		}
		for(int i = 0; i < hundreds; i++)
			ans += "C";
		//tens position
		if(tens == 9){
			ans += "XC";
			tens = 0;
		}
		if(tens >= 5){
			ans += "L";
			tens -= 5;
		}
		if(tens == 4){
			ans += "XL";
			tens = 0;
		}
		for(int i = 0; i < tens; i++)
			ans += "X";
		//ones position
		if(ones == 9){
			ans += "IX";
			ones = 0;
		}
		if(ones >= 5){
			ans += "V";
			ones -= 5;
		}
		if(ones == 4){
			ans += "IV";
			ones = 0;
		}
		for(int i = 0; i < ones; i++)
			ans += "I";
		
		return ans;
	}
}
