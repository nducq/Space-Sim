package starmap;

import java.awt.Color;
import java.util.LinkedList;

import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import governments.Faction;
import main.Main;

public class StarSystem implements Comparable{
	public static final int MAX_LINKS = 16;
	private static final int BASE_ID = 128;
	
	private static int numSystems;
	private StellarObject sun;
	private String name;
	private Faction government;
	private double x;
	private double y;
	private double theta;
	private int id;
	private boolean selected;
	private boolean explored;
	private double age;
	private double relativeAbundance;
	
	private int color = Color.WHITE.getRGB();
	
	private boolean hasMapped;
	private LinkedList<StarSystem> links;
	private LinkedList<StellarObject> spobs;
	
	public StarSystem(double x, double y, String name, StellarObject sun){
		this.x = x;
		this.y = y;
		this.name = name;
		this.sun = sun;
		this.id = numSystems + BASE_ID;
		this.government = null;
		
		numSystems++;
		selected = false;
		theta = 0;
		relativeAbundance = 0;
		
		//StarMapGenerator.setGrid((int)Math.ceil(x / StarMapGenerator.CELL_WIDTH) + (StarMapGenerator.GRID_WIDTH / 2) - 1, (int)Math.ceil(y / StarMapGenerator.CELL_HEIGHT) + (StarMapGenerator.GRID_HEIGHT / 2) - 1);
		
		hasMapped = false;
		links = new LinkedList<StarSystem>();
		spobs = new LinkedList<StellarObject>();
		//assignPlanetsToStar(sun);
	}
	
	public int numSuns(){
		if (sun == null)
			return 0;
		else
			return numSunsOrbiting(sun) + 1;
	}
	
	public void addLink(StarSystem link){
		removeLink(link);
		if(!links.contains(link))
			links.add(link);
		if(!link.getLinks().contains(this))
			link.getLinks().add(this);
		//System.out.println(link.getName() + " has been linked with " + name);
	}
	
	public void removeLink(StarSystem link){
		while(links.contains(link))
			links.remove(link);
		while(link.getLinks().contains(this))
			link.getLinks().remove(this);
	}
	
	public int countReferences(LinkedList<StarSystem> systemList){
		int numRefs = 0;
		
		for(StarSystem s: systemList){
			if(s.links.contains(this))
				numRefs++;
		}
			
		
		return numRefs;
	}
	
	public void removeAllLinks(){
		for(int i = 0; i < links.size(); i++)
			links.get(i).removeLink(this);
		links.clear();
	}
	
	public int numSunsOrbiting(StellarObject body){
		if (body == null)
			return 0;
		else{
			for(StellarObject s: body.getOrbitingBodies()){
				if(s.getType() != Terrestrial.bodies.Type.PLANET || s.getType() != Terrestrial.bodies.Type.MOON)
					return 1 + numSunsOrbiting(s);
				else
					return numSunsOrbiting(s);
			}
		}
		//Shouldn't be reached
		return 0;
	}
	
	public void assignPlanetsToStar(StellarObject spob){
		addSpob(spob);
		for(StellarObject body: spob.getOrbitingBodies()){
			//addSpob(body);
			assignPlanetsToStar(body);
		}
	}
	
	public void addSpob(StellarObject spob){
		spobs.add(spob);
	}

	public LinkedList<StellarObject> getSpobs(){
		return spobs;
	}

	public LinkedList<StellarObject> getStars(){
		LinkedList<StellarObject> stars = new LinkedList<StellarObject>();
		for(StellarObject spob: spobs){
			if(spob.getType() == Type.STAR)
				stars.add(spob);
		}
		return stars;
	}
	
	public LinkedList<StellarObject> getPlanets(){
		LinkedList<StellarObject> planets = new LinkedList<StellarObject>();
		for(StellarObject spob: spobs){
			if(spob.getType() == Type.PLANET || spob.getType() == Type.MOON)
				planets.add(spob);
		}
		return planets;
	}
	
	public StellarObject getSun(){
		return sun;
	}
	
	public void setSun(StellarObject sun){
		this.sun = sun;
	}

	public void setMap(boolean hasMapped){
		this.hasMapped = hasMapped;
	}
	
	public void setTheta(double theta){
		this.theta = theta;
	}
	
	public void setAge(double age){
		this.age = age;
	}
	
	public double getAge(){
		return age;
	}
	
	public void setRelativeAbundance(double relativeAbundance){
		this.relativeAbundance = relativeAbundance;
	}
	
	public double getRelativeAbundance(){
		return relativeAbundance;
	}
	
	public boolean hasMapped(){
		return hasMapped;
	}
	
	public double getTheta(){
		return theta;
	}
	
	public LinkedList<StarSystem> getLinks(){
		return links;
	}
	
	public double getX(){
		return x;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void addToGrid(){
		//System.out.println("asdf");
		StarMapGenerator.setGrid((int)Math.ceil(x / StarMapGenerator.CELL_WIDTH) + (StarMapGenerator.GRID_WIDTH / 2) - 1, (int)Math.ceil(y / StarMapGenerator.CELL_HEIGHT) + (StarMapGenerator.GRID_HEIGHT / 2) - 1);
	}

	public int getID(){
		return id;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int r, int g, int b){
		this.color = (255 << 24) + (r << 16) + (g << 8) + (b);
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	@Override
	public int compareTo(Object arg0) {
		return id - ((StarSystem)arg0).id;
	}

	public void setGovernment(Faction government) {
		this.government = government;
	}
	
	public Faction getGovernment(){
		return government;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public boolean linkExplored(){
		for(StarSystem s: links){
			if(s.isExplored())
				return true;
		}
		return false;
	}
	
	public boolean isExplored(){
		return explored;
	}
	
	public void explore(){
		explored = true;
	}
}
