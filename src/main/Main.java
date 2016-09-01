package main;

import processing.core.PApplet;
import processing.core.PImage;
import solarsystem.SolarSystemGenerator;
import starmap.BodyGenerator;
import starmap.StarMapGenerator;
import starmap.StarSystem;
import init.InitializeFactions;
import init.InitializeMaterials;
import init.InitializeMinerals;
import init.InitializePlanetTypes;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import Terrestrial.resources.Mineral;
import governments.Faction;
import gui.GuiCreator;
import gui.GuiObject;
import gui.SeedWindow;
import Terrestrial.bodies.StarSubType;
import Terrestrial.bodies.Type;
import Terrestrial.bodies.PlanetSubType;

import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import com.jogamp.graph.geom.Triangle;

public class Main extends PApplet implements GuiCreator{
	public static final double WARP_SPEED = 700;
	public static final double WARP_DISTANCE = 2000;
	public static final double ARRIVAL_DISTANCE = Util.AU * 40 / 100;
	public static final double SHORT_JUMP_ARRIVAL_DISTANCE = 100000;
	public static final double WARP_FADE = 0.0075;
	public static final int WIN_WIDTH = 960;
	public static final int WIN_HEIGHT = 720;
	
	public static final double MAX_TEMPERATURE = 2000;
	public static final double TEMPERATURE_JITTER = 0.2;
	public static final double MAX_GREENHOUSE_FACTOR = 3.35;
	
	public static final double MEAN_TEMP = 14.0;
	public static final double TEMP_DEVIATION = 24.0;
	
	public static final double MAX_PRESSURE = 4.0;
	
	public static final double MEAN_OXYGEN = 0.2;
	public static final double OXYGEN_DEVIATION = 0.1;
	
	public static final int MAX_SUNS_PER_SYSTEM = 0;
	public static final int MAX_SYSTEMS = 2000;
	public static int NUM_ROOTS = 30;
	
	public static final double TITIUS_COEFFICIENT = 0.3;
	public static final double BODE_CONSTANT = 0.4;
	
	public static PImage[] backgroundList;
	public static int[] backgroundX;
	public static int[] backgroundY;
	
	public static final Random rand = new Random();
	
	public static PImage[] redStarList;
	public static PImage[] orangeStarList;
	public static PImage[] yellowStarList;
	public static PImage[] whiteStarList;
	public static PImage[] blueWhiteStarList;
	
	public static PImage[] dwarfList;
	public static PImage[] terrestrialList;
	public static PImage[] superTerrestrialList;
	
	public static PImage[] gasGiantList;
	public static PImage[] superGasGiantList;
	public static PImage[] asteroidList;
	
	public static PImage[] targetBorder;
	
	private static int sunCount;
	
	public static Ship playerShip;
	private static StarSystem currentSystem;
	private static StarSystem targetSystem;

	private static SeedWindow seedWindow;
	private static InitializeMaterials initMat;
	private static InitializeMinerals initMin;
	private static InitializeFactions initFact;
	private static InitializePlanetTypes planetTypes;
	
	private static LinkedList<StellarObject> spobList;
	private static LinkedList<Ship> shipList;
	private static LinkedList<StarSystem> systemList;
	
	private static HashMap<String, Integer> systemNameList;
	
	public static long viewXView;
	public static long viewYView;
	
	public static double warpAlpha;
	private static Map starMap;
	
	private int keyState[] = new int[512];
	private int mouseState[] = new int[256];
	private boolean gamePaused = false;
	private static String keyString = "";
	private char lastChar = '\0';
	private boolean charTyped = false;

	public static final int KEY_PRESSED = 1;
	public static final int KEY_HELD = 2;
	public static final int KEY_RELEASED = 0;
	public static final int KEY_JUST_RELEASED = 3;
	
	static public void main(String args[]) {
		//initialize the PApplet
		PApplet.main("main.Main");
	}
	
	public void settings(){
		//smooth(2);
		size(WIN_WIDTH, WIN_HEIGHT);
		//fullScreen();
	}
	
