package gui;

import main.Game;
import main.Main;

public class Button extends GuiObject{

	protected String label;
	
	public Button(int x, int y, int width, int height, String label, GuiCreator parent, Main device) {
		super(x, y, width, height, parent, device);
		this.label = label;
	}
	
	public void step(){
		if(isHidden)
			return;
		super.step();
		
		if(parent instanceof GuiObject){
			xOffset = ((GuiObject)parent).getX();
			yOffset = ((GuiObject)parent).getY();
		}
		
		//button clicked
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED)
			parent.switchFocus(this);
		
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height) && device.getMouseState()[Main.LEFT] == Game.KEY_JUST_RELEASED)
			parent.eventCallback(this);
	
	}
	
	public void draw(){		
		if(isHidden)
			return;
		
		device.fill(0, 0, 0);
		device.stroke(255, 255, 255);
		device.rect(x + xOffset, y + yOffset, width, height);
		
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height)){
			if(device.getMouseState()[Main.LEFT] != Game.KEY_HELD)
				device.fill(255, 255, 255, 128);
			else
				device.fill(255, 255, 255, 255);
			device.rect(x + xOffset, y + yOffset, width, height);			
		}
		
		device.fill(255, 255, 255);
		
		device.textAlign(Main.CENTER, Main.CENTER);
		device.text(label, x + xOffset + (width / 2), y + yOffset + (height / 2));
		device.textAlign(Main.LEFT, Main.TOP);
	}
}
