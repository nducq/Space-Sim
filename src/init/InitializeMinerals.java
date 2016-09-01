package init;

import Terrestrial.resources.Mineral;
import Terrestrial.resources.State;

import java.util.ArrayList;
import java.util.Iterator;

public class InitializeMinerals implements Iterable<Mineral>{
	public final Mineral silica;
	public final Mineral ironOre;		//iron
	public final Mineral uraniumOre;	//uranium
	public final Mineral aluminumOre;	//aluminum
	public final Mineral titaniumOre;	//titanium
	public final Mineral leadOre;		//lead, silver
	public final Mineral copperOre;		//copper
	public final Mineral tinOre;		//tin
	public final Mineral tungstenOre;
	public final Mineral molybdenumOre;
	public final Mineral palladiumOre;
	public final Mineral iridiumOre;
	public final Mineral nativeGold;
	public final Mineral nativeSilver;
	
	//public final Mineral crudeOil;		//carbon, energy, plastics
	public final Mineral helium;		//coolant
	public final Mineral hydrogen;		//energy
	public final Mineral methane;		//energy
	public final Mineral water;
	public final Mineral carbonDioxide;
	public final Mineral oxygen;
	public final Mineral nitrogen;
	public final Mineral flourine;
	public final Mineral chlorine;
	public final Mineral neon;
	public final Mineral argon;
	public final Mineral krypton;
	public final Mineral xenon;
	public final Mineral radon;
	
	
	public InitializeMinerals(){
		silica = new Mineral(new String[]{"Silica", "Molten Silica", "Silica Vapor"}, new double[]{0.30, 0.125, 0.05}, new double[]{1713.0, 2950.0}, 0.0, 180000000, 75, State.SOLID, false);
		ironOre = new Mineral(new String[]{"Hematite", "Molten Iron", "Iron Vapor"}, new double[]{0.17, 0.25, 0.1}, new double[]{1538.0, 2862.0}, 0.0, 100000000, 50, State.SOLID, false);
		uraniumOre = new Mineral(new String[]{"Uraninite", "Molten Uranium", "Uranium Vapor"}, new double[]{0.1, 0.08, 0.05}, new double[]{1132.2, 4131.0}, 0.0, 10000, 5, State.SOLID, false);
		aluminumOre = new Mineral(new String[]{"Bauxite", "Molten Aluminum", "Aluminum Vapor"}, new double[]{0.2, 0.25, 0.1}, new double[]{660.32, 2470.0}, 0.0, 1000000, 20, State.SOLID, false);
		titaniumOre = new Mineral(new String[]{"Ilmenite", "Molten Titanium", "Titanium Vapor"}, new double[]{0.17, 0.25, 0.1}, new double[]{1668.0, 3287.0}, 0.0, 100000, 10, State.SOLID, false);
		leadOre = new Mineral(new String[]{"Galena", "Molten Lead", "Lead Vapor"}, new double[]{0.25, 0.2, 0.1}, new double[]{327.46, 1749.0}, 0.0, 100000, 15, State.SOLID, false);
		copperOre = new Mineral(new String[]{"Malachite", "Molten Copper", "Copper Vapor"}, new double[]{0.15, 0.12, 0.1}, new double[]{1084.62, 2562.0}, 0.0, 100000, 20, State.SOLID, false);
		tinOre = new Mineral(new String[]{"Cassiterite", "Molten Tin", "Tin Vapor"}, new double[]{0.25, 0.2, 0.1}, new double[]{231.93, 2602.0}, 0.0, 100000, 5, State.SOLID, false);
		tungstenOre = new Mineral(new String[]{"Wolframite", "Molten Tungsten", "Tungsten Vapor"}, new double[]{0.15, 0.12, 0.1}, new double[]{3422.0, 5930.0}, 0.0, 1000, 5, State.SOLID, false);
		molybdenumOre = new Mineral(new String[]{"Molybdenite", "Molten Molybdenum", "Molybdenum Vapor"}, new double[]{0.25, 0.2, 0.1}, new double[]{2623.0, 4639.0}, 0.0, 1000, 8, State.SOLID, false);
		palladiumOre = new Mineral(new String[]{"Native Palladium", "Molten Palladium", "Palladium Vapor"}, new double[]{0.4, 0.3, 0.2}, new double[]{1554.9, 2963.0}, 0.0, 10000, 3, State.SOLID, false);
		iridiumOre = new Mineral(new String[]{"Native Iridium", "Molten Iridium", "Iridium Vapor"}, new double[]{0.4, 0.3, 0.2}, new double[]{2446.0, 4130.0}, 0.0, 10000, 3, State.SOLID, false);
		nativeGold = new Mineral(new String[]{"Native Gold", "Molten Gold", "Gold Vapor"}, new double[]{0.4, 0.3, 0.2}, new double[]{1064.18, 2970.0}, 0.0, 1000, 3, State.SOLID, false);
		nativeSilver = new Mineral(new String[]{"Native Silver", "Molten Silver", "Silver Vapor"}, new double[]{0.4, 0.3, 0.2}, new double[]{961.78, 2162.0}, 0.0, 1000, 1, State.SOLID, false);		
		
		//crudeOil = new Mineral(new String[]{"Crude Oil", "Crude Oil", "Crude Oil"}, new double[]{0.8, 0.25, 0.9}, new double[]{0.0, 100.0}, 0.0, 0.0, 0.0, State.LIQUID);
		helium = new Mineral(new String[]{"Frozen Helium", "Liquid Helium", "Helium"}, new double[]{0.8, 0.25, 0.15}, new double[]{-272.20, -268.928}, 0.0, 100000, 5, State.GAS, false);
		hydrogen = new Mineral(new String[]{"Frozen Hydrogen", "Liquid Hydrogen", "Hydrogen"}, new double[]{0.8, 0.25, 0.15}, new double[]{-259.16, -252.879}, 0.0, 1000000, 5, State.GAS, false);
		methane = new Mineral(new String[]{"Frozen Methane", "Liquid Methane", "Methane"}, new double[]{0.8, 0.25, 0.15}, new double[]{-182.5, -161.49}, 0.0, 100000, 5, State.GAS, true);
		water = new Mineral(new String[]{"Ice", "Water", "Water Vapor"}, new double[]{0.8, 0.25, 0.75}, new double[]{0.0, 100.0}, 0.0, 150000000, 50, State.LIQUID, true);
		carbonDioxide = new Mineral(new String[]{"Dry Ice", "Liquid Carbon Dioxide", "Carbon Dioxide"}, new double[]{0.8, 0.25, 0.75}, new double[]{-56.6, -56.6}, 0.0, 100000, 30, State.LIQUID, true);
		oxygen = new Mineral(new String[]{"Frozen Oxygen", "Liquid Oxygen", "Oxygen"}, new double[]{0.8, 0.25, 0.15}, new double[]{-218.79, -182.962}, 0.0, 1000000, 80, State.GAS, false);
		nitrogen = new Mineral(new String[]{"Frozen Nitrogen", "Liquid Nitrogen", "Nitrogen"}, new double[]{0.8, 0.25, 0.15}, new double[]{-210.00, -195.795}, 0.0, 10000000, 70, State.GAS, false);
		flourine = new Mineral(new String[]{"Frozen Flourine", "Liquid Flourine", "Flourine"}, new double[]{0.8, 0.25, 0.15}, new double[]{-219.67, -188.11}, 0.0, 100000, 10, State.GAS, false);
		chlorine = new Mineral(new String[]{"Frozen Chlorine", "Liquid Chlorine", "Chlorine"}, new double[]{0.8, 0.25, 0.15}, new double[]{-101.5, -34.04}, 0.0, 100000, 4, State.GAS, false);
		neon = new Mineral(new String[]{"Frozen Neon", "Liquid Neon", "Neon"}, new double[]{0.8, 0.25, 0.15}, new double[]{-248.59, -246.046}, 0.0, 1000000, 3, State.GAS, false);
		argon = new Mineral(new String[]{"Frozen Argon", "Liquid Argon", "Argon"}, new double[]{0.8, 0.25, 0.15}, new double[]{-189.34, -185.848}, 0.0, 100000, 3, State.GAS, false);
		krypton = new Mineral(new String[]{"Frozen Krypton", "Liquid Krypton", "Krypton"}, new double[]{0.8, 0.25, 0.15}, new double[]{-157.37, -153.415}, 0.0, 100000, 3, State.GAS, false);
		xenon = new Mineral(new String[]{"Frozen Xenon", "Liquid Xenon", "Xenon"}, new double[]{0.8, 0.25, 0.15}, new double[]{-111.75, -108.099}, 0.0, 100000, 3, State.GAS, false);
		radon = new Mineral(new String[]{"Frozen Radon", "Liquid Radon", "Radon"}, new double[]{0.8, 0.25, 0.15}, new double[]{-71.0, -61.7}, 0.0, 10000, 3, State.GAS, false);
	}

