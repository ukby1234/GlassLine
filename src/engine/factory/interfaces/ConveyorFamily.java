package engine.factory.interfaces;

import engine.factory.shared.Part;

public interface ConveyorFamily{
	public void msgisAvailable(boolean state);
	public void msgHereIsParts(Part p);
}
