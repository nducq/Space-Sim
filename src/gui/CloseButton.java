package gui;

import main.Game;
import main.Main;

public class CloseButton extends Button{
	public CloseButton(int x, int y, int width, int height, GuiCreator parent, Main device) {
		super(x, y, width, height, "", parent, device);
	}

	void eventCallback(GuiObject event) {
		return;
	}

	public void step(){
		super.step();
	}
	
	public void draw(){
		super.draw();
		
		if(isHidden)
			return;
		
		device.stroke(255, 255, 255);
		device.line(x + xOffset + 2, y + 2 + yOffset, x + width - 2 + xOffset, y + height - 2 + yOffset);
		device.line(x + 2 + xOffset, y + height - 2 + yOffset, x + width - 2 + xOffset, y + 2 + yOffset);
	}
}
