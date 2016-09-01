package gui;

public interface GuiCreator {
	public void initializeGui();
	public void disposeGui();
	public void hideGui(GuiObject obj);
	public void showGui(GuiObject obj);
	public void eventCallback(GuiObject button);
	abstract void switchFocus(GuiObject target);
}
