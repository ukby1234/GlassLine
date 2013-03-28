package engine.factory;

import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.conveyorFamily.*;

public class ConveyorFamilyAgent implements ConveyorFamily {
	Conveyor conv;
	PostSensor postSensor;
	PreSensor preSensor;
	Popup popup;
	public ConveyorFamilyAgent() {
		conv = new ConveyorAgent("Conveyor");
		postSensor = new ConveyorExitSensorAgent("Exit");
		preSensor = new ConveyorEntrySensorAgent("Entry");
		popup = new PopupAgent("Popup");
		conv.setPostSensor(postSensor);
		postSensor.setConveyor(conv);
		postSensor.setPopupAgent(popup);
		preSensor.setConveyor(conv);
		popup.setPostSensor(postSensor);
	}
	@Override
	public void msgisAvailable(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			popup.msgConveyorReady();
		}
		else {
			popup.msgConveyorStopped();
		}
	}

	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		preSensor.msgHereIsParts(p);
	}
	
	public void startThread() {
		
	}
	
	public void setPrevConv(ConveyorFamily conv) {
		preSensor.setPrevConv(conv);
	}
	
	public void setNextConv(ConveyorFamily conv) {
		popup.setNextConvFamily(conv);
	}

}
