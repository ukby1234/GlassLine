package engine.factory.interfaces;

import engine.factory.shared.Part;

public interface Popup {
	public void msgConveyorReady();
	public void msgConveyorStopped();
	public void msgHereIsParts(Part p);
	public void msgLoadingFinished();
	public void msgPartsDone();
	public void msgMoveFinished();
	public void setPostSensor(PostSensor s);
	public void setNextConvFamily(ConveyorFamily conv);
	public String getName();
}
