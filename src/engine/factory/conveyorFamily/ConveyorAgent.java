package engine.factory.conveyorFamily;

import java.util.*;
import transducer.*;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

public class ConveyorAgent extends Agent implements Conveyor {
	public EventLog events = new EventLog();
	public List<Part> parts = Collections.synchronizedList(new ArrayList<Part>()); 
	public static enum ConveyorState {Running, Stopped};
	public ConveyorState conveyorState = ConveyorState.Running;
	public boolean changed = false;
	PostSensor exitSensor;
	//Messaging
	@Override
	public void msgHereIsParts(Part p) {
		// TODO Auto-generated method stub
		parts.add(p);
		stateChanged();
	}

	@Override
	public void msgStopConveyor() {
		// TODO Auto-generated method stub
		conveyorState = ConveyorState.Stopped;
		changed = true;
		stateChanged();
	}
	
	@Override
	public void msgStartConveyor() {
		// TODO Auto-generated method stub
		conveyorState = ConveyorState.Running;
		changed = true;
		stateChanged();
	}

	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (changed) {
			doConveyor();
			return true;
		}
		if (!parts.isEmpty()) {
			sendParts();
			return true;
		}
		return false;
	}

	//Actions
	private void doConveyor() {
		if (conveyorState == ConveyorState.Running) {
			print("Start Conveyor");
			events.add(new LoggedEvent("Start Conveyor"));
		}
		else {
			print("Stop Conveyor");
			events.add(new LoggedEvent("Stop Conveyor"));
		}
		changed = false;
	}
	
	private void sendParts() {
		print("Give parts to post sensor");
		events.add(new LoggedEvent("Give parts to post sensor"));
		exitSensor.msgHereIsParts(parts.remove(0));	
	}
	public ConveyorAgent() {
		// TODO Auto-generated constructor stub
	}

	public ConveyorAgent(String agentName) {
		super(agentName);
		// TODO Auto-generated constructor stub
	}

	public ConveyorAgent(String agentName, Transducer ft) {
		super(agentName, ft);
		// TODO Auto-generated constructor stub
	}
	
	public void setPostSensor(PostSensor s) {
		exitSensor = s;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}
	
	public String getName() {
		return name;
	}

}