	public void setup(){
		//initialize materials and minerals
		initMat = new InitializeMaterials();
		initMin = new InitializeMinerals();
		initFact = new InitializeFactions();
		planetTypes = new InitializePlanetTypes();
		
		//load star names;
		systemNameList = new HashMap<String, Integer>();

		//Instantiate our random number generator
		//rand = new Random();
		//rand.setSeed(("asdfasdf").hashCode());
		
		//load graphics directories
		File[] redStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Red/").listFiles();
		File[] orangeStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Orange/").listFiles();
		File[] yellowStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Yellow/").listFiles();
		File[] whiteStarGraphicsFolder = new File("./src/graphics/sprites/Suns/White/").listFiles();
		File[] blueWhiteStarGraphicsFolder = new File("./src/graphics/sprites/Suns/BlueWhite/").listFiles();
		
		File[] jovianGraphicsFolder = new File("./src/graphics/sprites/Planets/Jovian/").listFiles();
		File[] terrestrialGraphicsFolder = new File("./src/graphics/sprites/Planets/Terrestrial/").listFiles();
		File[] asteroidGraphicsFolder = new File("./src/graphics/sprites/Asteroids/").listFiles();
		
		File systemNames = new File("./src/data/system names.txt");
				
		backgroundList = new PImage[10];
		backgroundX = new int[10];
		backgroundY = new int[10];
		
		redStarList = new PImage[redStarGraphicsFolder.length];
		orangeStarList = new PImage[orangeStarGraphicsFolder.length];
		yellowStarList = new PImage[yellowStarGraphicsFolder.length];
		whiteStarList = new PImage[whiteStarGraphicsFolder.length];
		blueWhiteStarList = new PImage[blueWhiteStarGraphicsFolder.length];
		
		dwarfList = new PImage[terrestrialGraphicsFolder.length];
		terrestrialList = new PImage[terrestrialGraphicsFolder.length];
		superTerrestrialList = new PImage[terrestrialGraphicsFolder.length];
		
		gasGiantList = new PImage[jovianGraphicsFolder.length];
		superGasGiantList = new PImage[jovianGraphicsFolder.length];
		asteroidList = new PImage[asteroidGraphicsFolder.length];
		targetBorder = new PImage[4];
		
		targetBorder[0] = loadImage("./src/graphics/sprites/Gui/planetTL.bmp"); 
		targetBorder[1] = loadImage("./src/graphics/sprites/Gui/planetTR.bmp");
		targetBorder[2] = loadImage("./src/graphics/sprites/Gui/planetBL.bmp");
		targetBorder[3] = loadImage("./src/graphics/sprites/Gui/planetBR.bmp");

		for(int i = 0; i < terrestrialList.length; i++){
			dwarfList[i] = loadImage(terrestrialGraphicsFolder[i].getPath());
			terrestrialList[i] = dwarfList[i].copy();
			superTerrestrialList[i] = dwarfList[i].copy();
			
			dwarfList[i].resize(75, 75);
			terrestrialList[i].resize(150, 150);
			superTerrestrialList[i].resize(300, 300);
			
			for(int j = dwarfList[i].pixels.length - 1; j > 0; j--){
				if (dwarfList[i].pixels[j] == dwarfList[i].pixels[0])
					dwarfList[i].pixels[j] = color(0, 0, 0, 0);
			}
			for(int j = terrestrialList[i].pixels.length - 1; j > 0; j--){
				if (terrestrialList[i].pixels[j] == terrestrialList[i].pixels[0])
					terrestrialList[i].pixels[j] = color(0, 0, 0, 0);
			}
			for(int j = superTerrestrialList[i].pixels.length - 1; j > 0; j--){
				if (superTerrestrialList[i].pixels[j] == superTerrestrialList[i].pixels[0])
					superTerrestrialList[i].pixels[j] = color(0, 0, 0, 0);
			}
			//dwarfList[i].pixels[0] = color(255, 0, 255, 0);
			//terrestrialList[i].pixels[0] = color(255, 0, 255, 0);
			//superTerrestrialList[i].pixels[0] = color(255, 0, 255, 0);
			
			dwarfList[i].updatePixels();
			terrestrialList[i].updatePixels();
			superTerrestrialList[i].updatePixels();
		}
		
		//Initialize gas giant graphics
		for(int i = 0; i < gasGiantList.length; i++){
			//int transparentPixel = gasGiantList[i].pixels[0];
			gasGiantList[i] = loadImage(jovianGraphicsFolder[i].getPath());
			superGasGiantList[i] = gasGiantList[i].copy();
			
			gasGiantList[i].resize(500, 500);
			superGasGiantList[i].resize(800, 800);
			
			for(int j = gasGiantList[i].pixels.length - 1; j > 0; j--){
				if (gasGiantList[i].pixels[j] == gasGiantList[i].pixels[0])
					gasGiantList[i].pixels[j] = color(0, 0, 0 , 0);
			}
			for(int j = superGasGiantList[i].pixels.length - 1; j > 0; j--){
				if (superGasGiantList[i].pixels[j] == superGasGiantList[i].pixels[0])
					superGasGiantList[i].pixels[j] = color(255, 0, 255, 0);
				
			}
			//gasGiantList[i].pixels[0] = color(255, 0, 255, 0);
			//superGasGiantList[i].pixels[0] = color(255, 0, 255, 0);
			
			gasGiantList[i].updatePixels();
			superGasGiantList[i].updatePixels();
		
		}
		
		for(int i = 0; i < asteroidList.length; i++){
			asteroidList[i] = loadImage(asteroidGraphicsFolder[i].getPath());
			asteroidList[i].resize(150, 150);
			
			for(int j = asteroidList[i].pixels.length - 1; j > 0; j--){
				if (asteroidList[i].pixels[j] == asteroidList[i].pixels[0])
					asteroidList[i].pixels[j] = color(255, 0, 255, 0);
			}
			asteroidList[i].updatePixels();
		}
		
		for(int i = 0; i < redStarList.length; i++){
			redStarList[i] = loadImage(redStarGraphicsFolder[i].getPath());
			
			for(int j = redStarList[i].pixels.length - 1; j > 0; j--){
				if (redStarList[i].pixels[j] == redStarList[i].pixels[0])
					redStarList[i].pixels[j] = color(255, 0, 255, 0);
			}
			redStarList[i].updatePixels();
		}

		for(int i = 0; i < orangeStarList.length; i++){
			orangeStarList[i] = loadImage(orangeStarGraphicsFolder[i].getPath());
			
			for(int j = orangeStarList[i].pixels.length - 1; j > 0; j--){
				if (orangeStarList[i].pixels[j] == orangeStarList[i].pixels[0])
					orangeStarList[i].pixels[j] = color(255, 0, 255, 0);
			}
			orangeStarList[i].updatePixels();
		}
		
		for(int i = 0; i < yellowStarList.length; i++){
			yellowStarList[i] = loadImage(yellowStarGraphicsFolder[i].getPath());
			
			for(int j = yellowStarList[i].pixels.length - 1; j > 0; j--){
				if (yellowStarList[i].pixels[j] == yellowStarList[i].pixels[0])
					yellowStarList[i].pixels[j] = color(255, 0, 255, 0);
			}
			yellowStarList[i].updatePixels();
		}
		
		for(int i = 0; i < whiteStarList.length; i++){
			whiteStarList[i] = loadImage(whiteStarGraphicsFolder[i].getPath());
			
			for(int j = whiteStarList[i].pixels.length - 1; j > 0; j--){
				if (whiteStarList[i].pixels[j] == whiteStarList[i].pixels[0])
					whiteStarList[i].pixels[j] = color(255, 0, 255, 0);
			}
			whiteStarList[i].updatePixels();
		}
		
		for(int i = 0; i < blueWhiteStarList.length; i++){
			blueWhiteStarList[i] = loadImage(blueWhiteStarGraphicsFolder[i].getPath());
			
			for(int j = blueWhiteStarList[i].pixels.length - 1; j > 0; j--){
				if (blueWhiteStarList[i].pixels[j] == blueWhiteStarList[i].pixels[0])
					blueWhiteStarList[i].pixels[j] = color(255, 0, 255, 0);
			}
			blueWhiteStarList[i].updatePixels();
		}
		
		for(int i = 0; i < targetBorder.length; i++){
			for(int j = targetBorder[i].pixels.length - 1; j > 0; j--){
				if (targetBorder[i].pixels[j] == targetBorder[i].pixels[0])
					targetBorder[i].pixels[j] = color(255, 0, 255, 0);
			}
			//targetBorder[i].pixels[0] = color(255, 0, 255, 0);
			targetBorder[i].updatePixels();
		}
		
		backgroundList[0] = loadImage("graphics/backgrounds/bkg_stars1.png"
				);
		backgroundList[1] = loadImage("graphics/backgrounds/bkg_stars1.png");
		backgroundList[2] = loadImage("graphics/backgrounds/bkg_stars2.png");
		backgroundList[3] = loadImage("graphics/backgrounds/bkg_stars3.png");
		
		backgroundX[0] = 0;
		backgroundX[1] = 0;
		backgroundX[2] = 0;
		backgroundX[3] = 0;

		backgroundY[0] = 0;
		backgroundY[1] = 0;
		backgroundY[2] = 0;
		backgroundY[3] = 0;
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(systemNames));
			String line = br.readLine();
			while(line != null){
				if(line.length() != 0 && line.charAt(0) != '#'){
					String[] lineArr = line.split(", ");
					systemNameList.put(lineArr[0], Integer.parseInt(lineArr[1]));
				}
				line = br.readLine();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		warpAlpha = 0;
		
		textSize(12);
		//smooth(0);
		frameRate(60);

		seedWindow = new SeedWindow(200, 200, this, this);
		seedWindow.show();
	}
	
