package main;

import java.util.LinkedList;

import starmap.StarSystem;

public class SearchWindow {
	private final int MAX_TIMER = 20;
	
	private Game device;
	private int blinkTimer;
	private boolean drawBlinky;
	private int x;
	private int y;
	private Map map;
	
	public SearchWindow(Game device, Map map, int x, int y){
		Game.setKeyString("");
		this.device = device;
		this.map = map;
		this.x = x;
		this.y = y;
	}
	
	public void step(){
		Game.setKeyString(Game.getKeyString().replace("\n", ""));
		if(blinkTimer > 0)
			blinkTimer--;
		else{
			blinkTimer = MAX_TIMER;
			drawBlinky = !drawBlinky;
		}
		
		if(Game.getKeyString().length() > 45){
			Game.setKeyString(Game.getKeyString().substring(0, 45));
		}
		
		if(device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED){
			if(device.mouseX > x - 150 && device.mouseX < x - 50 && device.mouseY > y + 60 && device.mouseY < y + 80){
				LinkedList<StarSystem> searchCandidates = Main.searchSystemList(Game.getKeyString(), true);
				if(searchCandidates.size() > 0)
					map.jumpToSystem(searchCandidates.get(0));
				//map.closeSearchWindow();
			}
			if(device.mouseX > x + 50 && device.mouseX < x + 150 && device.mouseY > y + 60 && device.mouseY < y + 80){}
				//map.closeSearchWindow();
		}
	}
	
	public void draw(){
		device.rectMode(Main.CENTER);
		device.stroke(255, 255, 255);
		device.fill(0, 0, 0);
		
		device.rect(x, y, 400, 200);
		device.rect(x, y + 10, 360, 25);
		
		device.fill(255, 255, 255);
		device.text(Game.getKeyString(), x - 180, y + 14);
		
		device.fill(0, 0, 0);
		device.rect(x - 100, y + 70, 100, 20);
		device.rect(x + 100, y + 70, 100, 20);
		device.fill(255, 255, 255);

		device.textAlign(Main.CENTER, Main.CENTER);
		device.text("OK", x - 100, y + 70);		
		device.text("Cancel", x + 100, y + 70);
		device.textAlign(Main.LEFT, Main.TOP);
		
		if(device.mouseX > x - 150 && device.mouseX < x - 50 && device.mouseY > y + 60 && device.mouseY < y + 80){
			device.fill(255, 255, 255, 128);
			device.rect(x - 100, y + 70, 100, 20);
		}		
		
		if(device.mouseX > x + 50 && device.mouseX < x + 150 && device.mouseY > y + 60 && device.mouseY < y + 80){
			device.fill(255, 255, 255, 128);
			device.rect(x + 100, y + 70, 100, 20);
		}
				
		device.rectMode(Main.CORNER);
		device.fill(255, 255, 255, 255);
		
		device.text("Enter system name:", x - 180, y - 20);
		
		if(drawBlinky)
			device.line(x - 178 + device.textWidth(Game.getKeyString()), y, x - 178 + device.textWidth(Game.getKeyString()), y + 15);
	}
}
