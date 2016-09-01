package init;

import Terrestrial.resources.Material;
import Terrestrial.resources.State;

public class InitializeMaterials {
	public final Material iron;		//iron
	public final Material uranium;	//uranium
	public final Material aluminum;	//aluminum
	public final Material titanium;	//titanium
	public final Material lead;		//lead, silver
	public final Material copper;		//copper
	public final Material tin;		//tin
	public final Material tungsten;
	public final Material molybdenum;
	public final Material palladium;
	public final Material iridium;
	public final Material gold;
	public final Material silver;
	public final Material steel;
	public final Material magnessium;
	
	public final Material carbon;
	public final Material carbonFiber;
	public final Material plastic;
	public final Material glass;
	
	public final Material graphene;		//coolant
	public final Material fuel;			//energy
	
	public InitializeMaterials(){
		iron = new Material("Iron", 0.0, 0.0, State.SOLID);
		uranium = new Material("Uranium", 0.0, 0.0, State.SOLID);
		aluminum = new Material("Aluminum", 0.0, 0.0, State.SOLID);
		titanium = new Material("Titanium", 0.0, 0.0, State.SOLID);
		lead = new Material("Lead", 0.0, 0.0, State.SOLID);
		copper = new Material("Malachite", 0.0, 0.0, State.SOLID);
		tin = new Material("Cassiterite", 0.0, 0.0, State.SOLID);
		tungsten = new Material("Tungsten", 0.0, 0.0, State.SOLID);
		molybdenum = new Material("Molybdenum", 0.0, 0.0, State.SOLID);
		palladium = new Material("Palladium", 0.0, 0.0, State.SOLID);
		iridium = new Material("Iridium", 0.0, 0.0, State.SOLID);
		gold = new Material("Gold", 0.0, 0.0, State.SOLID);
		silver = new Material("Silver", 0.0, 0.0, State.SOLID);
		steel = new Material("Steel", 0.0, 0.0, State.SOLID);
		magnessium = new Material("Magnessium", 0.0, 0.0, State.SOLID);
		
		carbon = new Material("Carbon", 0.0, 0.0, State.SOLID);
		carbonFiber = new Material("Carbon Fiber", 0.0, 0.0, State.SOLID);
		plastic = new Material("Plastics", 0.0, 0.0, State.SOLID);
		glass = new Material("Glass", 0.0, 0.0, State.SOLID);
		
		graphene = new Material("Graphene", 0.0, 0.0, State.SOLID);
		fuel = new Material("Fuel", 0.0, 0.0, State.LIQUID);
	}
}
