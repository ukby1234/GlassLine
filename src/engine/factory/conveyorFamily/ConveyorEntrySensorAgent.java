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
	public enum SensorState {Pressed, Released, Nothing};
	public SensorState sensorState = SensorState.Nothing;
	public Conveyor conveyor;
	public ConveyorFamily prevConv;
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
		if(sensorState == SensorState.Pressed) {
			informPrevConveyor();
			try {
				Thread.sleep(500);
			}catch(InterruptedException e) {}
			msgReleased();
			return true;
		}
		
		if(sensorState == SensorState.Released) {
			informNextConveyor();
			informPrevConveyor();
			sensorState = SensorState.Nothing;
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
	public ConveyorEntrySensorAgent(String agentName) {
		super(agentName);
		// TODO Auto-generated constructor stub
	}

	public ConveyorEntrySensorAgent(String agentName, Transducer ft) {
		super(agentName, ft);
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}
	
	public void setConveyor(Conveyor c) {
		conveyor = c;
	}
	
	public void setPrevConv(ConveyorFamily conv) {
		prevConv = conv;
	}

	

}
