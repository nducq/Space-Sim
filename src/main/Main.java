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
	/* GAME CONSTANTS */
	
	//the speed a ship must reach while "warping" to actually complete a hyperspace jump
	public static final double WARP_SPEED = 700;
	//public static final double WARP_DISTANCE = 2000;
	//the distance from the system center a ship arrives at after jumping in
	public static final double ARRIVAL_DISTANCE = Util.AU * 40 / 100;
	//the distance from the target body a ship arrives at after performing a local jump
	public static final double SHORT_JUMP_ARRIVAL_DISTANCE = 100000;
	//the amount of opacity the "warp flash" loses per frame
	public static final double WARP_FADE = 0.0075;
	//the height and width of the window (ignored for full screen)
	public static final int WIN_WIDTH = 960;
	public static final int WIN_HEIGHT = 720;
	
	//the hottest a planet can during system generation
	public static final double MAX_TEMPERATURE = 2000;
	//provides a "jitter" effect to the maximum temperature (i.e. 0.2 means the maximum temperature for some new body is 0.8 - 1.2 times MAX_TEMPERATURE
	public static final double TEMPERATURE_JITTER = 0.2;
	//the maximum greenhouse factor
	public static final double MAX_GREENHOUSE_FACTOR = 3.35;
	
	//the optimal temperature for human habitation
	public static final double MEAN_TEMP = 14.0;
	//the maximum deviation from the ideal before temperature begins affecting colony cost
	public static final double TEMP_DEVIATION = 24.0;
	
	//the highest atmospheric pressure can get before it begins affecting colony cost
	public static final double MAX_PRESSURE = 4.0;
	
	//the optimal oxygen concentration for human habitation
	public static final double MEAN_OXYGEN = 0.2;
	//the maximum deviation from the ideal before concentration begins affecting colony cost (oxygen poisoning)
	public static final double OXYGEN_DEVIATION = 0.1;
	
	//maximum number of suns per system
	public static final int MAX_SUNS_PER_SYSTEM = 0;
	//unused
	public static final int MAX_SYSTEMS = 2000;
	//default number of roots to generate
	public static int NUM_ROOTS = 30;
	
	/* DYNAMIC GAME VARIABLES */
	
	//declare the list of background resources
	public static PImage[] backgroundList;
	//declare the coordinate arrays for said background resources
	public static int[] backgroundX;
	public static int[] backgroundY;
	
	//declare a static random object (this should force arbitrary seeds to generate the same exact galaxy every time)
	public static final Random rand = new Random();
	
	//declare lists of star graphics
	public static PImage[] redStarList;
	public static PImage[] orangeStarList;
	public static PImage[] yellowStarList;
	public static PImage[] whiteStarList;
	public static PImage[] blueWhiteStarList;
	
	//declare lists of terrestrial planet graphics
	public static PImage[] dwarfList;
	public static PImage[] terrestrialList;
	public static PImage[] superTerrestrialList;
	
	//declare lists of jovian planet graphics
	public static PImage[] gasGiantList;
	public static PImage[] superGasGiantList;
	public static PImage[] asteroidList;
	
	//declare the list of target border resources
	public static PImage[] targetBorder;
	
	//this counts the number of suns per solar system *DEPRECATED*
	private static int sunCount;
	
	//declare a pointer 
	private static Ship playerShip;
	private static StarSystem currentSystem;
	private static StarSystem targetSystem;

	//declare the seed window
	private static SeedWindow seedWindow;
	
	//declare the material initializer
	private static InitializeMaterials initMat;
	//declare the mineral initializer
	private static InitializeMinerals initMin;
	//declare the faction initializer
	private static InitializeFactions initFact;
	//declare the material initializer
	private static InitializePlanetTypes planetTypes;
	
	//declare the list of all space objects
	private static LinkedList<StellarObject> spobList;
	//declare the list of all ships
	private static LinkedList<Ship> shipList;
	//decalre the list of all systems
	private static LinkedList<StarSystem> systemList;
	
	//declare the map containing every eligible system name
	private static HashMap<String, Integer> systemNameList;
	
	//declare the values of the camera offsets
	public static long viewXView;
	public static long viewYView;
	
	//declare the container value of the warp flash opacity
	public static double warpAlpha;
	
	//declare a pointer to a starMap object
	private static Map starMap;
	
	//initialize lists storing keyboard and mouse states
	private int keyState[] = new int[512];
	private int mouseState[] = new int[256];
	
	//this boolean is used to indicate whether or not the game is actually "paused"
	private boolean gamePaused = false;

	//keyString contains a list of all printable characters that are typed in as the game runs
	private static String keyString = "";
	//stores the value of the last character typed
	private char lastChar = '\0';
	//indicates whether or not a character was typed in the last frame
	private boolean charTyped = false;

	//these are the values that should be assigned to the keyboard and mouse state arrays at the index of whichever key you're testing
	//(i.e. if(keyboardState[KeyEvent.VK_UP] == KEY_HELD) tests if the user is currently holding the up key):
	
	//key has been pressed once in the last frame but is not being held
	public static final int KEY_PRESSED = 1;
	//key is being held
	public static final int KEY_HELD = 2;
	//key is not being held
	public static final int KEY_RELEASED = 0;
	//the key has just been released in the last frame
	public static final int KEY_JUST_RELEASED = 3;
	
	/* 
	 * Since a PApplet uses its own initializers, this shouldn't do anything besides initialize the current PApplet
	 */
	static public void main(String args[]) {
		//initialize the PApplet
		PApplet.main("main.Main");
	}
	
	/*
	 * This prepares the opengl settings the PApplet will use (do not call this)
	 */
	public void settings(){
		//smooth(0);
		//set the size of the window to the WIN_WIDTH and WIN_HEIGHT constants (ignored for full screen)
		size(WIN_WIDTH, WIN_HEIGHT);
		//fullScreen();
	}
	
	/*
	 * This function is called automatically after settings() during PApplet initialization and should be used for
	 * initializing game data
	 */
	public void setup(){
		//properly initialize materials and minerals
		initMat = new InitializeMaterials();
		initMin = new InitializeMinerals();
		initFact = new InitializeFactions();
		planetTypes = new InitializePlanetTypes();
		
		//initialize star names hash map;
		systemNameList = new HashMap<String, Integer>();

		//Instantiate our random number generator
		//rand = new Random();
		//rand.setSeed(("asdfasdf").hashCode());
		
		//identify graphics directories
		File[] redStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Red/").listFiles();
		File[] orangeStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Orange/").listFiles();
		File[] yellowStarGraphicsFolder = new File("./src/graphics/sprites/Suns/Yellow/").listFiles();
		File[] whiteStarGraphicsFolder = new File("./src/graphics/sprites/Suns/White/").listFiles();
		File[] blueWhiteStarGraphicsFolder = new File("./src/graphics/sprites/Suns/BlueWhite/").listFiles();
		
		File[] jovianGraphicsFolder = new File("./src/graphics/sprites/Planets/Jovian/").listFiles();
		File[] terrestrialGraphicsFolder = new File("./src/graphics/sprites/Planets/Terrestrial/").listFiles();
		File[] asteroidGraphicsFolder = new File("./src/graphics/sprites/Asteroids/").listFiles();
		
		File systemNames = new File("./src/data/system names.txt");
		
		//initialize background resource and coordinate arrays
		backgroundList = new PImage[10];
		backgroundX = new int[10];
		backgroundY = new int[10];
		
		//initialize sprite lists
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
		
		//load target border sprites
		targetBorder[0] = loadImage("./src/graphics/sprites/Gui/planetTL.bmp"); 
		targetBorder[1] = loadImage("./src/graphics/sprites/Gui/planetTR.bmp");
		targetBorder[2] = loadImage("./src/graphics/sprites/Gui/planetBL.bmp");
		targetBorder[3] = loadImage("./src/graphics/sprites/Gui/planetBR.bmp");

		//load terrestrial, dwarf, and chunk planet sprites
		for(int i = 0; i < terrestrialList.length; i++){
			//load graphics
			dwarfList[i] = loadImage(terrestrialGraphicsFolder[i].getPath());
			terrestrialList[i] = dwarfList[i].copy();
			superTerrestrialList[i] = dwarfList[i].copy();
			
			//scale graphics to an appropriate size for their class
			dwarfList[i].resize(75, 75);
			terrestrialList[i].resize(150, 150);
			superTerrestrialList[i].resize(300, 300);
			
			//remove each sprite's background color
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
			//update the pixel arrays
			dwarfList[i].updatePixels();
			terrestrialList[i].updatePixels();
			superTerrestrialList[i].updatePixels();
		}
		
		//load jovian and super jovian planet sprites
		for(int i = 0; i < gasGiantList.length; i++){
			//load graphics
			gasGiantList[i] = loadImage(jovianGraphicsFolder[i].getPath());
			superGasGiantList[i] = gasGiantList[i].copy();
			
			//scale images to an appropriate size for their class
			gasGiantList[i].resize(500, 500);
			superGasGiantList[i].resize(800, 800);
			
			//remove each sprite's background color
			for(int j = gasGiantList[i].pixels.length - 1; j > 0; j--){
				if (gasGiantList[i].pixels[j] == gasGiantList[i].pixels[0])
					gasGiantList[i].pixels[j] = color(0, 0, 0 , 0);
			}
			for(int j = superGasGiantList[i].pixels.length - 1; j > 0; j--){
				if (superGasGiantList[i].pixels[j] == superGasGiantList[i].pixels[0])
					superGasGiantList[i].pixels[j] = color(255, 0, 255, 0);
				
			}
			//update the pixel arrays
			gasGiantList[i].updatePixels();
			superGasGiantList[i].updatePixels();
		}
		
		//load asteroid sprites
		for(int i = 0; i < asteroidList.length; i++){
			//load graphics
			asteroidList[i] = loadImage(asteroidGraphicsFolder[i].getPath());
			
			//scale image to an appropriate size
			asteroidList[i].resize(150, 150);
			
			//remove each sprite's background color
			for(int j = asteroidList[i].pixels.length - 1; j > 0; j--){
				if (asteroidList[i].pixels[j] == asteroidList[i].pixels[0])
					asteroidList[i].pixels[j] = color(255, 0, 255, 0);
			}
			
			//update the pixel array
			asteroidList[i].updatePixels();
		}

		/*
		 * Rinse, lather, and repeat everything that was just done for each star type
		 */
		
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
		
		//load the background resources
		backgroundList[0] = loadImage("graphics/backgrounds/bkg_stars1.png"
				);
		backgroundList[1] = loadImage("graphics/backgrounds/bkg_stars1.png");
		backgroundList[2] = loadImage("graphics/backgrounds/bkg_stars2.png");
		backgroundList[3] = loadImage("graphics/backgrounds/bkg_stars3.png");
		
		//initialize the X coordinates
		backgroundX[0] = 0;
		backgroundX[1] = 0;
		backgroundX[2] = 0;
		backgroundX[3] = 0;

		//initialize the Y coordinates
		backgroundY[0] = 0;
		backgroundY[1] = 0;
		backgroundY[2] = 0;
		backgroundY[3] = 0;
		
		//create a buffered reader to read system names
		try{
			BufferedReader br = new BufferedReader(new FileReader(systemNames));
			String line = br.readLine();
			while(line != null){
				//"#"'s precede comments, names and distances are separated with ","'s
				if(line.length() != 0 && line.charAt(0) != '#'){
					String[] lineArr = line.split(", ");
					//and the names and distances to the system list map
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
		frameRate(60);

		//create and display the seed window
		seedWindow = new SeedWindow(200, 200, this, this);
		seedWindow.show();
	}
	
	/*
	 * This function facilitates generation of the game world by creating the star map
	 */
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
		
		//generate the star map around the root
		StarMapGenerator.generateStarMap(currentSystem);
		
		//place factions (this should be removed)
		for(Faction f: initFact){
			StarSystem hq = systemList.get(rand.nextInt(systemList.size()));
			//placeFactions(hq, f, f.getRange());
		}
		
		//create the player ship
		shipList = new LinkedList<Ship>();
		playerShip = new Ship(true);
		shipList.add(playerShip);
	}
	
	/*
	 * Step functions are called once every frame and are used to separate game logic from proper drawing code.
	 * In order for an object to be drawn or processed, its step/draw functions must be reachable from the "Main" object
	 */
	public void step(){
		//process the seed window
		seedWindow.step();
		
		//check if the enter key was pressed
		if(keyState[KeyEvent.VK_ENTER] == KEY_PRESSED){
			//toggle the pause state
			gamePaused = !gamePaused;
			
			//game was paused, create a star map and center its view around the position of the player ship
			if(gamePaused)
				starMap = new Map((WIN_WIDTH / 2) - ((int)playerShip.getX() / 2000), (WIN_HEIGHT / 2) - ((int)playerShip.getY() / 2000), this);
			//game was unpaused, update update target settings and dispose of the map
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
		
		//ensure that the list of stellar objects exists before processing ships and stellar objects
		if(Main.getSpobList() != null){
			//process the map's step event if the game is paused
			if(gamePaused) {
				starMap.step();
			}
			//otherwise update the warp flash opacity and process the step events of planets and ships
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
		
		//reset the charTyped flag
		charTyped = false;
		
		//a key can only be pressed for one frame, otherwise it's considered 'held'
		for(int i = 0; i < keyState.length; i++){
			if(keyState[i] == KEY_PRESSED)
				keyState[i] = KEY_HELD;
		}
			
		//the same applies to mouse buttons
		for(int i = 0; i < mouseState.length; i++){
			if(mouseState[i] == KEY_PRESSED)
				mouseState[i] = KEY_HELD;
		}

		//set keys that have just been released to completely released after one frame
		for(int i = 0; i < keyState.length; i++){
			if(keyState[i] == KEY_JUST_RELEASED)
				keyState[i] = KEY_RELEASED;
		}
		
		//do the same for mouse buttons
		for(int i = 0; i < mouseState.length; i++){
			if(mouseState[i] == KEY_JUST_RELEASED)
				mouseState[i] = KEY_RELEASED;
		}
	}
	
	/*
	 * Step functions are called once every frame and are used to separate game logic from proper drawing code.
	 * In order for an object to be drawn or processed, its step/draw functions must be reachable from the "Main" object.
	 * Draw functions should only be to actually draw things, game logic belongs in "step"
	 */
	public void draw() {
		//draw a black background
		background(0);
		
		//draw the seedwindow
		seedWindow.draw();
			
		//call the step event (this should only be necessary for main, other classes won't need to call 'draw' from 'step' or vice versa)
        step();
        
        //draw the backgrounds if the game is unpaused
        if(!gamePaused){
        	//iterate through each background, drawing only the ones that aren't 'null'
    		for(int bkgIndex = 0; bkgIndex < Main.backgroundList.length; bkgIndex++){
    			if(Main.backgroundList[bkgIndex] != null){
    				
    				//tile the backgrounds so that the fill up the enter game window, but offset them by their X and Y coordinates
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
    		
    		//draw the warp flash
    		if(warpAlpha > 0){
    			fill(255, 255, 255, (int)(255 * warpAlpha));
    			rect(0, 0, width, height);
    		}
    		
    		//draw the planets and the ship
    		if(currentSystem != null){
		        for(StellarObject spob: currentSystem.getSpobs()){
		        	spob.draw(this);
		        }
    		
		        for(Ship s: Main.getShipList()){
		        	s.draw(this);
		        }
    		}
        }
        //if the game is paused instead draw the star map
        else
        	starMap.draw();
	}
	
	/*public static void placeFactions(StarSystem src, Faction faction, int range){
		if(range > 0){
			for(StarSystem s: src.getLinks()){
				s.setGovernment(faction);
				//s.setColor(faction.getColor());
				placeFactions(s, faction, range - 1);
			}
		}
	}*/
	
	/*
	 * This function generates 'numbodies' planets and adds them to the primary star, 'host', in 'system'
	 */
	public static void addBodies(StarSystem system, int numBodies, StellarObject host){
		//double desiredTemperature = (rand.nextDouble() * MAX_TEMPERATURE * TEMPERATURE_JITTER * 2) + (MAX_TEMPERATURE * (1.0 - TEMPERATURE_JITTER));
		//compute the orbit of the next body
		long nextOrbit = (long) ((host.getMass() * host.getMass() * 0.05 * ((double)(Main.rand.nextInt(10) + 1))) * Util.AU);//(long) ((((double) Util.AU) * host.getBodeConstant()) * (Util.randomDouble(0.9, 1.1)));		

		//compute the minimum orbit
		long minimumOrbit = (long)(Math.sqrt(host.getLuminosity()) * 0.025);
		
		//adjust the minimum orbit if the star is a white dwarf
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
		
		//generate the planets
		for(int i = 0; i < numBodies; i++){
			StellarObject planet;
			
			//generate a planet and set its temperature
			planet = generatePlanet(host.getName() + " " + Util.intToNumerals(i + 1), nextOrbit, host, system);
			planet.setEffectiveTemperature(Util.calculateTemperature(planet));	
			
			//recompute the next orbit
			nextOrbit = (long) (((((double) nextOrbit) / Util.AU) * (1.1 + (((double)(Main.rand.nextInt(10) + 1)) / 10.0)) + (0.1)) * Util.AU);
					
			//System.out.println("New Planet Distance: " + minimumOrbit);
			//add the planet if it's orbital distance is greater than the minimum orbit, reject it otherwise
			if(nextOrbit >= minimumOrbit && planet != null && planet.getSubType() != PlanetSubType.ASTEROID_BELT && planet.getSubType() != PlanetSubType.EMPTY_ORBIT){
				host.getOrbitingBodies().add(planet);
				spobList.add(planet);
			}
		}
	}
	
	/*
	 * This function adds moons to the stellar object 'host'
	 */
	public static void addMoons(StellarObject host){
		int numMoons = 0;
		//moon roll determines how many moons a body can receive
		int moonRoll = Main.rand.nextInt(10);
		
		//add 5 to whatever the roll was if the planet is in the star's outerzone
		if(host.getOrbitalDistance() > Math.sqrt(host.getHost().getLuminosity()) * 4)
			moonRoll += 5;
		
		//assess the moon roll for chunk planets
		if(host.getSubType() == PlanetSubType.CHUNK){
			if(moonRoll >= 10)
				numMoons = 1;
		}
		//assess the moon roll for terrestrial planets
		else if(host.getSubType() == PlanetSubType.TERRESTRIAL || host.getSubType() == PlanetSubType.SUPER_TERRESTRIAL || host.getSubType() == PlanetSubType.DWARF){
			//one moon for rolls of 6 or 7
			if(moonRoll >= 6 && moonRoll <= 7)
				numMoons = 1;
			//1-2 moons for rolls of 8 or 9
			else if(moonRoll >= 8 && moonRoll <= 9)
				numMoons = rand.nextInt(2) + 1;
			//1-5 moons for rolls of 10-13
			else if(moonRoll >= 10 && moonRoll <= 13)
				numMoons = rand.nextInt(5) + 1;
			//1-10 moons for everything else
			else if(moonRoll >= 14)
				numMoons = rand.nextInt(10) + 1;
		}
		//assess the moon roll for jovian planets
		else if(host.getSubType() == PlanetSubType.JOVIAN || host.getSubType() == PlanetSubType.SUPER_JOVIAN){
			//1- 5 moons for rolls of 5 or lower
			if(moonRoll <= 5)
				numMoons = rand.nextInt(5) + 1;
			//1-10 moons for rolls of 6 or 7
			else if(moonRoll >= 6 && moonRoll <= 7)
				numMoons = rand.nextInt(10) + 1;
			//5-15 moons for rolls of 8 or 9
			else if(moonRoll >= 8 && moonRoll <= 9)
				numMoons = rand.nextInt(10) + 6;
			//10-20 moons for rolls of 10-13
			else if(moonRoll >= 10 && moonRoll <= 13)
				numMoons = rand.nextInt(10) + 11;
			//20-30 moons for everything else
			else if(moonRoll >= 14)
				numMoons = rand.nextInt(10) + 21;		
		}
		
		//after determining the number of moons we configure their orbits 
		long minimumOrbit = 0;
		for(int i = 0; i < numMoons; i++){
			//roll for orbital distance
			int distanceRoll = rand.nextInt(10) + 1;
			if(distanceRoll <= 4)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) * 0.5 + (host.getDiameter() / 2));
			else if(distanceRoll >= 5 && distanceRoll <= 6)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) + ((host.getDiameter() / 2) * 6));
			else if(distanceRoll >= 7 && distanceRoll <= 8)
				minimumOrbit = (long) (((double)(rand.nextInt(10) + 1)) * 3.0 + ((host.getDiameter() / 2) * 16));
			else if(distanceRoll == 9)
				minimumOrbit = (long) (((double)(rand.nextInt(100) + 1)) * 3.0 + ((host.getDiameter() / 2) * 45));
			
			//generate the moons and add them to the hosts pool of orbiting bodies
			StellarObject moon = generateMoon(host.getName() + " - Moon " + (i + 1), minimumOrbit, host);
			//minimumOrbit += moon.getOrbitalDistance();
			host.getOrbitingBodies().add(moon);
		}
	}
	
	/*
	 * This function generates a sun and assigns to a system
	 */
	public static StellarObject generateSun(StarSystem system, String name, long minimumOrbit, StellarObject host, StarSubType desiredType){
		int solarType = 0;
		double luminosity = 0.0;
		
		//generate the new sun
		StellarObject ret = SolarSystemGenerator.generateStar(name, host);
		
		//int coreBodies = rand.nextInt(StarSubType.maxPlanets[solarType] - StarSubType.minPlanets[solarType]) + StarSubType.minPlanets[solarType];
		
		//roll for the number of planets
		int bodiesRoll = rand.nextInt(10) + 1;
		int coreBodies = 0;
		
		//add 1 to the roll if the sun is orange and has a spectral class greater than 4
		if(ret.getSubType() == StarSubType.ORANGE || ret.getSubType() == StarSubType.ORANGE_SUB_GIANT ||ret.getSubType() == StarSubType.ORANGE_GIANT){
			if(ret.getSpectralClass() >= 5)
				bodiesRoll += 1;
		}
		
		//add 2 to the roll if the star is red and has a spectral class less than 5 or 3 if it's greater than 4
		if(ret.getSubType() == StarSubType.RED ||ret.getSubType() == StarSubType.RED_GIANT){
			if(ret.getSpectralClass() <= 4)
				bodiesRoll += 2;
			else if(ret.getSpectralClass() >= 5)
				bodiesRoll += 3;
		}
		
		//add 5 to the roll if the star is a brown dwarf
		if(ret.getSubType() == StarSubType.BROWN_DWARF)
			bodiesRoll += 5;
		
		//lastly, subtract the stars relative abundance of heavy metals
		bodiesRoll -= system.getRelativeAbundance();
		
		//10-20 bodies if the roll is 1
		if(bodiesRoll == 1)
			coreBodies = rand.nextInt(10) + 11;
		//5-10 bodies if the roll is 2-5
		else if(bodiesRoll >= 2 || bodiesRoll <= 5)
			coreBodies = rand.nextInt(10) + 6;
		//1-10 bodies if the roll is 6 or 7
		else if(bodiesRoll >= 6 || bodiesRoll <= 7)
			coreBodies = rand.nextInt(10) + 1;
		//1-5 bodies if the roll is 8 or 9
		else if(bodiesRoll >= 8 || bodiesRoll <= 9)
			coreBodies = (rand.nextInt(5) + 1);
		//0 bodies otherwise
		else
			coreBodies = 0;
		
		//set the orbit of the first planet
		minimumOrbit = (long) (Math.pow(ret.getMass(), 2) * 0.05 * (Main.rand.nextInt(10) + 1));
		
		//print the properties of the star
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
		addMoons(ret);
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
