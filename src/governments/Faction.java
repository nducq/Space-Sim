package governments;

import java.awt.Color;

/**
 * Faction class
 */
public class Faction {
	private String longName;
	private String shortName;
	private String adj;
	
	private int factionColor;
	private int range;
	
	public Faction(String longName, String shortName, String adj, int factionColor, int range){
		this.longName = longName;
		this.shortName = shortName;
		this.adj = adj;
		this.factionColor = factionColor;
		this.range = range;
	}
	
	public String getLongName(){
		return longName;
	}

	public String getShortName(){
		return shortName;
	}
	
	public String getAdj(){
		return adj;
	}
	
	public int getColor(){
		return factionColor;
	}
	
	public int getRange(){
		return range;
	}
}
