package engine.factory.interfaces;

import engine.factory.shared.Part;
public interface PreSensor {
	public void msgHereIsParts(Part p);
	public void msgPressed();
	public void msgReleased();
	public void setConveyor(Conveyor c);
	public void setPrevConv(ConveyorFamily conv);
	public String getName();
}
