package engine.factory.conveyorFamily;

import java.util.*;
import transducer.*;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.*;
import engine.factory.testing.util.*;
import java.util.concurrent.*;

public class ConveyorExitSensorAgent extends Agent implements PostSensor{
	public EventLog events = new EventLog();
	public List<Part> parts = Collections.synchronizedList(new ArrayList<Part>());
	public Part currentPart = null;
	public enum SensorState {Pressed, Released, Nothing};
	public enum PopupState {Up, Down};
	public enum ConveyorState {Running, Stopped};
	public SensorState sensorState = SensorState.Nothing;
	public PopupState popupState = PopupState.Down;
	public ConveyorState conveyorState = ConveyorState.Running;
	Conveyor conveyor;
	Popup popup;
	public int index;
	//Semaphore sem = new Semaphore(0, true);
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		print("Receive Part " + p.type);
		events.add(new LoggedEvent("Receive Part " + p.type));
		parts.add(p);
		//msgPressed();
		stateChanged();
	}

	public void msgPressed() {
		// TODO Auto-generated method stub
		sensorState = SensorState.Pressed;
		events.add(new LoggedEvent("Pressed"));
		stateChanged();
	}

	public void msgReleased() {
		// TODO Auto-generated method stub
		sensorState = SensorState.Released;
		//sem.release();
		print("Parts released");
		events.add(new LoggedEvent("Released"));
		stateChanged();	
	}

	public void msgPopupLow() {
		// TODO Auto-generated method stub
		popupState = PopupState.Down;
		print("Pop Lowered");
		events.add(new LoggedEvent("Pop Lowered"));
		stateChanged();
	}
	
	public void msgPopupRaise() {
		// TODO Auto-generated method stub
		popupState = PopupState.Up;
		print("Pop Raised");
		events.add(new LoggedEvent("Pop Raised"));
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (sensorState == SensorState.Pressed && popupState == PopupState.Up && conveyorState == ConveyorState.Running) {
			print("1");
			stopConveyor();	
			return true;
		}
		
		/*if (sensorState == SensorState.Pressed && popupState == PopupState.Down && conveyorState == ConveyorState.Running) {
			print("2");
			print("" + parts.isEmpty());
			try {
				Thread.sleep(500);
			}catch(InterruptedException e) {}
			msgReleased();		
			return true;
		}*/
		
		if (popupState == PopupState.Down && conveyorState == ConveyorState.Stopped) {
			print("3");
			startConveyor();		
			return true;
		}
		
		if (sensorState == SensorState.Released) {
			print("4");
			loadPopup();		
			sensorState = SensorState.Nothing;
			return true;
		}
		return false;
	}

	//Actions
	private void stopConveyor() {
		conveyor.msgStopConveyor();
		conveyorState = ConveyorState.Stopped;
	}
	
	private void startConveyor() {
		conveyor.msgStartConveyor();
		conveyorState = ConveyorState.Running;
		//while(!sem.tryAcquire());
		//loadPopup();
	}

	private void loadPopup() {
		print("Give parts to popup");
		events.add(new LoggedEvent("Give parts to popup"));
		popup.msgHereIsParts(parts.remove(0));	
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		int index = -1;
		if (args[0] instanceof Integer) {
			index = (Integer)args[0];
		}
		if (index == this.index) {
			if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED)
				msgPressed();
			else if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED)
				msgReleased();
				
		}
	}

	public ConveyorExitSensorAgent(String agentName, int index) {
		super(agentName);
		this.index = index;
		// TODO Auto-generated constructor stub
	}

	public ConveyorExitSensorAgent(String agentName, Transducer ft, int index) {
		super(agentName, ft);
		transducer.register(this, TChannel.SENSOR);
		this.index = index;
		// TODO Auto-generated constructor stub
	}

	public void setConveyor(Conveyor p) {
		conveyor = p;
	}
	
	public void setPopupAgent(Popup p) {
		popup = p;
	}
	
	public String getName() {
		return name;
	}
	

}
