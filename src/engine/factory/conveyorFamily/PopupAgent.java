package engine.factory.conveyorFamily;

import transducer.*;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.util.*;
import java.util.*;
import java.util.concurrent.*;

public class PopupAgent extends Agent implements Popup{
	public EventLog events = new EventLog();
	public List<MyWorkstation> workstations = Collections.synchronizedList(new ArrayList<MyWorkstation>());
	public MyParts currentPart = null;
	ConveyorFamily nextConv;
	public static enum ConveyorState {Running, Stopped};
	public static enum PartState {PartsComing, NeedMoving, NeedProcessing, Processed, Leaving, Leaved};
	public static enum WorkstationState {Working, Stopped};
	public ConveyorState conveyorState = ConveyorState.Running;
	public static enum PopupState {Up, Down};
	public PopupState popupState = PopupState.Down;
	int index;
	String type = "x";
	PostSensor s;
	Semaphore sem = new Semaphore(0, true);
	boolean isStopped = false;
	public class MyParts {
		public Part part;
		public PartState partState;
		public boolean isProcessed;
		public MyParts(Part p, PartState s) {
			part = p;
			partState = s;
			isProcessed = false;
		}
	}
	public class MyWorkstation {
		public Workstation workStation;
		public WorkstationState state;
		public boolean needMove;
		public MyWorkstation(Workstation w, WorkstationState s) {
			workStation = w;
			state = s;
			needMove  = false;
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
	
	public void msgSendGlassToPopup(Part p) {
		msgHereIsParts(p);
		currentPart.isProcessed = true;
	}

	public void msgLoadingFinished() {
		sem.release();
		stateChanged();
	}

	public void msgMovePopup(Workstation ws) {
		synchronized (workstations) {
			for (MyWorkstation w : workstations) {
				if (w.workStation == ws) {
					print("found");
					w.needMove = true;
				}
			}
		}
		stateChanged();
	}

	public void msgMoveFinished() {
		sem.release();
		stateChanged();
	}
	
	public void msgIsAvailable(Workstation w, boolean s) {
		synchronized (workstations) {
			for (MyWorkstation ws : workstations) {
				if (ws == w) {
					if (s)
						ws.state = WorkstationState.Working;
					else
						ws.state = WorkstationState.Stopped;
				}
			}
		}
		stateChanged();
	}
	
	public void msgPopupReleased() {
		sem.release();
		stateChanged();
	}
	//Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub		

		//print("t" + (conveyorState == ConveyorState.Running));
		if (isStopped && getWorkstation() != null && currentPart == null && popupState == PopupState.Down) {
			print("1");
			startConveyor();
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.PartsComing) {
			print("2");
			loadPopup();
			if (popupState == PopupState.Up || (popupState == PopupState.Down && currentPart.part.type == type))
				currentPart.partState = PartState.NeedMoving;
			else
				currentPart.partState = PartState.Leaving;
			return true;
		}

		MyWorkstation ws = null;
		synchronized (workstations) {
			for (MyWorkstation w : workstations) {
				if (w.needMove) {
					ws = w;
					break;
				}
			}
		}
		print("" + (ws == null));
		if (ws != null && popupState == PopupState.Up) {
			print("3");
			informRobot(ws);
			return true;
		}
		
		if (ws != null && currentPart == null) {
			print("4");
			stopConveyor();
			movePopup(1);
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.NeedMoving) {
			print("5");
			if (getWorkstation() == null) {
				if (!isStopped)
					stopConveyor();
				return true;
			}
			movePopup(popupState == PopupState.Down ? 1 : 0);
			if (!currentPart.isProcessed)
				currentPart.partState = PartState.NeedProcessing;
			else
				currentPart.partState = PartState.Leaving;
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.NeedProcessing) {
			print("6");
			processPart(currentPart);
			currentPart = null;
			movePopup(0);
			startConveyor();
			return true;
		}

		if (currentPart != null && currentPart.partState == PartState.Leaving && conveyorState == ConveyorState.Stopped) {
			print("7");
			events.add(new LoggedEvent("Blocked"));
		}
		
		if (currentPart != null && currentPart.partState == PartState.Leaving && conveyorState == ConveyorState.Running) {
			print("8");
			events.add(new LoggedEvent("Here"));
			print("Here");
			releasePopup();
			currentPart = null;
			return true;
		}
		return false;
	}

	//Actions
	private void loadPopup() {
		stopConveyor();
		print("Loading popup");
		events.add(new LoggedEvent("Loading popup"));
		//msgLoadingFinished();
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
			//msgMoveFinished();
			popupState = PopupState.Down;
			//s.msgPopupLow();
		}
		else {
			//s.msgPopupRaise();
			Do("Move Up");
			events.add(new LoggedEvent("Move Up"));
			//msgMoveFinished();
			popupState = PopupState.Up;	
		}
		while(!sem.tryAcquire());
		print("Move finished");
		events.add(new LoggedEvent("Move finished"));
	}

	private void releasePopup(){
		print("Released");
		events.add(new LoggedEvent("Released"));
		while(!sem.tryAcquire());
		nextConv.msgHereIsParts(currentPart.part);
		//capacity++;
		startConveyor();
		//s.msgPopupLow();
	}

	private void processPart(MyParts p) {
		print(p.part.type + " is processing");
		events.add(new LoggedEvent(p.part.type + " is processing"));
		MyWorkstation temp = getWorkstation();
		temp.workStation.msgSendGlass(p.part);
		temp.state = WorkstationState.Stopped;
		//capacity--;
	}

	private void informRobot(MyWorkstation ws) {
		print("I'm ready");
		events.add(new LoggedEvent("I'm ready"));
		ws.workStation.msgIsPopupAvailable();
		ws.needMove = false;
	}
	
	private MyWorkstation getWorkstation() {
		synchronized (workstations) {
			for (MyWorkstation ws : workstations) {
				if (ws.state == WorkstationState.Working)
					return ws;
			}
		}
		return null;
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
		int index = -1;
		if (args[0] instanceof Integer) {
			index = (Integer)args[0];
		}
		if (index == this.index) {
			if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_LOAD_FINISHED)
				msgLoadingFinished();
			if (channel == TChannel.POPUP && (event == TEvent.POPUP_GUI_MOVED_DOWN || 
					event == TEvent.POPUP_GUI_MOVED_UP))
				msgMoveFinished();
			if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_RELEASE_FINISHED)
				msgPopupReleased();
				
		}
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
	
	public void addWorkStation(Workstation ws) {
		workstations.add(new MyWorkstation(ws, WorkstationState.Working));
	}
}
