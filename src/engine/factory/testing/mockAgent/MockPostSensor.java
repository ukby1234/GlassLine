package engine.factory.testing.mockAgent;

import engine.factory.interfaces.Conveyor;
import engine.factory.interfaces.Popup;
import engine.factory.interfaces.PostSensor;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class MockPostSensor implements PostSensor {
	public EventLog events = new EventLog();
	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Receive part " + p.type));
	}

	@Override
	public void msgPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgReleased() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgPopupLow() {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Lowered"));
	}

	@Override
	public void msgPopupRaise() {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Raised"));
	}

	@Override
	public void setConveyor(Conveyor p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPopupAgent(Popup p) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
