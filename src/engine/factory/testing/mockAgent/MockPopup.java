package engine.factory.testing.mockAgent;

import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class MockPopup implements Popup {
	public EventLog events = new EventLog();
	@Override
	public void msgConveyorReady() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgConveyorStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Receive part " + p.type));
	}

	@Override
	public void msgLoadingFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgMoveFinished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPostSensor(PostSensor s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNextConvFamily(ConveyorFamily conv) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgSendGlassToPopup(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Receive part " + p.type));
	}

	@Override
	public void msgMovePopup(Workstation ws) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWorkStation(Workstation ws) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLoadedGlass() {
		// TODO Auto-generated method stub
		
	}

}
