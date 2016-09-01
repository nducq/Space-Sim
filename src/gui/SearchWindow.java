package gui;

import main.Game;
import main.Main;
import starmap.StarSystem;
import java.util.LinkedList;

public class SearchWindow extends Window{
	
	TextLabel fieldLabel;
	TextField field;
	
	Button okButton;
	Button cancelButton;
	
	SystemScrollList scrollList;
	
	public SearchWindow(int x, int y, GuiCreator parent, Main main) {
		super(x, y, 600, 450, parent, main);
		intializeGui();
	}
	
	public void intializeGui(){
		fieldLabel = new TextLabel(20, 20, 300, 50, "Enter system name:", this, device);
		field = new TextField(20, 80, 560, 24, this, device);
		okButton = new Button(20, 410, 60, 25, "OK", this, device);
		cancelButton = new Button(520, 410, 60, 25, "Cancel", this, device);
		scrollList = new SystemScrollList(20, 140, 560, 250, this, device);
		
		components.clear();
		
		components.add(fieldLabel);
		components.add(field);
		components.add(okButton);
		components.add(cancelButton);
		components.add(scrollList);
	}
	
	public void eventCallback(GuiObject event) {
		inFocus = false;
		switchFocus(event);
		if(event == okButton){
			parent.eventCallback(this);
			parent.hideGui(this);
		}
		if(event == cancelButton)
			parent.hideGui(this);
		if(event == field){
			scrollList.clearItems();
			for(StarSystem s: Main.searchSystemList(field.getText(), true))
				scrollList.addItem(s);
		}
	}
	
	public StarSystem getSelectedSystem(){ 
		return (StarSystem) scrollList.getItemList().get(scrollList.getSelectedIndex());
	}
}
