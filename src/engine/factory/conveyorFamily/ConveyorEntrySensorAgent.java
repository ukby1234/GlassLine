package engine.factory.conveyorFamily;

import engine.factory.testing.util.*;
import transducer.*;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import java.util.*;

public class ConveyorEntrySensorAgent extends Agent implements PreSensor{
	public EventLog events = new EventLog();
	public List<Part> parts = Collections.synchronizedList(new ArrayList<Part>());
	public static enum SensorState {Pressed, Released, Nothing};
	public SensorState sensorState = SensorState.Nothing;
	public Conveyor conveyor;
	public ConveyorFamily prevConv;
	public int index;
	public boolean flag = false;
	//Messaging
	
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		parts.add(p);
		stateChanged();
	}

	public void msgPressed() {
		// TODO Auto-generated method stub
		sensorState = SensorState.Pressed;
		print("Sensor Pressed");
		events.add(new LoggedEvent("Sensor Pressed"));
		stateChanged();
	}
	
	public void msgReleased() {
		// TODO Auto-generated method stub
		sensorState = SensorState.Released;
		print("Sensor Released");
		events.add(new LoggedEvent("Sensor Released"));
		stateChanged();
	}
	
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(sensorState == SensorState.Pressed && !flag) {
			informPrevConveyor();
			flag = true;
			/*try {
				Thread.sleep(500);
			}catch(InterruptedException e) {}
			msgReleased();*/
			return true;
		}
		
		if(sensorState == SensorState.Released) {
			informNextConveyor();
			informPrevConveyor();
			sensorState = SensorState.Nothing;
			flag = false;
			return true;
		}
			
		return false;
	}
	//Actions
	private void informPrevConveyor() {
		prevConv.msgisAvailable(sensorState == SensorState.Released);
		if (sensorState == SensorState.Pressed) {
			print("Stop Conveyor");
			events.add(new LoggedEvent("Stop Conveyor"));
		}
		else {
			print("Start Conveyor");
			events.add(new LoggedEvent("Start Conveyor"));
		}
	}
	
	private void informNextConveyor() {
		conveyor.msgHereIsParts(parts.remove(0));
	}
	//Other Methods
	public ConveyorEntrySensorAgent(String agentName, int index) {
		super(agentName);
		this.index = index;
		// TODO Auto-generated constructor stub
	}

	public ConveyorEntrySensorAgent(String agentName, Transducer ft, int index) {
		super(agentName, ft);
		transducer.register(this, TChannel.SENSOR);
		this.index = index;
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
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
	
	public void setConveyor(Conveyor c) {
		conveyor = c;
	}
	
	public void setPrevConv(ConveyorFamily conv) {
		prevConv = conv;
	}

	

}
