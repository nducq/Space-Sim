package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

import Terrestrial.bodies.PlanetSubType;
import Terrestrial.bodies.StarSubType;
import Terrestrial.bodies.StellarObject;
import Terrestrial.bodies.Type;
import Terrestrial.resources.Mineral;
import gui.GuiCreator;
import gui.GuiObject;
import gui.SearchWindow;
import processing.core.PConstants;
import starmap.StarMapGenerator;
import starmap.StarSystem;

public class Map implements GuiCreator{
	private Main device;
	private long mapX;
	private long mapY;
	private int scale;
	private int drawMap;
	private boolean drawGrid;
	private boolean drawHidden;
	private double zoomMultiplier;
	private double solarZoomMultiplier;
	private StarSystem targetSystem;
	private StellarObject targetPlanet;
	
	private SearchWindow srchWindow;
	
	public Map(int mapX, int mapY, Main main){
		this.mapX = mapX;
		this.mapY = mapY;
		this.device = main;
		
		zoomMultiplier = 1;
		solarZoomMultiplier = 0.00001;
		
		drawMap = 0;
		drawGrid = false;
		targetSystem = Main.getTargetSystem();
		targetPlanet = Main.getCurrentSystem().getSpobs().get(Main.getPlayerShip().getTargetIndex());
		drawHidden = false;
		
		srchWindow = new SearchWindow(450, 450, this, main);
		srchWindow.hide();
	}
	
