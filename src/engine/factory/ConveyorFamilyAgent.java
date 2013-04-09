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
	Transducer t;
	WorkstationAgent ws1, ws2;
	public ConveyorFamilyAgent(Transducer trans) {
		t = trans;
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
	
	public void setPopupIndex(int index) {
		popup = new PopupAgent("Popup", t, index);
	}
	
	public void setConvIndex(int index) {
		conv = new ConveyorAgent("Conveyor", t, index);
		conv.setPostSensor(postSensor);
	}
	
	public void setPostIndex(int index) {
		postSensor = new ConveyorExitSensorAgent("Exit", t, index);
	}
	
	public void setPreIndex(int index) {
		preSensor = new ConveyorEntrySensorAgent("Entry", t, index);
	}
	
	public void setUp() {
		conv.setPostSensor(postSensor);
		postSensor.setConveyor(conv);
		postSensor.setPopupAgent(popup);
		preSensor.setConveyor(conv);
		popup.setPostSensor(postSensor);
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
	}
	
	public void setWorkstation(WorkstationAgent ws1, WorkstationAgent ws2) {
		this.ws1 = ws1;
		this.ws2 = ws2;
	}
	
	public Popup getPopup() {
		return popup;
	}

}
