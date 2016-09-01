package gui;

import java.util.LinkedList;

import main.Game;
import main.Main;

public abstract class GuiObject {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int xOffset;
	protected int yOffset;
	protected boolean isHidden;
	protected GuiCreator parent;
	protected Main device;
	
	protected boolean inFocus;

	protected LinkedList<GuiObject> components;
	
	public GuiObject(int x, int y, int width, int height, GuiCreator parent, Main main){
		this.x = x;
		this.y = y;
		this.xOffset = 0;
		this.yOffset = 0;
		this.width = width;
		this.height = height;
		this.parent = parent;
		this.device = main;
		this.inFocus = false;
		
		if(parent instanceof GuiObject){
			xOffset = ((GuiObject)parent).getX();
			yOffset = ((GuiObject)parent).getY();
		}
		
		components = new LinkedList<GuiObject>();
	}
	
	public int getX(){
		return x + xOffset;
	}

	public int getY(){
		return y + yOffset;
	}
	
	public boolean isHidden(){
		return isHidden;
	}
	
	public void show(){
		isHidden = false;
		for(GuiObject gui: components)
			gui.show();
	}
	
	public void hide(){
		isHidden = true;
		for(GuiObject gui: components)
			gui.hide();
	}
	
	public void setFocus(boolean focus){
		inFocus = focus;
	}
	
	public boolean mouseInRectangle(int x, int y, int width, int height){
		return (device.mouseX > x && device.mouseY > y && device.mouseX < (x + width) && device.mouseY < (y + height));
	}
	
	public void step(){
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED){
			if(this instanceof GuiCreator)
				((GuiCreator)this).switchFocus(this);
			else
				parent.switchFocus(this);
		}
	}
	
	abstract void draw();
}
