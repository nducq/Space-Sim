package solarsystem;

import Terrestrial.bodies.StarSubType;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import main.Main;
import main.Util;

public class SolarSystemGenerator {
	public static StellarObject generateStar(String name, StellarObject host){
		StarSubType basicType;
		int typeRoll = Main.rand.nextInt(100) + 1;
		int spectralClass = Main.rand.nextInt(10);
		double luminosity;
		double mass;
		double surfaceTemperature;
		double radius;
		
		double massMult = Util.randomDouble(0.9, 1.1);
		double luminosityMult = Util.randomDouble(0.9, 1.1);
		
		if(typeRoll == 1){
			double[] multipliers = StarSubType.giantMultipliers[Main.rand.nextInt(10)];
			
			massMult *= multipliers[0];
			luminosityMult *= multipliers[1];
			
			switch((Main.rand.nextInt(10) + 1)){
				case 1:
					basicType = StarSubType.WHITE_GIANT;
					break;
				case 2:
					basicType = StarSubType.YELLOW_GIANT;
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					basicType = StarSubType.ORANGE_GIANT;
					break;
				case 8:
				case 9:
					basicType = StarSubType.RED_GIANT;
					break;
				//10 would suffice, but we want the compiler to be sure that basicType is never undefined
				default:
					basicType = StarSubType.BLUE_WHITE_GIANT;
					break;
			}
		}
		else if(typeRoll == 2){
			basicType = StarSubType.BLUE_WHITE;
			if(Main.rand.nextInt(10) >= 6)
				basicType = StarSubType.BLUE_WHITE_SUB_GIANT;
		}
		else if(typeRoll >= 3 && typeRoll <= 5){
			basicType = StarSubType.WHITE;
			if(Main.rand.nextInt(10) >= 8)
				basicType = StarSubType.BLUE_WHITE_GIANT;
		}
		else if(typeRoll >= 6 && typeRoll <= 13){
			basicType = StarSubType.YELLOW;
			if(Main.rand.nextInt(10) == 9)
				basicType = StarSubType.YELLOW_SUB_GIANT;
		}
		else if(typeRoll >= 14 && typeRoll <= 27){
			basicType = StarSubType.ORANGE;
			if(Main.rand.nextInt(10) == 9){
				basicType = StarSubType.ORANGE_SUB_GIANT;
				spectralClass = 0;
			}
		}
		else if(typeRoll >= 28 && typeRoll <= 76){
			basicType = StarSubType.RED;
		}
		else if(typeRoll >= 77 && typeRoll <= 86)
			basicType = StarSubType.WHITE_DWARF;
		else if(typeRoll >= 87 && typeRoll <= 99)
			basicType = StarSubType.BROWN_DWARF;
		else
			basicType = StarSubType.WHITE_DWARF; //SPECIAL
		
		if(basicType != StarSubType.WHITE_DWARF && basicType != StarSubType.BROWN_DWARF){
			luminosity = StarSubType.massLuminosityChart[basicType.ordinal()][spectralClass][StarSubType.LUMINOSITY_INDEX] * luminosityMult;
			mass = StarSubType.massLuminosityChart[basicType.ordinal()][spectralClass][StarSubType.MASS_INDEX] * massMult;
			surfaceTemperature = StarSubType.massLuminosityChart[basicType.ordinal()][spectralClass][StarSubType.TEMPERATURE_INDEX];
			radius = StarSubType.massLuminosityChart[basicType.ordinal()][spectralClass][StarSubType.RADIUS_INDEX];
		}
		else if(basicType == StarSubType.WHITE_DWARF){
			int massIndex = Main.rand.nextInt(10);
			int temperatureIndex = Main.rand.nextInt(10);
			mass = StarSubType.whiteDwarfMassRadius[massIndex][0] * massMult;
			radius = StarSubType.whiteDwarfMassRadius[massIndex][1] * massMult;
			surfaceTemperature = StarSubType.whiteDwarfTemperature[temperatureIndex] * luminosityMult;
			luminosity = StarSubType.getLuminosity(radius, surfaceTemperature);
		}
		else{
			int massIndex = Main.rand.nextInt(10);
			int temperatureIndex = Main.rand.nextInt(10);
			mass = StarSubType.brownDwarfMassRadius[massIndex][0] * massMult;
			radius = StarSubType.brownDwarfMassRadius[massIndex][1] * massMult;
			surfaceTemperature = StarSubType.brownDwarfTemperature[temperatureIndex] * luminosityMult;
			luminosity = StarSubType.getLuminosity(radius, surfaceTemperature);
		}
			
		StellarObject ret = new StellarObject(name, (int)(radius * 2 * Util.SOLAR_RADIUS), 0, 0, 0, Type.STAR, basicType);
		ret.setHost(host);
		ret.setEffectiveTemperature(surfaceTemperature);
		ret.setSurfaceTemperature(surfaceTemperature);
		ret.setMass(mass);
		ret.setLuminosity(luminosity);
		ret.setSpectralClass(spectralClass);
		
		return ret;
	}
	
	
}