	public static void beginGeneration(int seed){
		//seed random generator
		rand.setSeed(seed);
		
		//initialize the solar system
		spobList = new LinkedList<StellarObject>();
		StellarObject sun = new StellarObject("Sol", (int) (Util.SOLAR_RADIUS * 2), 0, 0, 0, Type.STAR, StarSubType.YELLOW);//generateSun(currentSystem, "Sol", 0, null, StarSubType.YELLOW);
		StellarObject earth = new StellarObject("Earth", (int) (Util.EARTH_RADIUS * 2), (long)Util.AU, 0, 0, Type.PLANET, PlanetSubType.TERRESTRIAL);
		StellarObject venus = new StellarObject("Venus", (int) (Util.EARTH_RADIUS * 2), (long)(Util.AU * 0.728), 0, 0, Type.PLANET, PlanetSubType.TERRESTRIAL);
		StellarObject mercury = new StellarObject("Mercury", (int) (Util.EARTH_RADIUS * 0.7658), (long)(Util.AU * 0.308), 0, 0, Type.PLANET, PlanetSubType.DWARF);
		StellarObject mars = new StellarObject("Mars", (int) (Util.EARTH_RADIUS * 1.066), (long)(Util.AU * 1.381), 0, 0, Type.PLANET, PlanetSubType.DWARF);
		
		//mars.setApoapsis((long)(Util.AU * 1.381 * 4.8));
		//mars.setPeriapsis((long)(Util.AU * 1.381 * 1.2));
		
		sun.setLuminosity(1.0);
		sun.getOrbitingBodies().add(mercury);
		sun.getOrbitingBodies().add(venus);
		sun.getOrbitingBodies().add(earth);
		sun.getOrbitingBodies().add(mars);
		
		earth.setAtmosphericPressure(1.0);
		venus.setAtmosphericPressure(100.0);
		mercury.setAtmosphericPressure(0.01);
		mars.setAtmosphericPressure(0.01);
		
		earth.setHost(sun);
		venus.setHost(sun);
		mercury.setHost(sun);
		mars.setHost(sun);
		
		BodyGenerator.generateOre(earth, planetTypes.earthlikeMinerals, planetTypes.earthlikeAtmosphere);
		BodyGenerator.generateOre(venus, planetTypes.barrenSurfaceMinerals, planetTypes.venusianAtmosphere);
		BodyGenerator.generateOre(mercury, planetTypes.barrenSurfaceMinerals, planetTypes.marslikeAtmosphere);
		BodyGenerator.generateOre(mars, planetTypes.desertMinerals, planetTypes.marslikeAtmosphere);
		
		earth.update();
		
		venus.setCloudCoverage(1.0);
		venus.update();
		mercury.update();
		mars.update();
		
		spobList.add(sun);
		spobList.add(earth);
		spobList.add(mercury);
		spobList.add(mars);
		
		//initialize root system
		currentSystem = new StarSystem(0, 0, "Root 1", sun);
		targetSystem = null;
		systemList = new LinkedList<StarSystem>();
		
		systemList.add(currentSystem);
		currentSystem.explore();
		currentSystem.assignPlanetsToStar(sun);
		
		StarMapGenerator.generateStarMap(currentSystem);
		
		//int numRoots = rand.nextInt(3) + 3;
		//double avgTheta = 2 * Math.PI / numRoots;
		
		for(Faction f: initFact){
			StarSystem hq = systemList.get(rand.nextInt(systemList.size()));
			placeFactions(hq, f, f.getRange());
		}
				
		shipList = new LinkedList<Ship>();
		playerShip = new Ship(true);
		shipList.add(playerShip);
	}
	