	@Override
	public Iterator<Mineral> iterator() {
		// TODO Auto-generated method stub
		ArrayList<Mineral> mineralList = new ArrayList<Mineral>();
		
		mineralList.add(silica);
		mineralList.add(ironOre);
		mineralList.add(uraniumOre);
		mineralList.add(aluminumOre);
		mineralList.add(titaniumOre);
		mineralList.add(leadOre);
		mineralList.add(copperOre);
		mineralList.add(tinOre);
		mineralList.add(tungstenOre);
		mineralList.add(molybdenumOre);
		mineralList.add(palladiumOre);
		mineralList.add(iridiumOre);
		mineralList.add(nativeGold);
		mineralList.add(nativeSilver);
		
		//mineralList.add(crudeOil);
		mineralList.add(helium);
		mineralList.add(hydrogen);
		mineralList.add(methane);
		mineralList.add(water);
		mineralList.add(carbonDioxide);
		mineralList.add(oxygen);
		mineralList.add(nitrogen);
		mineralList.add(flourine);
		mineralList.add(chlorine);
		mineralList.add(neon);
		mineralList.add(argon);
		mineralList.add(krypton);
		mineralList.add(xenon);
		mineralList.add(radon);
		
		
		Iterator<Mineral> it = new Iterator<Mineral>(){

			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < mineralList.size();
			}

			@Override
			public Mineral next() {
				index++;
				return mineralList.get(index - 1);
			}
			
		};
		
		return it;
	}
}
