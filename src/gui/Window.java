package gui;

import java.util.LinkedList;

import main.Game;
import main.Main;

abstract class Window extends GuiObject implements GuiCreator{
	
	protected int mouseXOffset;
	protected int mouseYOffset;
	protected boolean dragMode;
	protected Button closeWindow;
	protected final static int HEADER_SIZE = 20;
	
	public Window(int x, int y, int width, int height, GuiCreator parent, Main main){
		super(x, y, width, height, parent, main);
		
		dragMode = false;
		mouseXOffset = 0;
		mouseYOffset = 0;
		
		initializeGui();
	}

	public void initializeGui() {
		closeWindow = new CloseButton(width - (HEADER_SIZE) + 2, 2 - HEADER_SIZE, (HEADER_SIZE - 4), (HEADER_SIZE - 4), this, device);
		
		components.add(closeWindow);
	}

	public void disposeGui() {
		components.clear();
	}

	public void hideGui(GuiObject obj) {
		obj.hide();
	}

	public void showGui(GuiObject obj) {
		obj.show();
	}
	
	public void step() {
		if(isHidden)
			return;	
		
		if(mouseInRectangle(x + xOffset, y + yOffset - HEADER_SIZE, width, HEADER_SIZE) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED)
			switchFocus(this);
		
		if(device.getMouseState()[Main.LEFT] == Game.KEY_RELEASED){
			dragMode = false;
			if(x < 0)
				x = 0;
			else if (x + width > Main.WIN_WIDTH)
				x = Main.WIN_WIDTH - width;

			if(y - HEADER_SIZE < 0)
				y = HEADER_SIZE;
			else if (y + height > Main.WIN_HEIGHT)
				y = Main.WIN_HEIGHT - height;
		}
		if(!dragMode){
			if(mouseInRectangle(x, y - HEADER_SIZE, width, HEADER_SIZE) && device.getMouseState()[Main.LEFT] == Game.KEY_HELD && inFocus){
				mouseXOffset = device.mouseX - x;
				mouseYOffset = device.mouseY - y;
				dragMode = true;
			}
		}
		else{
			x = device.mouseX - mouseXOffset;
			y = device.mouseY - mouseYOffset;
		}
		
		for(GuiObject gui: components)
			gui.step();
	}

	public void draw() {
		if(isHidden)
			return;

		if(!inFocus)
			device.fill(0, 0, 0);
		else
			device.fill(0, 0, 0);
		
		device.stroke(255, 255, 255);

		device.rect(x, y - HEADER_SIZE, width, HEADER_SIZE);
		device.rect(x, y, width, height);
		
		for(GuiObject gui: components)
			gui.draw();
	}

	public void eventCallback(GuiObject event) {
		inFocus = false;
		switchFocus(event);
		if(event == closeWindow)
			parent.hideGui(this);
	}
	
	@Override
	public void switchFocus(GuiObject target) {
		if(target == this)
			inFocus = true;
		else
			inFocus = false;
		for(GuiObject gui: components){
			if(gui != target)
				gui.setFocus(false);
			else
				gui.setFocus(true);
		}

		//System.out.println(target.getClass() + " is now in focus");
	}
	
}
