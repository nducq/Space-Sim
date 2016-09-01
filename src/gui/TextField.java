package gui;

//import java.awt.event.KeyEvent;

import main.Game;
import main.Main;

public class TextField extends GuiObject{

	protected static final int MAX_TIMER = 40;
	
	protected static final int MAX_EVENT_TIMER = 10;
	
	protected int eventTimer;
	protected String text;
	protected boolean drawBlinky;
	protected int blinkTimer;
	
	public TextField(int x, int y, int width, int height, GuiCreator parent, Main device) {
		super(x, y, width, height, parent, device);
		
		text = "";
		drawBlinky = true;
		blinkTimer = MAX_TIMER;
		eventTimer = -1;
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
		
		if(blinkTimer > 0)
			blinkTimer--;
		else{
			blinkTimer = MAX_TIMER;
			drawBlinky = !drawBlinky;
		}
		
		if(eventTimer > 0 && inFocus)
			eventTimer--;
		else if (eventTimer == 0){
			eventTimer = -1;
			parent.eventCallback(this);
		}
		
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED){
			parent.switchFocus(this);
			parent.eventCallback(this);
			Main.setKeyString(text);
		}
		
		if(inFocus)
			text = Main.getKeyString().replaceAll("\n", "");
		
		if(device.charTyped())
			eventTimer = MAX_EVENT_TIMER;
	}

	@Override
	void draw() {
		device.fill(0, 0, 0);
		device.stroke(255, 255, 255);
		device.rect(x + xOffset, y + yOffset, width, height);
		
		device.fill(255, 255, 255);
		device.textAlign(Main.LEFT, Main.BOTTOM);
		
		if(inFocus){
			device.fill(255, 255, 255);
			device.rect(x + xOffset + 2, y + yOffset + 2, width - 4, height - 4);
			device.fill(0, 0, 0);
			device.stroke(0, 0, 0);
			if(drawBlinky)
				device.line(x + xOffset + 4 + device.textWidth(text), y + yOffset + 4, x + xOffset + 4 + device.textWidth(text), y + yOffset + height - 4);
		}
		
		device.text(text, x + 3 + xOffset, y + height - 3 + yOffset);
		
		device.textAlign(Main.LEFT, Main.TOP);
	}
	
	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}
}
