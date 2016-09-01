package gui;

import java.util.LinkedList;

import main.Game;
import main.Main;
import starmap.StarSystem;

public class SystemScrollList extends ScrollList{

	protected int scrollButtonWidth = 16;
	protected int scrollButtonHeight = 16;
	
	protected int scrollWidth = 16;
	
	protected LinkedList<StarSystem> itemList;
	
	public SystemScrollList(int x, int y, int width, int height, GuiCreator parent, Main device) {
		super(x, y, width, height, parent, device);
		itemList = new LinkedList<StarSystem>();
	}
	
	public void addItem(StarSystem item){
		itemList.add(item);
		super.addItem(item.getName());
	}
	
	public void removeItem(StarSystem item){
		itemList.remove(item);
		super.removeItem(item.getName());
	}

	public void removeItem(int item){
		itemList.remove(item);
		super.removeItem(item);
	}
	
	public void clearItems(){
		itemList.clear();
		super.clearItems();
	}
	
	public LinkedList<Object> getItemList(){
		return (LinkedList<Object>) itemList.clone();
	}
}
