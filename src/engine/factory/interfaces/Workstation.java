package engine.factory.interfaces;

import engine.factory.shared.Part;

public interface Workstation {
	public void msgIsPopupAvailable(boolean state);
	public void msgSendGlass(Part p);
}
