package init;

import Terrestrial.resources.TradeGoods;

public class InitializeTradeGoods {
	/*Initialize useless crap*/
	public final TradeGoods electronics;
	public final TradeGoods industrialGoods;
	public final TradeGoods medicalSupplies;
	public final TradeGoods luxuryGoods;
	public final TradeGoods equipment;
	public final TradeGoods circuitry;
	public final TradeGoods spirits;
	public final TradeGoods delicacies;
	public final TradeGoods clothing;
	public final TradeGoods paper;
	public final TradeGoods chemicals;
	public final TradeGoods cleaningSupplies;
	public final TradeGoods softDrinks;
	public final TradeGoods famousArtwork;
	public final TradeGoods plastics;
	public final TradeGoods smallArms;
	
	public InitializeTradeGoods(){
		electronics = new TradeGoods("Consumer Electronics");
		industrialGoods = new TradeGoods("Industrial Goods");
		medicalSupplies = new TradeGoods("Medical Supplies");
		luxuryGoods = new TradeGoods("Luxury Goods");
		equipment = new TradeGoods("Equipment");
		circuitry = new TradeGoods("Circuit Boards");
		spirits = new TradeGoods("Spirits");
		delicacies = new TradeGoods("Delicacies");
		clothing = new TradeGoods("Clothing");
		paper = new TradeGoods("Paper");
		chemicals = new TradeGoods("Chemicals");
		cleaningSupplies = new TradeGoods("Cleaning Supplies");
		softDrinks = new TradeGoods("Soft Drinks");
		famousArtwork = new TradeGoods("Famous Artwork");
		plastics = new TradeGoods("Plastics");
		smallArms = new TradeGoods("Small Arms");
	}
}
