package engine.factory.conveyorFamily;

import transducer.*;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;

import java.util.concurrent.*;

public class PopupAgent extends Agent implements Popup{
	public EventLog events = new EventLog();
	public MyParts currentPart = null;
	ConveyorFamily nextConv;
	enum ConveyorState {Running, Stopped};
	enum PartState {PartsComing, NeedMoving, NeedProcessing, Processed, Leaving, Leaved};
	ConveyorState conveyorState = ConveyorState.Running;
	enum PopupState {Up, Down};
	PopupState popupState = PopupState.Down;
	String type = "x";
	int capacity = 2;
	PostSensor s;
	Semaphore sem = new Semaphore(0, true);
	boolean needMove = false;
	boolean isStopped = false;
	private class MyParts {
		public Part part;
		public PartState partState;
		public MyParts(Part p, PartState s) {
			part = p;
			partState = s;
		}
	}
	//Messaging
	public void msgConveyorReady() {
		events.add(new LoggedEvent("Conv Start"));
		conveyorState =  ConveyorState.Running;
		stateChanged();
	}

	public void msgConveyorStopped() {
		events.add(new LoggedEvent("Conv Stop"));
		conveyorState =  ConveyorState.Stopped;
		stateChanged();
	}

	public void msgHereIsParts(Part p) {
		print("Parts Received");
		events.add(new LoggedEvent("Parts Received"));
		currentPart = new MyParts(p, PartState.PartsComing);
		stateChanged();
	}

	public void msgLoadingFinished() {
		sem.release();
		stateChanged();
	}

	public void msgPartsDone() {
		needMove = true;
		stateChanged();
	}

	public void msgMoveFinished() {
		sem.release();
		stateChanged();
	}
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub		

		if (isStopped && capacity > 0 && currentPart == null && popupState == PopupState.Down) {
			startConveyor();
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.PartsComing) {
			loadPopup();
			if (popupState == PopupState.Up || (popupState == PopupState.Down && currentPart.part.type == type))
				currentPart.partState = PartState.NeedMoving;
			else
				currentPart.partState = PartState.Leaving;
			return true;
		}

		if (needMove && popupState == PopupState.Up) {
			informRobot();
			return true;
		}
		
		if (needMove && currentPart == null) {
			stopConveyor();
			movePopup(1);
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.NeedMoving) {
			if (capacity == 0) {
				if (!isStopped)
					stopConveyor();
				return true;
			}
			movePopup(popupState == PopupState.Down ? 1 : 0);
			if (!currentPart.part.processed)
				currentPart.partState = PartState.NeedProcessing;
			else
				currentPart.partState = PartState.Leaving;
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.NeedProcessing) {
			processPart(currentPart);
			currentPart = null;
			movePopup(0);
			startConveyor();
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.Leaving && conveyorState == ConveyorState.Running) {
			releasePopup();
			currentPart = null;
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.Leaving && conveyorState == ConveyorState.Stopped) {
			events.add(new LoggedEvent("Blocked"));
		}


		return false;
	}

	//Actions
	private void loadPopup() {
		stopConveyor();
		print("Loading popup");
		events.add(new LoggedEvent("Loading popup"));
		msgLoadingFinished();
		while(!sem.tryAcquire());
		print("Loading finished");
		events.add(new LoggedEvent("Loading finished"));
	}

	private void stopConveyor() {
		s.msgPopupRaise();
		isStopped = true;
	}

	private void startConveyor() {
		s.msgPopupLow();
		isStopped = false;
	}

	private void movePopup(int direction) {
		if (direction == 0) {
			Do("Move Down");
			events.add(new LoggedEvent("Move Down"));
			msgMoveFinished();
			popupState = PopupState.Down;
			//s.msgPopupLow();
		}
		else {
			//s.msgPopupRaise();
			Do("Move Up");
			events.add(new LoggedEvent("Move Up"));
			msgMoveFinished();
			popupState = PopupState.Up;	
		}
		while(!sem.tryAcquire());
		print("Move finished");
		events.add(new LoggedEvent("Move finished"));
	}

	private void releasePopup(){
		print("Released");
		events.add(new LoggedEvent("Released"));
		nextConv.msgHereIsParts(currentPart.part);
		capacity++;
		startConveyor();
		//s.msgPopupLow();
	}

	private void processPart(MyParts p) {
		print(p.part.type + " is processing");
		events.add(new LoggedEvent(p.part.type + " is processing"));
		capacity--;
	}

	private void informRobot() {
		print("I'm ready");
		events.add(new LoggedEvent("I'm ready"));
		needMove = false;
	}

	public PopupAgent(String agentName) {
		super(agentName);
		// TODO Auto-generated constructor stub
	}

	public PopupAgent(String agentName, Transducer ft) {
		super(agentName, ft);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return name;
	}

	public void setPostSensor(PostSensor s) {
		this.s = s;
	}

	public void setNextConvFamily(ConveyorFamily conv) {
		nextConv = conv;
	}
}
