package engine.factory.testing.mockAgent;

import engine.factory.interfaces.ConveyorFamily;
import engine.factory.interfaces.Popup;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class MockConveyorFamily implements ConveyorFamily {
	public EventLog events = new EventLog();
	@Override
	public void msgisAvailable(boolean state) {
		// TODO Auto-generated method stub
		if(state) {
			events.add(new LoggedEvent("Conveyor Working"));
		}
		else {
			events.add(new LoggedEvent("Conveyor Stopping"));
		}
	}

	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Receive parts " + p.type));
	}

	@Override
	public Popup getPopup() {
		// TODO Auto-generated method stub
		return null;
	}

}
