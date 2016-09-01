package gui;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import main.Game;
import main.Main;
import processing.core.PGraphics;

public class ScrollList extends GuiObject implements GuiCreator{
	
	protected int scrollButtonWidth = 16;
	protected int scrollButtonHeight = 16;
	
	protected int scrollWidth = 16;
	protected LinkedList<String> nameList;
	int scrollOffset;
	int selectedIndex;
	int maxHeight;
	
	protected int mouseXOffset;
	protected int mouseYOffset;
	protected boolean dragMode;
	
	float textHeight;
	int scrollBarHeight;
	int scrollBarY;
	
	protected PGraphics surface;
	
	protected LinkedList<GuiObject> components;
	
	ScrollButton scrollUp;
	ScrollButton scrollDown;
	
	public ScrollList(int x, int y, int width, int height, GuiCreator parent, Main device){
		super(x, y, width, height, parent, device);
		selectedIndex = 0;
		
		nameList = new LinkedList<String>();
		surface = device.createGraphics(width, height);
				
		surface.beginDraw();
		surface.textSize(14);
		textHeight = surface.textAscent() + surface.textDescent();
		surface.endDraw();
		
		components = new LinkedList<GuiObject>();
		
		scrollBarHeight = 0;
		scrollBarY = 0;
		
		initializeGui();
	}

	public void addItem(String item){
		nameList.add(item);
		maxHeight = nameList.size() * (int)(textHeight);
		scrollBarHeight = Math.min((int)((((double)height) / ((double)maxHeight)) * (height - (2 * scrollButtonHeight))), height - (2 * scrollButtonHeight));
	}
	
	public void removeItem(String item){
		nameList.remove(item);
		maxHeight = nameList.size() * (int)(textHeight);
		scrollBarHeight = Math.min((int)((((double)height) / ((double)maxHeight)) * (height - (2 * scrollButtonHeight))), height - (2 * scrollButtonHeight));		
	}

	public void removeItem(int item){
		nameList.remove(item);
		maxHeight = nameList.size() * (int)(textHeight);
		scrollBarHeight = Math.min((int)((((double)height) / ((double)maxHeight)) * (height - (2 * scrollButtonHeight))), height - (2 * scrollButtonHeight));		
	}	
	
	public void clearItems(){
		nameList.clear();
		maxHeight = 0;
	}
	
	public void step(){
		if(parent instanceof GuiObject){
			xOffset = ((GuiObject)parent).getX();
			yOffset = ((GuiObject)parent).getY();
		}
		
		if(mouseInRectangle(x + xOffset, y + yOffset, width, height) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED)
			parent.switchFocus(this);
		
		//let user click selection
		if(mouseInRectangle(x + xOffset, y + yOffset, width - scrollWidth, height) && device.getMouseState()[Main.LEFT] == Game.KEY_PRESSED){
			double adjustedMouseY = device.mouseY - y - yOffset - scrollOffset;
			selectedIndex = (int) (adjustedMouseY / textHeight);
		}
		
		//process click and drag for scroll bar
		if(device.getMouseState()[Main.LEFT] == Game.KEY_RELEASED)
			dragMode = false;
		
		if(!dragMode){
			//check to see if the mouse is within the scroll bar and the user has clicked once
			if(mouseInRectangle(width - (scrollWidth + 2) + x + xOffset, scrollWidth + 1 + y + yOffset + 1 + scrollBarY, scrollWidth, scrollBarHeight) && device.getMouseState()[Main.LEFT] == Game.KEY_HELD){
				//calculate what part of the scroll bar was clicked
				mouseYOffset = device.mouseY - scrollButtonHeight - y - yOffset - scrollBarY - 2;
				dragMode = true;
			}
		}
		
		else{
			//scroll as the user moves the mouse
			scrollBarY = device.mouseY - scrollButtonHeight - y - yOffset - 2 - mouseYOffset;
		}
		
		//Processing scroll bar
		if(scrollBarY < 0)
			scrollBarY = 0;
		if(scrollBarY > height - scrollBarHeight - (2 * scrollButtonHeight))
			scrollBarY = height - scrollBarHeight - (2 * scrollButtonHeight);
		
		//adjust text height for scroll bar position
		scrollOffset = -(int) (( ((double) scrollBarY) / ((double) (height - (2 * scrollButtonHeight))) ) * maxHeight);
				
		for(GuiObject gui: components)
			gui.step();
	}
	
