package engine.factory;

import transducer.*;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.conveyorFamily.*;

public class ConveyorFamilyAgent implements ConveyorFamily {
	ConveyorAgent conv;
	ConveyorExitSensorAgent postSensor;
	ConveyorEntrySensorAgent preSensor;
	PopupAgent popup;
	public ConveyorFamilyAgent(int index, Transducer trans) {
		conv = new ConveyorAgent("Conveyor", trans, index);
		postSensor = new ConveyorExitSensorAgent("Exit", trans, index + 1);
		preSensor = new ConveyorEntrySensorAgent("Entry", trans, index);
		popup = new PopupAgent("Popup", trans, index);
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
		conv.startThread();
		preSensor.startThread();
		postSensor.startThread();
		popup.startThread();
	}
	
	public void stopThread() {
		conv.stopAgent();
		preSensor.stopAgent();
		postSensor.stopAgent();
		popup.stopAgent();
	}
	
	public void setPrevConv(ConveyorFamily conv) {
		preSensor.setPrevConv(conv);
	}
	
	public void setNextConv(ConveyorFamily conv) {
		popup.setNextConvFamily(conv);
	}

}
