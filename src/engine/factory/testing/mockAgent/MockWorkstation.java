package engine.factory.testing.mockAgent;

import engine.factory.interfaces.Workstation;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class MockWorkstation implements Workstation {
	public EventLog events = new EventLog();
	@Override
	public void msgIsPopupAvailable(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			events.add(new LoggedEvent("Available"));
		}
		else {
			events.add(new LoggedEvent("Not Available"));
		}
	}

	@Override
	public void msgSendGlass(Part p) {
		// TODO Auto-generated method stub
		events.add(new LoggedEvent("Receive parts " + p.type));
	}

}