	@Override
	public void draw() {
		//initiate drawing the surface
		surface.beginDraw();
		surface.textSize(14);
		
		surface.background(device.color(96, 96, 96));
		
		
		//draw the selection highlight bar
		surface.stroke(0, 0, 0, 0);
		surface.fill(255, 255, 255, 128);
		surface.rect(1, selectedIndex * textHeight + 1 + scrollOffset, width - 2, textHeight);
		
		//draw the scroll bar track
		surface.fill(0, 0, 0);
		surface.rect(width - (scrollWidth + 2), 1, scrollWidth + 1, height - 2);
		
		surface.stroke(255, 255, 255);
		//surface.rect(width - (scrollWidth + 1), 1, scrollWidth, scrollWidth);
		
		surface.fill(255, 255, 255);
		
		//draw the selections text
		for(int i = 0; i < nameList.size(); i++)
			surface.text(nameList.get(i), 2, ((i + 1) * textHeight) + scrollOffset - 1);
		
		//highlight the scroll bar if the user hovers their mouse over it
		if(mouseInRectangle(width - (scrollWidth + 2) + x + xOffset, scrollWidth + 1 + y + yOffset + 1 + scrollBarY, scrollWidth, scrollBarHeight))
			surface.fill(255, 255, 255, 128);
		else
			surface.fill(255, 255, 255, 0);
		
		//draw the scroll bar
		surface.rect(width - (scrollWidth + 2), scrollButtonHeight + 1 + scrollBarY, scrollWidth, scrollBarHeight);
	
		//draw the frame
		surface.fill(0, 0, 0, 0);
		surface.stroke(device.color(128, 128, 128));
		
		surface.rect(0, 0, width - 1, height - 1);
		
		surface.endDraw();
		device.image(surface, x + xOffset, y + yOffset);

		for(GuiObject gui: components)
			gui.draw();
	}

	public int getSelectedIndex(){
		return selectedIndex;
	}
	
	public LinkedList<Object> getNameList(){
		return (LinkedList<Object>) nameList.clone();
	}
	
	@Override
	public void initializeGui() {
		scrollUp = new ScrollButton(width - scrollButtonWidth - 2, 1, scrollButtonWidth, scrollButtonHeight, this, device, true);
		scrollDown = new ScrollButton(width - scrollButtonWidth - 2, height - scrollButtonHeight - 2, scrollButtonWidth, scrollButtonHeight, this, device, false);
		
		components.add(scrollUp);
		components.add(scrollDown);
	}

	@Override
	public void disposeGui() {

	}

	@Override
	public void hideGui(GuiObject obj) {
		for(GuiObject gui: components)
			gui.hide();
	}

	@Override
	public void showGui(GuiObject obj) {
		for(GuiObject gui: components)
			gui.show();		
	}

	@Override
	public void eventCallback(GuiObject button) {
		if(button == scrollDown){
			scrollBarY += Math.round((textHeight / ((double) maxHeight) * ((double) (height - (2 * scrollWidth)))));	
		}
		if(button == scrollUp){
			scrollBarY -= Math.round((textHeight / ((double) maxHeight) * ((double) (height - (2 * scrollWidth)))));
		}
	}

	@Override
	public void switchFocus(GuiObject target) {
		// TODO Auto-generated method stub
		
	}
	
	protected class ScrollButton extends Button{
		private boolean upArrow;
		
		public ScrollButton(int x, int y, int width, int height, GuiCreator parent, Main device, boolean upArrow) {
			super(x, y, width, height, "", parent, device);
			this.upArrow = upArrow;
		}
		
		public void step(){
			if(isHidden)
				return;
			//super.step();
			
			xOffset = ((GuiObject)parent).getX();
			yOffset = ((GuiObject)parent).getY();
			
			//button clicked
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
			
			device.rectMode(Main.CENTER);
			if(upArrow)
				device.triangle(x - 4 + xOffset + (width / 2), y + 2 + yOffset + (height / 2), x + 4 + xOffset + (width / 2), y + 2 + yOffset + (height / 2), x + xOffset + (width / 2), y - 4 + yOffset + (height / 2));
			else
				device.triangle(x - 4 + xOffset + (width / 2), y - 4 + yOffset + (height / 2), x + 4 + xOffset + (width / 2), y - 4 + yOffset + (height / 2), x + xOffset + (width / 2), y + 2 + yOffset + (height / 2));
			
			device.rectMode(Main.CORNER);
			
			//System.out.println("scroll button drawn");
		}
	}
}