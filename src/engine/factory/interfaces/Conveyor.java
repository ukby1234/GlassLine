package engine.factory.interfaces;

import engine.factory.shared.Part;

public interface Conveyor {
	public void msgHereIsParts(Part p);
	public void msgStopConveyor();
	public void msgStartConveyor();
	public void setPostSensor(PostSensor s);
	public String getName();
}