	public void step(){
		seedWindow.step();
		
		if(keyState[KeyEvent.VK_ENTER] == KEY_PRESSED){
			gamePaused = !gamePaused;
			
			if(gamePaused)
				starMap = new Map((WIN_WIDTH / 2) - ((int)playerShip.getX() / 2000), (WIN_HEIGHT / 2) - ((int)playerShip.getY() / 2000), this);
			else{
				if(starMap.getTargetSystem() != null)
					Main.setTargetSystem(starMap.getTargetSystem());
				for(int i = 0; i < Main.getCurrentSystem().getSpobs().size(); i++){
					if(Main.getCurrentSystem().getSpobs().get(i) == starMap.getTargetPlanet())
						Main.getPlayerShip().setTargetIndex(i);
				}
				starMap = null;
			}
		}
		
		if(Main.getSpobList() != null){
			if(gamePaused) {
				starMap.step();
			}
			else{
				if(warpAlpha > 0)
					warpAlpha -= Main.WARP_FADE;
				else
					warpAlpha = 0;
				
				for(StellarObject s: Main.getSpobList()){
					s.step(this);
				}
				for(Ship s: Main.getShipList()){
		        	s.step(this);
		        }
			}
		}
		
		charTyped = false;
		
		for(int i = 0; i < keyState.length; i++){
			if(keyState[i] == KEY_PRESSED)
				keyState[i] = KEY_HELD;
		}
			
		for(int i = 0; i < mouseState.length; i++){
			if(mouseState[i] == KEY_PRESSED)
				mouseState[i] = KEY_HELD;
		}

		for(int i = 0; i < keyState.length; i++){
			if(keyState[i] == KEY_JUST_RELEASED)
				keyState[i] = KEY_RELEASED;
		}
			
		for(int i = 0; i < mouseState.length; i++){
			if(mouseState[i] == KEY_JUST_RELEASED)
				mouseState[i] = KEY_RELEASED;
		}
	}
	
	public void draw() {
		background(0);
		
		seedWindow.draw();
			
        step();
        
        if(!gamePaused){
    		for(int bkgIndex = 0; bkgIndex < Main.backgroundList.length; bkgIndex++){
    			if(Main.backgroundList[bkgIndex] != null){
    				int numColTiles = (int)Math.ceil((double)WIN_WIDTH / (double)Main.backgroundList[bkgIndex].width) + 2;
    				int numRowTiles = (int)Math.ceil((double)WIN_HEIGHT / (double)Main.backgroundList[bkgIndex].height) + 2;
    				for(int i = 0; i < numColTiles; i++){
    					for(int j = 0; j < numRowTiles; j++)
    						image(Main.backgroundList[bkgIndex],
    						i * Main.backgroundList[bkgIndex].width - (Main.backgroundX[bkgIndex] % Main.backgroundList[bkgIndex].width) - Main.backgroundList[bkgIndex].width,
    						j * Main.backgroundList[bkgIndex].height - (Main.backgroundY[bkgIndex] % Main.backgroundList[bkgIndex].height) - Main.backgroundList[bkgIndex].height);	
    				}
    			}
    		}
    		
    		if(warpAlpha > 0){
    			fill(255, 255, 255, (int)(255 * warpAlpha));
    			rect(0, 0, WIN_WIDTH, WIN_HEIGHT);
    		}
    		
    		if(currentSystem != null){
		        for(StellarObject spob: currentSystem.getSpobs()){
		        	spob.draw(this);
		        }
    		
		        for(Ship s: Main.getShipList()){
		        	s.draw(this);
		        }
    		}
        }
        else
        	starMap.draw();
	}
	
	public static void placeFactions(StarSystem src, Faction faction, int range){
		if(range > 0){
			for(StarSystem s: src.getLinks()){
				s.setGovernment(faction);
				//s.setColor(faction.getColor());
				placeFactions(s, faction, range - 1);
			}
		}
	}
	
