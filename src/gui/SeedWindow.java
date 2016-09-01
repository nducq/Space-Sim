package gui;

import main.Game;
import main.Main;

public class SeedWindow extends Window{

	TextLabel fieldLabelSeed;
	TextField fieldSeed;
	
	TextLabel fieldLabelRoots;
	TextField fieldRoots;
	
	Button okButton;
	
	public SeedWindow(int x, int y, GuiCreator parent, Main main) {
		super(x, y, 400, 300, parent, main);
		intializeGui();
	}
	
	public void intializeGui(){
		fieldLabelSeed = new TextLabel(20, 20, 360, 50, "Enter seed:", this, device);
		fieldSeed = new TextField(20, 80, 360, 24, this, device);
		
		fieldLabelRoots = new TextLabel(20, 140, 360, 50, "Enter number of roots:", this, device);
		fieldRoots = new TextField(20, 200, 360, 24, this, device);
		okButton = new Button(20, 255, 60, 25, "Generate", this, device);
		
		components.clear();
		
		components.add(fieldLabelSeed);
		components.add(fieldSeed);
		components.add(fieldLabelRoots);
		components.add(fieldRoots);
		components.add(okButton);
	}
	
	public String getSeed(){
		return fieldSeed.getText();
	}
	
	public int getNumRoots(){
		int ret = Main.NUM_ROOTS;
		try{
			ret = Integer.parseInt(fieldRoots.getText());
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void eventCallback(GuiObject event) {
		inFocus = false;
		switchFocus(event);
		if(event == okButton){
			parent.hideGui(this);
			parent.eventCallback(this);
		}
	}
	
}
