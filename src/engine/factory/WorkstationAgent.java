package engine.factory;

import java.util.concurrent.Semaphore;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;

public class WorkstationAgent extends Agent implements Workstation {
	private ConveyorFamily family;
	private int operatorIndex;
	private Part glass;
	private boolean popupAvailable = false;
	private OperatorStatus status = OperatorStatus.NATURAL;

	public enum OperatorStatus {
		RECEIVED_GLASS, LOADED_GLASS, WORKING, FINISHED_WORKING, RELEASING, NATURAL
	};

	private Semaphore popupLevelSemaphore = new Semaphore(0);

	private Transducer t;
	private TChannel channelName;

	public WorkstationAgent(ConveyorFamily family, int index, TChannel channelName) {
		super("Workstation");
		this.operatorIndex = index;
		this.channelName = channelName;
		this.family = family;
	}

	@Override
	public void msgSendGlass(Part glass) {
		this.glass = glass;
		status = OperatorStatus.RECEIVED_GLASS;
		print("Received");
		stateChanged();
	}

	public void msgLoadedGlass() {
		status = OperatorStatus.LOADED_GLASS;
		family.getPopup().msgLoadedGlass();
		stateChanged();
	}

	public void msgFinishedWorking() {
		print("Finished");
		status = OperatorStatus.FINISHED_WORKING;
		stateChanged();
	}

	public void msgFinishedReleasing() {
		family.getPopup().msgSendGlassToPopup(glass);
		glass = null;
		status = OperatorStatus.NATURAL;
		stateChanged();
	}

	public void msgIsPopupAvailable(boolean avail) {
		popupAvailable = avail;
		if (avail)
			popupLevelSemaphore.release();
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if (status != OperatorStatus.NATURAL) {
			if (status == OperatorStatus.RECEIVED_GLASS) {
				print("Here");
				loadGlass();
				status = OperatorStatus.NATURAL;
				return true;
			} else if (status == OperatorStatus.LOADED_GLASS) {
				startWorking();
				status = OperatorStatus.WORKING;
			} else if (status == OperatorStatus.FINISHED_WORKING) {
				print("lasd");
				if (!popupAvailable) {
					sendGlass();
				}
			}
		}
		return false;
	}

	public void setTransducer(Transducer t) {
		this.t = t;
		t.register(this, channelName);
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (((Integer) args[0]) == operatorIndex) {
			if (event == TEvent.WORKSTATION_LOAD_FINISHED) {
				msgLoadedGlass();
			} else if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
				msgFinishedWorking();
			} else if (event == TEvent.WORKSTATION_RELEASE_FINISHED) {
				msgFinishedReleasing();
			}
		}
	}

	public void loadGlass() {
		if (t != null) {
			t.fireEvent(channelName, TEvent.WORKSTATION_DO_LOAD_GLASS, new Object[] { operatorIndex });
		}
	}

	public void startWorking() {
		if (t != null) {
			t.fireEvent(channelName, TEvent.WORKSTATION_DO_ACTION, new Object[] { operatorIndex });
		}
	}

	public void sendGlass() {
		
		try {
			status = OperatorStatus.RELEASING;
			family.getPopup().msgMovePopup(this);
			popupLevelSemaphore.acquire();
			if (t != null) {
				t.fireEvent(channelName, TEvent.WORKSTATION_RELEASE_GLASS, new Object[] { operatorIndex });
			}
			popupAvailable = false;
			family.getPopup().msgIsAvailable(this, true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public TChannel getChannel() {
		return channelName;
	}

}