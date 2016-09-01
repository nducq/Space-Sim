package gui;

import main.Game;
import main.Main;

public class TextLabel extends GuiObject{

	protected String label;
	
	public TextLabel(int x, int y, int width, int height, String label, GuiCreator parent, Main device) {
		super(x, y, width, height, parent, device);
		int numCharsPerLine = (int) (width / device.textWidth("A"));
		int lineCount = 0;
		
		this.label = new String(label);
		
		for(int i = 0; i < label.length(); i++){
			if(i % numCharsPerLine == 0){
				this.label = (this.label.substring(0, i + lineCount) + "\n" + this.label.substring(i + lineCount));
				lineCount++;
			}
		}
	}

	@Override
	public void step() {
		if(isHidden)
			return;
		super.step();
		
		if(parent instanceof GuiObject){
			xOffset = ((GuiObject)parent).getX();
			yOffset = ((GuiObject)parent).getY();
		}
	}

	@Override
	public void draw() {
		if(isHidden)
			return;
		
		device.fill(255, 255, 255);
		device.text(label, x + xOffset, y + yOffset);
	}

}