	public static void addBodies(StarSystem system, int numBodies, StellarObject host){
		//double desiredTemperature = (rand.nextDouble() * MAX_TEMPERATURE * TEMPERATURE_JITTER * 2) + (MAX_TEMPERATURE * (1.0 - TEMPERATURE_JITTER));
		long nextOrbit = (long) ((host.getMass() * host.getMass() * 0.05 * ((double)(Main.rand.nextInt(10) + 1))) * Util.AU);//(long) ((((double) Util.AU) * host.getBodeConstant()) * (Util.randomDouble(0.9, 1.1)));		

		long minimumOrbit = (long)(Math.sqrt(host.getLuminosity()) * 0.025);
		if(host.getSubType() == StarSubType.WHITE_DWARF){
			int rollBonus = 0;
			if(host.getMass() >= 0.6)
				rollBonus += 1;
			if(host.getMass() >= 0.9)
				rollBonus += 3;
			int roll = rand.nextInt(10) + 1 + rollBonus;
			if(roll <= 4)
				minimumOrbit = (long) (2 * Util.AU);
			else if(roll >= 5 && roll <= 8)
				minimumOrbit = (long) (4 * Util.AU);
			else if(roll >= 9 && roll <= 11)
				minimumOrbit = (long) (6 * Util.AU);
			else if(roll >= 12)
				minimumOrbit = (long) (10 * Util.AU);
		}
		for(int i = 0; i < numBodies; i++){
			StellarObject planet;
			if((rand.nextInt(100) > 5) || sunCount >= MAX_SUNS_PER_SYSTEM){
				planet = generatePlanet(host.getName() + " " + Util.intToNumerals(i + 1), nextOrbit, host, system);
				planet.setEffectiveTemperature(Util.calculateTemperature(planet));	
			}
			else{
				sunCount++;
				planet = generateSun(system, "Sun " + (sunCount + 1), nextOrbit, host, null);
				System.out.println("Sun: " + planet.getName() + " Number: " + sunCount);
			}
			
			nextOrbit = (long) (((((double) nextOrbit) / Util.AU) * (1.1 + (((double)(Main.rand.nextInt(10) + 1)) / 10.0)) + (0.1)) * Util.AU);
					
			//System.out.println("New Planet Distance: " + minimumOrbit);
			if(nextOrbit >= minimumOrbit && planet != null && planet.getSubType() != PlanetSubType.ASTEROID_BELT && planet.getSubType() != PlanetSubType.EMPTY_ORBIT){
				host.getOrbitingBodies().add(planet);
				spobList.add(planet);
			}
		}
	}
	
