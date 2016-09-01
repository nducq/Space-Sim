package init;

import Terrestrial.resources.Resource;

public class InitializeResources {
	public final Resource iron;
	public final Resource copper;
	public final Resource tungsten;
	public final Resource titanium;
	public final Resource aluminum;
	public final Resource uranium;
	public final Resource silicon;
	public final Resource crudeOil;
	public final Resource limestone;
	public final Resource lumber;
	public final Resource sand;
	
	public InitializeResources(){
		iron = new Resource("Iron");
		copper = new Resource("Coppper");
		tungsten = new Resource("Tungsten");
		titanium = new Resource("Titanium");
		aluminum = new Resource("Aluminum");
		uranium = new Resource("Uranium");
		silicon = new Resource("Silicon");
		crudeOil = new Resource("Crude Oil");
		limestone = new Resource("Limestone");
		lumber = new Resource("Lumber");
		sand = new Resource("Sand");
	}
}
