package engine.factory.interfaces;

import engine.factory.shared.Part;

public interface PostSensor {
	public void msgHereIsParts(Part p);
	public void msgPressed();
	public void msgReleased();
	public void msgPopupLow();
	public void msgPopupRaise();
	public void setConveyor(Conveyor p);
	public void setPopupAgent(Popup p);
	public String getName();
}
