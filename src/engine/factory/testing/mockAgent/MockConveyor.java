package engine.factory.testing.mockAgent;

import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class MockConveyor implements Conveyor {
	public EventLog events = new EventLog();
	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Parts Receiving " + p.type));
	}

	@Override
	public void msgStopConveyor() {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Stop"));
	}

	@Override
	public void msgStartConveyor() {
		events.add(new LoggedEvent("Start"));

	}

	@Override
	public void setPostSensor(PostSensor s) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