	public static void addMoons(int numMoons, StellarObject host){
		int moonRoll = Main.rand.nextInt(10);
		if(host.getOrbitalDistance() > Math.sqrt(host.getHost().getLuminosity()) * 4)
			moonRoll += 5;
		
		if(host.getSubType() == PlanetSubType.CHUNK){
			if(moonRoll >= 10)
				numMoons = 1;
		}
		else if(host.getSubType() == PlanetSubType.TERRESTRIAL || host.getSubType() == PlanetSubType.SUPER_TERRESTRIAL || host.getSubType() == PlanetSubType.DWARF){
			if(moonRoll >= 6 && moonRoll <= 7)
				numMoons = 1;
			else if(moonRoll >= 8 && moonRoll <= 9)
				numMoons = rand.nextInt(1) + 1;
			else if(moonRoll >= 10 && moonRoll <= 13)
				numMoons = rand.nextInt(5) + 1;
			else if(moonRoll >= 8 && moonRoll <= 14)
				numMoons = rand.nextInt(10) + 1;
		}
		else if(host.getSubType() == PlanetSubType.JOVIAN || host.getSubType() == PlanetSubType.SUPER_JOVIAN){
			if(moonRoll <= 5)
				numMoons = rand.nextInt(5) + 1;
			else if(moonRoll >= 6 && moonRoll <= 7)
				numMoons = rand.nextInt(10) + 1;
			else if(moonRoll >= 8 && moonRoll <= 9)
				numMoons = rand.nextInt(10) + 6;
			else if(moonRoll >= 10 && moonRoll <= 13)
				numMoons = rand.nextInt(10) + 11;
			else if(moonRoll == 14)
				numMoons = rand.nextInt(10) + 21;		
		}
		
		long minimumOrbit = 0;
		if(host == null)
			return;
		for(int i = 0; i < numMoons; i++){
			int distanceRoll = rand.nextInt(10) + 1;
			if(distanceRoll <= 4)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) * 0.5 + (host.getDiameter() / 2));
			else if(distanceRoll >= 5 && distanceRoll <= 6)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) + ((host.getDiameter() / 2) * 6));
			else if(distanceRoll >= 7 && distanceRoll <= 8)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) * 3.0 + ((host.getDiameter() / 2) * 16));
			else if(distanceRoll == 9)
				minimumOrbit = (long) (((double)(rand.nextInt(100) + 1)) * 3.0 + ((host.getDiameter() / 2) * 45));
			
			StellarObject moon = generateMoon(host.getName() + " - Moon " + (i + 1), minimumOrbit, host);
			//minimumOrbit += moon.getOrbitalDistance();
			host.getOrbitingBodies().add(moon);
		}
	}
	
	public static StellarObject generateSun(StarSystem system, String name, long minimumOrbit, StellarObject host, StarSubType desiredType){
		int solarType = 0;
		
		if(desiredType == null)
			solarType = rand.nextInt(StarSubType.values().length);
		else
			solarType = desiredType.ordinal();
		
		//double diameter = (rand.nextDouble() * (StarSubType.maxRadii[solarType] - StarSubType.minRadii[solarType]) + StarSubType.minRadii[solarType]) * 2;
		int orbitalVelocity = rand.nextInt(100) + 5;
		//long minimumOrbit = 0;//(spobList.size() > planetNum) ? spobList.get(planetNum).getOrbitalDistance() : 0;
		long orbitalDistance = 0;
		
		double orbitalTheta = rand.nextDouble() * 2 * Math.PI;
		double luminosity = 0.0;
		
		StellarObject ret = SolarSystemGenerator.generateStar(name, host);
		
		//int coreBodies = rand.nextInt(StarSubType.maxPlanets[solarType] - StarSubType.minPlanets[solarType]) + StarSubType.minPlanets[solarType];
		int bodiesRoll = rand.nextInt(10) + 1;
		int coreBodies = rand.nextInt(10);
		
		if(ret.getSubType() == StarSubType.ORANGE || ret.getSubType() == StarSubType.ORANGE_SUB_GIANT ||ret.getSubType() == StarSubType.ORANGE_GIANT){
			if(ret.getSpectralClass() >= 5)
				bodiesRoll += 1;
		}
		
		if(ret.getSubType() == StarSubType.RED ||ret.getSubType() == StarSubType.RED_GIANT){
			if(ret.getSpectralClass() <= 4)
				bodiesRoll += 2;
			else if(ret.getSpectralClass() >= 5)
				bodiesRoll += 3;
		}
		if(ret.getSubType() == StarSubType.BROWN_DWARF)
			bodiesRoll += 5;
		
		bodiesRoll -= system.getRelativeAbundance();
		
		if(bodiesRoll == 1)
			coreBodies = rand.nextInt(10) + 11;
		else if(bodiesRoll >= 2 || bodiesRoll <= 5)
			coreBodies = rand.nextInt(10) + 6;
		else if(bodiesRoll >= 6 || bodiesRoll <= 7)
			coreBodies = rand.nextInt(10) + 1;
		else if(bodiesRoll >= 8 || bodiesRoll <= 9)
			coreBodies = (rand.nextInt(5) + 1);
		else
			coreBodies += 0;
		
		minimumOrbit = (long) (ret.getMass() * ret.getMass() * 0.05 * (Main.rand.nextInt(10) + 1));
		//if(host == null){
		//	orbitalVelocity = 0;
		//	minimumOrbit = 0;
		//	orbitalDistance = 0;
		//	orbitalTheta = 0;
		//}
		//else{
		//	 orbitalDistance = (2L * minimumOrbit) + (rand.nextLong() % (long)(minimumOrbit / 10));			
		//}
		
		//diameter *=	Util.SOLAR_RADIUS;
		
		//StellarObject ret = new StellarObject(name, (int)diameter, orbitalDistance, orbitalVelocity, orbitalTheta, Type.STAR, StarSubType.values()[solarType]);
		//ret.setHost(host);
		//ret.setEffectiveTemperature(rand.nextInt(StarSubType.maxTemp[solarType] - StarSubType.minTemp[solarType]) + StarSubType.minTemp[solarType]);
		//luminosity = Util.calculateLuminosity(ret);
		//ret.setLuminosity(luminosity);
		
		System.out.println(name + " luminosity: " + luminosity);
		System.out.println("classification: " + StarSubType.values()[solarType]);
		System.out.println("effective temperature: " + ret.getEffectiveTemperature()+" C");
		System.out.println("radius: " + ret.getDiameter() / 2 + " kilometers");
		System.out.println("Planets: " + coreBodies + "\n");
		
		long minDistance = (long) (Util.MIN_SOLAR_HABITABLE_ZONE * Math.sqrt(ret.getLuminosity()) * Util.AU);
		long maxDistance = (long) (Util.MAX_SOLAR_HABITABLE_ZONE * Math.sqrt(ret.getLuminosity()) * Util.AU);

		long avgDistance = (minDistance + maxDistance) / 2;
		
		spobList.add(ret);
		addBodies(system, coreBodies, ret);
		//assignPlanetsToStar(ret, system);
		
		//there is a 50% chance that the solar generator will force a star to have a habitable planet
		if(rand.nextDouble() > 1.5){
			StellarObject closestSpob = null;
			
			for(StellarObject spob: ret.getOrbitingBodies()){
				if(closestSpob == null || Math.abs(spob.getOrbitalDistance() - avgDistance) < Math.abs(closestSpob.getOrbitalDistance() - avgDistance))
					closestSpob = spob;
			}
			
			if(closestSpob != null && (closestSpob.getSubType() == PlanetSubType.TERRESTRIAL || closestSpob.getSubType() == PlanetSubType.DWARF || closestSpob.getSubType() == PlanetSubType.SUPER_TERRESTRIAL)){
				closestSpob.setOrbitalDistance(Util.distanceOfEffectiveTemperature(Main.MEAN_TEMP, luminosity, closestSpob.getAlbedo()));
				closestSpob.moveToOrbit();
				closestSpob.setName(closestSpob.getName() + " (fixed)");
				
				closestSpob.update();				
			}
		}
		
		return ret;
	}
	
	public static StellarObject generatePlanet(String name, long minimumOrbit, StellarObject host, StarSystem system){		
		StellarObject ret = BodyGenerator.generateEarthlike(name, minimumOrbit, host, system);
		//generateOre(ret);
		addMoons(rand.nextInt(3), ret);
		return ret;
	}

	public static StellarObject generateMoon(String name, long minimumOrbit, StellarObject host){	
		int diameter = rand.nextInt(10000) + 1000;
		int orbitalVelocity = rand.nextInt(100) + 5;
		
		long orbitalDistance = (rand.nextInt(5000000)) + 2500000 + minimumOrbit;
		double orbitalTheta = rand.nextDouble() * 2 * Math.PI;
		
		StellarObject ret = new StellarObject(name, diameter, orbitalDistance, orbitalVelocity, orbitalTheta, Type.MOON, PlanetSubType.DWARF);
		ret.setHost(host);
		ret.setAlbedo(rand.nextDouble());
		
		ret.setEffectiveTemperature(Util.calculateTemperature(ret));
		
		spobList.add(ret);
		//System.out.println(name + " distance: " + orbitalDistance + " kilometers");
		generateOre(ret);
		
		return ret;
	}

	public static void generateOre(StellarObject spob){		
		int surfaceMineralTotal = 0;
		int atmosphericPressure = 0;
		double albedo = 0;
		double effectiveTemperature = spob.getEffectiveTemperature();
		
		for(Mineral m: initMin){
			if(rand.nextInt(100) < m.getProbability() && effectiveTemperature < m.getPhaseChanges()[1]){
				int surfaceMineralAmt = rand.nextInt((int)m.getRelativeAmount()) + (int)(m.getRelativeAmount() / 2);
				spob.setSurfaceMineralDeposit(m, (double)surfaceMineralAmt);
				surfaceMineralTotal += surfaceMineralAmt;
			}
			if(rand.nextInt(100) < m.getProbability() && effectiveTemperature >= m.getPhaseChanges()[1]){
				int atmAmt = rand.nextInt((int)m.getRelativeAmount()) + (int)(m.getRelativeAmount() / 2);
				spob.setAtmosphericGas(m, (double)atmAmt);
				atmosphericPressure += atmAmt;
			}
			if(rand.nextInt(100) < 25){
				spob.setMineralDeposit(m, rand.nextInt(10000000) + 45000);
				String unit;
				//System.out.println("*\t" + m.getNames()[0] + " - " + spob.getMinerals().get(m) + " " + unit);
			}
		}
		for(Mineral m: spob.getSurfaceMinerals().keySet()){
			int phase = 0;
			double[] phaseChanges = m.getPhaseChanges();
			
			//System.out.println("Temperature: " + effectiveTemperature + " Melting point: " + phaseChanges[0] + " Boiling point: " + phaseChanges[1] + " ");
			//System.out.println("total minerals: " + surfaceMineralTotal);
			
			NumberFormat formatter = new DecimalFormat("#0.0000");
			double percentage = Double.parseDouble(formatter.format((spob.getSurfaceMinerals().get(m) / (double)surfaceMineralTotal)));
			
			spob.setSurfaceMineralDeposit(m, percentage);
			
			if(effectiveTemperature >= phaseChanges[0])
				phase = 1;
			if(effectiveTemperature >= phaseChanges[1])
				phase = 2;
			
			albedo += percentage * m.getAlbedos()[phase];
			//System.out.println("*\t" + m.getNames()[phase] + " - " + (spob.getSurfaceMinerals().get(m) * 100) + "%");
		}
		
		for(Mineral m: spob.getAtmosphericMinerals().keySet()){
			int phase = 0;
			double[] phaseChanges = m.getPhaseChanges();
			
			//System.out.println("Temperature: " + effectiveTemperature + " Melting point: " + phaseChanges[0] + " Boiling point: " + phaseChanges[1] + " ");
			//System.out.println("total minerals: " + surfaceMineralTotal);
			
			NumberFormat formatter = new DecimalFormat("#0.0000");
			double percentage = Double.parseDouble(formatter.format((spob.getAtmosphericMinerals().get(m) / (double)atmosphericPressure)));
			
			spob.setAtmosphericGas(m, percentage);
			
			if(effectiveTemperature >= phaseChanges[0])
				phase = 1;
			if(effectiveTemperature >= phaseChanges[1])
				phase = 2;
			
			//albedo += percentage * m.getAlbedos()[phase];
			//System.out.println("*\t" + m.getNames()[phase] + " - " + (spob.getAtmosphericMinerals().get(m) * 100) + "%");
		}
		spob.setAlbedo(albedo);
		spob.setEffectiveTemperature(Util.calculateTemperature(spob));
	}
	
	public static LinkedList<StarSystem> getSystemsInRange(int min, int max, StarSystem src){
		LinkedList<StarSystem> ret = new LinkedList<StarSystem>();
		
		for(StarSystem s: systemList){
			if(s != src){
				double dist = Util.pointDistance(src.getX(), src.getY(), s.getX(), s.getY());
				if(dist >= min && dist <= max)
					ret.add(s);
			}
		}
		
		return ret;
	}
	
	public static boolean sysLinkIntersect(StarSystem src, StarSystem trg){
		Line2D linkOne = new Line2D.Float((float) src.getX(), (float) src.getY(), (float) trg.getX(), (float) trg.getY());
		
		LinkedList<StarSystem> systemListCopy = (LinkedList<StarSystem>) systemList.clone();
		systemListCopy.remove(src);
		systemListCopy.remove(trg);
		
		for(StarSystem s: systemListCopy){
			for(StarSystem l: s.getLinks()){
				Line2D linkTwo = new Line2D.Float((float) s.getX(), (float) s.getY(), (float) l.getX(), (float) l.getY());
				if(linkTwo.intersectsLine(linkOne) && systemListCopy.contains(l)){
					System.out.println("Warning: " + src.getName() + " - " + trg.getName() + " collides with " + s.getName() + " - " + l.getName());
					return true;
				}
			}
		}
		return false;
	}

	public static boolean sysLinkIntersect(StarSystem src, StarSystem trg, LinkedList<StarSystem> altSystemList){
		Line2D linkOne = new Line2D.Float((float) src.getX(), (float) src.getY(), (float) trg.getX(), (float) trg.getY());
		LinkedList<StarSystem> systemListCopy = (LinkedList<StarSystem>) altSystemList.clone();
		systemListCopy.remove(src);
		systemListCopy.remove(trg);
		
		for(StarSystem s: systemListCopy){
			for(StarSystem l: s.getLinks()){
				Line2D linkTwo = new Line2D.Float((float) s.getX(), (float) s.getY(), (float) l.getX(), (float) l.getY());
				if(linkTwo.intersectsLine(linkOne) && systemListCopy.contains(l)){
					System.out.println("Warning: " + src.getName() + " - " + trg.getName() + " collides with " + s.getName() + " - " + l.getName());
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean sysLinkIntersect(StarSystem src, StarSystem trg, StarSystem[] altSystemList){
		Line2D linkOne = new Line2D.Double(src.getX(), src.getY(), trg.getX(), trg.getY());
		
		StarSystem[] systemListCopy = (StarSystem[]) altSystemList.clone();
		
		for(StarSystem s: systemListCopy){
			if(s == src || s == trg)
				continue;
			for(StarSystem l: s.getLinks()){
				if(l == src || l == trg)
					continue;
				Line2D linkTwo = new Line2D.Double(s.getX(), s.getY(), l.getX(), l.getY());
				if(linkTwo.intersectsLine(linkOne)){
					System.out.println("Warning: " + src.getName() + " - " + trg.getName() + " collides with " + s.getName() + " - " + l.getName());
					return true;
				}
			}
		}
		return false;	
	}
	
	public static LinkedList<StarSystem> searchSystemList(String terms, boolean drawHidden){
		LinkedList<StarSystem> candidates = new LinkedList<StarSystem>();

		for(StarSystem s: systemList){
			if(!s.isExplored() && !drawHidden)
				continue;
			if(s.getName().toLowerCase().contains(terms.toLowerCase())){
				candidates.add(s);
			}
		}
		return candidates;
	}
	
	public static StarSystem getCurrentSystem(){
		return currentSystem;
	}
	
	public static InitializeMinerals getInitMinerals(){
		return initMin;
	}
	
	public static InitializePlanetTypes getInitPlanetTypes(){
		return planetTypes;
	}
	
	public static LinkedList<StellarObject> getSpobList(){
		return spobList;
	}
	
	public static Ship getPlayerShip(){
		return playerShip;
	}
	
	public static void setTargetSystem(StarSystem newTarget){
		targetSystem = newTarget;
	}
	
	public static StarSystem getTargetSystem(){
		return targetSystem;
	}
	
	public static HashMap<String, Integer> getSystemNameList(){
		return systemNameList;
	}
	
	public static LinkedList<Ship> getShipList() {
		return shipList;
	}
	
	public static void jump(StarSystem target){
		//System.out.println(target);
		currentSystem = target;
		System.out.println("Jumping to the " + target.getName() + " system");
		target.explore();
		targetSystem = null;
		setWarpAlpha(1);
	}

	public static void setWarpAlpha(double alpha) {
		warpAlpha = alpha;
	}
	
	public static LinkedList<StarSystem> getSystemList(){
		return systemList;
	}
	
	public boolean charTyped(){
		return charTyped;
	}
	
	public char lastChar(){
		return lastChar;
	}
	
	public void keyTyped(){
		lastChar = key;
		charTyped = true;
				
		if((int)key != KeyEvent.VK_BACK_SPACE)
			keyString += ((char)key);
		else if(keyString.length() > 0)
			keyString = keyString.substring(0, keyString.length() - 1);
		//System.out.println("typed " + keyString + " " + (int)key);
	}
	
	public void keyPressed(){
		if(keyState[keyCode] == KEY_RELEASED)
			keyState[keyCode] = KEY_PRESSED;
	}
	
	public void keyReleased(){
		keyState[keyCode] = KEY_JUST_RELEASED;
	}

	public void mousePressed(){
		if(mouseState[mouseButton] == KEY_RELEASED)
			mouseState[mouseButton] = KEY_PRESSED;
	}
	
	public void mouseReleased(){
		mouseState[mouseButton] = KEY_JUST_RELEASED;
	}
	
	public int[] getKeyState(){
		return keyState;
	}

	public static void setKeyString(String keyString){
		Main.keyString = keyString;
	}
	
	public static String getKeyString(){
		return keyString;
	}
	
	public boolean getGamePaused(){
		return gamePaused;
	}

	public int[] getMouseState() {
		return mouseState;
	}
	
	@Override
	public void initializeGui() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disposeGui() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideGui(GuiObject obj) {
		obj.hide();
	}

	@Override
	public void showGui(GuiObject obj) {
		obj.show();
	}

	@Override
	public void eventCallback(GuiObject event) {
		if(event == seedWindow){
			NUM_ROOTS = seedWindow.getNumRoots();
			beginGeneration(seedWindow.getSeed().hashCode());
		}
	}

	@Override
	public void switchFocus(GuiObject target) {
		
	}
}