	public void step(){
    	int adjustedMapX = (int)((mapX - (Main.WIN_WIDTH / 2)) * zoomMultiplier) + (Main.WIN_WIDTH / 2);
    	int adjustedMapY = (int)((mapY - (Main.WIN_HEIGHT / 2)) * zoomMultiplier) + (Main.WIN_HEIGHT / 2);

    	int adjustedSolarMapX = (int)((mapX - (Main.WIN_WIDTH / 2)) * solarZoomMultiplier) + (Main.WIN_WIDTH / 2);
    	int adjustedSolarMapY = (int)((mapY - (Main.WIN_HEIGHT / 2)) * solarZoomMultiplier) + (Main.WIN_HEIGHT / 2);
    	
    	srchWindow.step();
    	
    	if(drawMap == 1){
			if(device.getKeyState()[KeyEvent.VK_UP] == Game.KEY_HELD)
				mapY += (10 / zoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_DOWN] == Game.KEY_HELD)
				mapY -= (10 / zoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_LEFT] == Game.KEY_HELD)
				mapX += (10 / zoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_RIGHT] == Game.KEY_HELD)
				mapX -= (10 / zoomMultiplier);
    	}
    	if(drawMap == 0){
			if(device.getKeyState()[KeyEvent.VK_UP] == Game.KEY_HELD)
				mapY += (10 / solarZoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_DOWN] == Game.KEY_HELD)
				mapY -= (10 / solarZoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_LEFT] == Game.KEY_HELD)
				mapX += (10 / solarZoomMultiplier);
			if(device.getKeyState()[KeyEvent.VK_RIGHT] == Game.KEY_HELD)
				mapX -= (10 / solarZoomMultiplier);
    	}
		
		if(device.getKeyState()[KeyEvent.VK_W] == Game.KEY_HELD && srchWindow.isHidden() && drawMap == 0){
			if(solarZoomMultiplier < 1)
			solarZoomMultiplier *= 1.05;
		}
		if(device.getKeyState()[KeyEvent.VK_S] == Game.KEY_HELD && srchWindow.isHidden() && drawMap == 0){
			if(solarZoomMultiplier > 0.00000000000000001)
				solarZoomMultiplier /= 1.05;
		}
		if(device.getKeyState()[KeyEvent.VK_W] == Game.KEY_PRESSED && srchWindow.isHidden() && drawMap == 1){
			if(zoomMultiplier < 2.00)
				zoomMultiplier += 0.25;
		}
		if(device.getKeyState()[KeyEvent.VK_S] == Game.KEY_PRESSED && srchWindow.isHidden() && drawMap == 1){
			if(zoomMultiplier > 0.25)
				zoomMultiplier -= 0.25;
		}
		
		if(device.getKeyState()[KeyEvent.VK_TAB] == Game.KEY_PRESSED && srchWindow.isHidden()){
			drawMap++;
			if(drawMap > 2)
				drawMap = 0;
			if(drawMap == 1){
				mapX = (int) (-Main.getCurrentSystem().getX() + (Main.WIN_WIDTH / 2));
				mapY = (int) (-Main.getCurrentSystem().getY() + (Main.WIN_HEIGHT / 2));
			}
			if(drawMap == 0){
				mapX = (Main.WIN_WIDTH / 2);
				mapY = (Main.WIN_HEIGHT / 2);
			}
		}
		
		if(device.getKeyState()[KeyEvent.VK_CONTROL] == Game.KEY_PRESSED && drawMap == 1){
			srchWindow.show();
		}
		
		if(device.getKeyState()[KeyEvent.VK_SHIFT] == Game.KEY_PRESSED && srchWindow.isHidden())
			drawHidden = !drawHidden;
		if(device.getKeyState()[KeyEvent.VK_SPACE] == Game.KEY_PRESSED && srchWindow.isHidden())
			drawGrid = !drawGrid;
		if(device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED && srchWindow.isHidden()){
			if(drawMap == 0){
				for(StellarObject spob: Main.getCurrentSystem().getSpobs()){
					if(Util.pointDistance(device.mouseX - adjustedSolarMapX, device.mouseY - adjustedSolarMapY, spob.getX() * solarZoomMultiplier, spob.getY() * solarZoomMultiplier) < 8)
						targetPlanet = spob;
				}
			}
			if(drawMap == 1){
				for(StarSystem s: Main.getSystemList()){
					if(!s.linkExplored() && !s.isExplored() && !drawHidden)
						continue;
					if(Util.pointDistance(device.mouseX - adjustedMapX, device.mouseY - adjustedMapY, s.getX() * zoomMultiplier, s.getY() * zoomMultiplier) < 8)
						targetSystem = s;
				}
			}
		}
	}
	
	public void draw(){
		device.fill(0, 0, 48);
		device.rect(0, 0, Main.WIN_WIDTH, Main.WIN_HEIGHT);
		
    	int adjustedMapX = (int)((mapX - (Main.WIN_WIDTH / 2)) * zoomMultiplier) + (Main.WIN_WIDTH / 2);
    	int adjustedMapY = (int)((mapY - (Main.WIN_HEIGHT / 2)) * zoomMultiplier) + (Main.WIN_HEIGHT / 2);

    	int adjustedSolarMapX = (int)((mapX - (Main.WIN_WIDTH / 2)) * solarZoomMultiplier) + (Main.WIN_WIDTH / 2);
    	int adjustedSolarMapY = (int)((mapY - (Main.WIN_HEIGHT / 2)) * solarZoomMultiplier) + (Main.WIN_HEIGHT / 2);
    	
    	device.fill(255, 255, 255);
    	device.text("Mouse X: " + (device.mouseX - mapX) + "\nMouse Y: " + (device.mouseY - mapY), 10, 24);
    	device.text("Map X: " + mapX +"\nMap Y: " + mapY, 10, 52);

    	//device.fill(device.color(255, 128, 0));
        //device.ellipse(mapX, mapY, 12, 12);
        if(drawMap == 0){
        	if(Main.getCurrentSystem() != null && Main.getCurrentSystem().getSun() != null){
        		for(StellarObject spob: Main.getCurrentSystem().getSpobs())
        			drawBodies(spob);	
        	}
	        			
	        device.pushMatrix();
	    		
	       	device.translate((float)(Main.getPlayerShip().getX() * solarZoomMultiplier + adjustedSolarMapX), (float)(Main.getPlayerShip().getY() * solarZoomMultiplier + adjustedSolarMapY));
	       	device.rotate((float) Math.toRadians(Main.getPlayerShip().getTheta()));
	    	
	       	device.fill(device.color(0, 255, 0));
	    		
	       	device.triangle(-4, 2, -4, -2, 4, 0);
	    		
	       	device.popMatrix();

	       	//Draw each star's habitable zone
	       	
	       	/*for(StellarObject spob: Main.getCurrentSystem().getSpobs()){
	       		if(spob.getType() == Type.STAR){
	       			long minDistance = (long) (Util.MIN_SOLAR_HABITABLE_ZONE * Math.sqrt(spob.getLuminosity()) * Util.AU);
	       			long maxDistance = (long) (Util.MAX_SOLAR_HABITABLE_ZONE * Math.sqrt(spob.getLuminosity()) * Util.AU);
	       			
	       	    	device.fill(device.color(128, 128, 128), 0);
	       	    	
	       	    	device.stroke(device.color(0, 255, 0));
	       	    	device.ellipse(mapX + (int)spob.getX(), mapY + (int)spob.getY(), (int)(minDistance / (scale / 2)), (int)(minDistance / (scale / 2)));
	       	    	device.ellipse(mapX + (int)spob.getX(), mapY + (int)spob.getY(), (int)(maxDistance / (scale / 2)), (int)(maxDistance / (scale / 2)));
	       		}
	       	}*/
	       	
    		//Draw planet HUD
	       	for(StellarObject spob: Main.getCurrentSystem().getSpobs()){
	        	int planetX = (int) ((spob.getX() * solarZoomMultiplier) + adjustedSolarMapX);
	        	int planetY = (int) ((spob.getY() * solarZoomMultiplier) + adjustedSolarMapY);

	       		if(spob == targetPlanet){
	    	    	device.fill(0, 0, 0, 0);
	    	    	device.stroke(0, 255, 0);
	    	    	device.rect(planetX - 14, planetY - 14, 28, 28);
	    	    	
	    	    	device.stroke(255, 255, 255);
	    	    	device.fill(0, 0, 0);
	    	    	device.rect(planetX - 100, planetY + 40, 200, 300);
	    	    	
	    	    	device.fill(255, 255, 255);
	    	    	device.text(spob.getSubType().toString() + " " + spob.getType().toString(), planetX - 95, planetY + 55);
	    	    	
	    	    	double totalAtm = 0.0;
	    	    	double totalSurface = 0.0;

    				NumberFormat formatter = new DecimalFormat("#0.00");
	    	    	
	    	    	for(Mineral m: spob.getAtmosphericMinerals().keySet())
	    	    		totalAtm += spob.getAtmosphericMinerals().get(m);
	    	    	
	    	    	for(Mineral m: spob.getSurfaceMinerals().keySet())
	    	    		totalSurface += spob.getSurfaceMinerals().get(m);
	    	    	
	    	    	LinkedList<String> selectedText = new LinkedList<String>();
	    	    	
	    	    	selectedText.add("Orbital Distance: " + (((double)spob.getOrbitalDistance()) / ((double)Util.AU)) + " AU");
	    	    	selectedText.add("Effective Temperature: " + (Math.round(spob.getEffectiveTemperature() * 100) / 100) + " C");
	    	    	selectedText.add("Surface Temperature: " + (Math.round(spob.getSurfaceTemperature() * 100) / 100) + " C");
	    	    	if(spob.getType() == Type.STAR)
	    	    		selectedText.add("Luminosity: " + spob.getLuminosity());
	    	    	else
	    	    		selectedText.add("Colony Cost: " + formatter.format(spob.getColonyCost()));
	    	    	selectedText.add("Mass: " + formatter.format(spob.getMass()));
	    	    	selectedText.add("Diameter: " + formatter.format(spob.getDiameter()));
	    	    	selectedText.add("Surface (" + formatter.format(Util.computeSurfaceVolume(spob) / Util.EARTH_SURFACE_VOLUME) + "ESV):");
	    	    	selectedText.add("Albedo: " + formatter.format(spob.getAlbedo()));    	    	
	    	    	for(Mineral m: spob.getSurfaceMinerals().keySet()){
	    	    		if((spob.getSurfaceMinerals().get(m) / totalSurface * 100) > 0.1)
	    	    			selectedText.add(m.getNames()[m.getStateIndexAtTemp(spob.getSurfaceTemperature())] + " - " + formatter.format(spob.getSurfaceMinerals().get(m) / totalSurface * 100) + "%");
	    	    	}
	    	    		
	    	    	selectedText.add("Atmosphere:");
	    	    	selectedText.add("Pressure: " + formatter.format(spob.getAtmosphericPressure()));
	    	    	selectedText.add("Greenhouse Factor: " + formatter.format(spob.computeGreenhouseFactor()));
	    	    	
	    	    	for(Mineral m: spob.getAtmosphericMinerals().keySet()){
	    	    		if((spob.getAtmosphericMinerals().get(m) / totalAtm * 100) > 0.1)
	    	    			selectedText.add(m.getNames()[2] + " - " + formatter.format(spob.getAtmosphericMinerals().get(m) / totalAtm * 100) + "%");
	    	    	}
	    	    	
	    	    	selectedText.add("Minerals: ");

	    	    	for(Mineral m: spob.getMinerals().keySet())
		    	    	selectedText.add(m.getNames()[0] + " - " + (spob.getMinerals().get(m)));
	    	    	
	    	    	int i = 0;
	    	    	for(String s: selectedText){
	    	    		device.text(s, planetX - 95, planetY + 69 + (i * (device.textAscent() + device.textDescent())));
	    	    		i++;
	    	    	}
	    	    }
	       	}
        }
        else if (drawMap == 1){	
        	//for(CircumCircle c: Main.circumCircles)
        	//	c.draw(device, this);
        	
        	for(StarSystem s: Main.getSystemList()){
        		if(s.getGovernment() != null){
        			//device.stroke(0,0,0,0);
	        		//device.fill(s.getGovernment().getColor());
	        		//device.ellipse((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, (int)(100 * zoomMultiplier), (int)(100 * zoomMultiplier));
        		}
        	}
        	
        	for(StarSystem s: Main.getSystemList()){
        		if((zoomMultiplier * s.getX()) + adjustedMapX < 0 || (zoomMultiplier * s.getX()) + adjustedMapX > Main.WIN_WIDTH || (zoomMultiplier * s.getY()) + adjustedMapY < 0 || (zoomMultiplier * s.getY()) + adjustedMapY > Main.WIN_HEIGHT)
        			continue;
        		for(StarSystem l: s.getLinks()){
        			if(!s.isExplored() && !l.isExplored() && !drawHidden)
        				continue;
        				
        			if((s == targetSystem && l == Main.getCurrentSystem()) || (l == targetSystem && s == Main.getCurrentSystem()))
            			device.stroke(device.color(0, 255, 0));
        			else
        				device.stroke(device.color(128, 128, 128));
        			device.line((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, (int)(zoomMultiplier * l.getX()) + adjustedMapX, (int)(zoomMultiplier * l.getY()) + adjustedMapY);
        		}
        	}
        	
        	for(StarSystem s: Main.getSystemList()){
        		if((zoomMultiplier * s.getX()) + adjustedMapX < 0 || (zoomMultiplier * s.getX()) + adjustedMapX > Main.WIN_WIDTH || (zoomMultiplier * s.getY()) + adjustedMapY < 0 || (zoomMultiplier * s.getY()) + adjustedMapY > Main.WIN_HEIGHT)
        			continue;
        		if(!s.isExplored() && !drawHidden && !s.linkExplored())
        			continue;
        		
        		if(s.isExplored() || drawHidden){
		        	device.fill(device.color(0, 0, 0));
	        		device.stroke(s.getColor());
	        		device.ellipse((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, 10, 10);
        		}
        		else if(s.linkExplored()){
		        	device.fill(device.color(0, 0, 0));
	        		device.stroke(device.color(64, 64, 64));
	        		device.ellipse((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, 10, 10);        			
        		}
        		
        		if(s == Main.getCurrentSystem()){
        			device.stroke(0, 0, 0, 0);
            		device.fill(device.color(0, 196, 255));
            		device.ellipse((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, 6, 6);
        		}
        		if(s == targetSystem){
        			device.stroke(0, 255, 0);
        			device.fill(0, 0, 0, 0);
            		device.rect((int)(zoomMultiplier * s.getX()) + adjustedMapX - 7, (int)(zoomMultiplier * s.getY()) + adjustedMapY - 7, 14, 14);
        		}
    			device.stroke(0, 0, 0, 0);
        		device.fill(device.color(255, 255, 255));
	        	if(zoomMultiplier > 0.25 && (s.isExplored() || drawHidden)){
	        		device.textAlign(PConstants.CENTER);
	        		device.text(s.getName(), (int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY - 10);
	        		device.textAlign(PConstants.LEFT);
	        	}
	        }
        	        	
        	//draw selected system info:
        	device.stroke(255, 255, 255);
        	device.fill(0, 0, 0);
        	
        	device.rect(Main.WIN_WIDTH - 310, 10, 300, Main.WIN_HEIGHT - 200);
        	if(targetSystem != null && (targetSystem.isExplored() || drawHidden)){
        		device.fill(255, 255, 255);
        		device.text("Selected System:\n " + targetSystem.getName(), Main.WIN_WIDTH - 305, 25);
        		device.text("Stars: ", Main.WIN_WIDTH - 305, 67);
        		LinkedList<StellarObject> stars = targetSystem.getStars();
        		LinkedList<StellarObject> planets = targetSystem.getPlanets();
        		
        		NumberFormat formatter = new DecimalFormat("#0.00");
        		
        		for(int i = 0; i < stars.size(); i++)
        			device.text(" " + stars.get(i).getName() + " - " + stars.get(i).getSubType(), Main.WIN_WIDTH - 305, 81 + (i * 14));
        		device.text("Planets:", Main.WIN_WIDTH - 305, 95 + (stars.size() * 14));
        		for(int i = stars.size(); i < (planets.size() + stars.size()); i++){
        			if(planets.get(i - stars.size()).getType() == Type.PLANET)
        				device.text(" " + planets.get(i - stars.size()).getName() + " - " + planets.get(i - stars.size()).getSubType() + "(CC: " + formatter.format(planets.get(i - stars.size()).getColonyCost()) + ")", Main.WIN_WIDTH - 305, 109 + (i * 14));
        			else
        				device.text(" - " + planets.get(i - stars.size()).getName() + " - ", Main.WIN_WIDTH - 285, 109 + (i * 14));
        		}
        	}
        	else if(targetSystem != null && !targetSystem.isExplored()){
        		device.fill(255, 255, 255);
        		device.text("Selected System:\n Unidentified System", Main.WIN_WIDTH - 305, 25);
        		device.text("Stars:\n Unknown", Main.WIN_WIDTH - 305, 67);
        		device.text("Planets:\n Unknown", Main.WIN_WIDTH - 305, 109);
        	}
	        device.fill(255, 255, 255);
        	device.text("Number of Systems: " + Main.getSystemList().size(), 10, 10);
        	
        	if(srchWindow != null)
        		srchWindow.draw();
        	
        }
        else if (drawMap == 2){
			double adjustedMouseX = Math.max(0, Math.min(device.mouseX - (Main.WIN_WIDTH / 2) + (StarMapGenerator.GRID_WIDTH / 2 * 8), 8 * StarMapGenerator.GRID_WIDTH));
			double adjustedMouseY = Math.max(0, Math.min(device.mouseY - (Main.WIN_HEIGHT / 2) + (StarMapGenerator.GRID_HEIGHT / 2 * 8), 8 * StarMapGenerator.GRID_WIDTH));

        	for(int i = 0; i < StarMapGenerator.GRID_WIDTH; i++){
        		for(int j = 0; j < StarMapGenerator.GRID_HEIGHT; j++){
        			if(StarMapGenerator.getGrid(i, j)){
        				device.stroke(0, 128, 0);
        				device.fill(0, 255, 0);
        				device.rect((i * 8) - (4 * StarMapGenerator.GRID_WIDTH) + (Main.WIN_WIDTH / 2), (j * 8) - (4 * StarMapGenerator.GRID_HEIGHT) + (Main.WIN_HEIGHT / 2), 8, 8);
        			}
        			else{
        				device.stroke(0, 255, 0);
        				device.fill(0, 32, 0);
        				device.rect((i * 8) - (4 * StarMapGenerator.GRID_WIDTH) + (Main.WIN_WIDTH / 2), (j * 8) - (4 * StarMapGenerator.GRID_HEIGHT) + (Main.WIN_HEIGHT / 2), 8, 8);        				
        			}
        		}
        	}
        	device.stroke(255, 255, 255);
        	device.line((Main.WIN_WIDTH / 2), (Main.WIN_HEIGHT / 2) - 8, (Main.WIN_WIDTH / 2), (Main.WIN_HEIGHT / 2) + 8);
        	device.line((Main.WIN_WIDTH / 2) - 8, (Main.WIN_HEIGHT / 2), (Main.WIN_WIDTH / 2) + 8, (Main.WIN_HEIGHT / 2));
        	
        	device.fill(255, 255, 255);
        	device.text("Cell (" + Math.floor(adjustedMouseX / 8) + ", " + Math.floor(adjustedMouseY / 8) + ")", 10, 10);
        }
	}
	
	public void drawBodies(StellarObject spob){
    	int adjustedSolarMapX = (int)((mapX - (Main.WIN_WIDTH / 2)) * solarZoomMultiplier) + (Main.WIN_WIDTH / 2);
    	int adjustedSolarMapY = (int)((mapY - (Main.WIN_HEIGHT / 2)) * solarZoomMultiplier) + (Main.WIN_HEIGHT / 2);
    	
    	int planetX = (int) (solarZoomMultiplier * spob.getX()) + adjustedSolarMapX;//(spob.getX() / ((scale) / 100));//(int)(Math.cos(curr.getOrbitalTheta()) * curr.getOrbitalDistance() / 10000);
    	int planetY = (int) (solarZoomMultiplier * spob.getY()) + adjustedSolarMapY;//(spob.getY() / ((scale) / 100));//(int)(Math.sin(curr.getOrbitalTheta()) * curr.getOrbitalDistance() / 10000);		
    	
    	int hostX = 0;
    	int hostY = 0;
    	
    	if(spob.getHost() != null){
    		hostX = (int) (solarZoomMultiplier * spob.getHost().getX()) + adjustedSolarMapX;
    		hostY = (int) (solarZoomMultiplier * spob.getHost().getY()) + adjustedSolarMapY;
    	}
    	
    	//device.ellipse((int)(zoomMultiplier * s.getX()) + adjustedMapX, (int)(zoomMultiplier * s.getY()) + adjustedMapY, 10, 10);
    	
    	device.fill(device.color(128, 128, 128), 0);
    	device.stroke(device.color(0, 255, 0));
    	
    	
    	if(spob.getType() != Type.ASTEROID && spob.getType() != Type.MOON){
    		long eccentricityOffset = ((spob.getApoapsis() + spob.getPeriapsis()) / 2) - spob.getPeriapsis();
    		
    		device.ellipse(hostX + (int)(eccentricityOffset * solarZoomMultiplier / 100), hostY, (int)((spob.getApoapsis() + spob.getPeriapsis()) * solarZoomMultiplier / 100), (int)(spob.getOrbitalDistance() * solarZoomMultiplier / 50));
    		//device.ellipse(hostX, hostY, (int)(spob.getOrbitalDistance() * solarZoomMultiplier / 50), (int)(spob.getOrbitalDistance() * solarZoomMultiplier / 50));
    	}
    		
    	device.stroke(device.color(128, 128, 128), 0);
    	
    	device.textAlign(PConstants.CENTER);
    	device.fill(device.color(0, 255, 0), 255);
    	device.text(spob.getName(), planetX, (int)(planetY - 10));
    	device.textAlign(PConstants.LEFT);
    	
    	switch(spob.getType()){
    		case ASTEROID:
	    		device.image(spob.getGraphicIndex(), planetX - 2, planetY - 2, 4, 4);
	    		break;
			case PLANET:
	    		switch((PlanetSubType)spob.getSubType()){
				case DWARF:
					device.image(spob.getGraphicIndex(), planetX - 3, planetY - 3, 6, 6);
					break;
				case CHUNK:
					device.image(spob.getGraphicIndex(), planetX - 3, planetY - 3, 6, 6);
		    		break;
				case JOVIAN:
		    		//device.fill(device.color(0, 128, 196));
		    		device.image(spob.getGraphicIndex(), planetX - 6, planetY - 6, 12, 12);
		    		break;
				case SUPER_JOVIAN:
		    		device.image(spob.getGraphicIndex(), planetX - 12, planetY - 12, 24, 24);
		    		break;
				case SUPER_TERRESTRIAL:
					device.image(spob.getGraphicIndex(), planetX - 6, planetY - 6, 12, 12);
		    		break;
    			case TERRESTRIAL:
					device.image(spob.getGraphicIndex(), planetX - 4, planetY - 4, 8, 8);
		    		break;
				default:
					break;
	    		}
	    		break;
			case STAR:
				switch((StarSubType)spob.getSubType()){
					case RED_GIANT:
					case RED:
						device.image(spob.getGraphicIndex(), planetX - 16, planetY - 16, 32, 32);
				        break;
					case ORANGE_SUB_GIANT:
					case ORANGE_GIANT:
					case ORANGE:
						device.image(spob.getGraphicIndex(), planetX - 16, planetY - 16, 32, 32);
				        break;
					case YELLOW_SUB_GIANT:
					case YELLOW_GIANT:
					case YELLOW:
						device.image(spob.getGraphicIndex(), planetX - 16, planetY - 16, 32, 32);
			    		break;
					case WHITE_SUB_GIANT:
					case WHITE_GIANT:
					case WHITE:
						device.image(spob.getGraphicIndex(), planetX - 16, planetY - 16, 32, 32);
				        break;
					case BLUE_WHITE_SUB_GIANT:
					case BLUE_WHITE_GIANT:
					case BLUE_WHITE:
						device.image(spob.getGraphicIndex(), planetX - 16, planetY - 16, 32, 32);
				        break;
				}
				break;
			case MOON:
				device.image(spob.getGraphicIndex(), planetX - 3, planetY - 3, 6, 6);
				break;
		    default:
		    	device.fill(device.color(0, 0, 255));
				device.ellipse(planetX, planetY, 6, 6);
				break;
    	}
    }
	
	public void jumpToSystem(StarSystem s){
		targetSystem = s;
		
		mapX = (int) (-s.getX() + (Main.WIN_WIDTH / 2));
		mapY = (int) (-s.getY() + (Main.WIN_HEIGHT / 2));
	}
	
	public long getMapX(){
		return mapX;
	}
	
	public long getMapY(){
		return mapY;
	}
	
	public StarSystem getTargetSystem(){
		return targetSystem;
	}
	
	public StellarObject getTargetPlanet(){
		return targetPlanet;
	}

	@Override
	public void initializeGui() {
		
	}

	@Override
	public void disposeGui() {
		
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
		if(event == srchWindow)
			jumpToSystem(srchWindow.getSelectedSystem());
	}

	@Override
	public void switchFocus(GuiObject target) {
		// TODO Auto-generated method stub
		
	}
}
