package main;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import Terrestrial.bodies.StellarObject;
import gui.GuiCreator;
import gui.GuiObject;
import gui.SeedWindow;
import init.InitializeFactions;
import init.InitializeMaterials;
import init.InitializeMinerals;
import init.InitializePlanetTypes;
import processing.core.PApplet;
import processing.core.PImage;
import starmap.StarSystem;

public class Game extends PApplet implements GuiCreator{
	private int winWidth;
	private int winHeight;
	private boolean fullscreen;
	
	public long viewXView;
	public long viewYView;
	
	public double warpAlpha;
	private Map starMap;
	
	private int keyState[] = new int[256];
	private int mouseState[] = new int[256];
	private boolean gamePaused = false;
	private static String keyString = "";
	private char lastChar = '\0';
	private boolean charTyped = false;

	public static final int KEY_PRESSED = 1;
	public static final int KEY_HELD = 2;
	public static final int KEY_RELEASED = 0;
	public static final int KEY_JUST_RELEASED = 3;
	
	public Game(int winWidth,int winHeight, boolean fullscreen){
		this.winWidth = winWidth;
		this.winWidth = winWidth;
		this.fullscreen = fullscreen;
		
		viewXView = winWidth / 2;
		viewYView = winHeight / 2;
		
		starMap = null;
	}
	
	public void setup(){
		warpAlpha = 0;
		
		textSize(12);
		//smooth(0);
		frameRate(60);
	}
	
	public void step(){
		//seedWindow.step();
		
		if(keyState[KeyEvent.VK_ENTER] == KEY_PRESSED){
			gamePaused = !gamePaused;
			
			if(gamePaused){}
				//starMap = new Map((winWidth / 2) - ((int)Main.playerShip.getX() / 2000), (winHeight / 2) - ((int)Main.playerShip.getY() / 2000), this);
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
					//s.step(this);
				}
				for(Ship s: Main.getShipList()){
		        	//s.step(this);
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
		
		//seedWindow.draw();
			
        step();
        
        if(!gamePaused){
    		for(int bkgIndex = 0; bkgIndex < Main.backgroundList.length; bkgIndex++){
    			if(Main.backgroundList[bkgIndex] != null){
    				int numColTiles = (int)Math.ceil((double)winWidth / (double)Main.backgroundList[bkgIndex].width) + 2;
    				int numRowTiles = (int)Math.ceil((double)winHeight / (double)Main.backgroundList[bkgIndex].height) + 2;
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
    			rect(0, 0, winWidth, winHeight);
    		}
    		
    		if(Main.getSpobList() != null){
		        for(StellarObject s: Main.getSpobList()){
		        	//s.draw(this);
		        }
    		
		        for(Ship s: Main.getShipList()){
		        	//s.draw(this);
		        }
    		}
        }
        else
        	starMap.draw();
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
		Game.keyString = keyString;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showGui(GuiObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventCallback(GuiObject button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchFocus(GuiObject target) {
		// TODO Auto-generated method stub
		
	}
}
