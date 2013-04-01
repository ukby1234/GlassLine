package engine.factory.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import transducer.*;
import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.PopupAgent.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPopup {

	@Test
	public void testReceiveParts() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		Part p = new Part("x");
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertTrue(popup.currentPart != null);
		assertTrue(popup.events.containsString("Parts Received"));
	}
	
	@Test
	public void testStartStopConv() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		popup.conveyorState = ConveyorState.Stopped;
		popup.msgConveyorReady();
		assertTrue(popup.conveyorState == ConveyorState.Running);
		assertTrue(popup.events.containsString("Conv Start"));
		popup.msgConveyorStopped();
		assertTrue(popup.conveyorState == ConveyorState.Stopped);
		assertTrue(popup.events.containsString("Conv Stop"));
	}
	
	@Test
	public void testProcessOnePart() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		Object args[] = new Object[1];
		args[0] = 0;
		assertTrue(popup.workstations.isEmpty());
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		assertFalse(popup.workstations.isEmpty());
		MockPostSensor post = new MockPostSensor();
		popup.setPostSensor(post);
		Part p = new Part("x");
		assertTrue(popup.currentPart == null);
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_UP + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedProcessing);
		assertTrue(popup.popupState == PopupState.Up);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		MyWorkstation ws = null;
		for (MyWorkstation w : popup.workstations) {
			if (w.state == WorkstationState.Stopped) {
				ws = w;
				break;
			}
		}
		assertFalse(ws == null);
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_DOWN + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Down);
		assertTrue(post.events.containsString("Lowered"));
	}
	
	@Test
	public void testProcessTwoParts() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		Object args[] = new Object[1];
		args[0] = 0;
		assertTrue(popup.workstations.isEmpty());
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		assertFalse(popup.workstations.isEmpty());
		MockPostSensor post = new MockPostSensor();
		popup.setPostSensor(post);
		Part p = new Part("x");
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		System.out.println("Here");	
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_UP + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedProcessing);
		assertTrue(popup.popupState == PopupState.Up);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		MyWorkstation ws = null;
		for (MyWorkstation w : popup.workstations) {
			if (w.state == WorkstationState.Stopped) {
				ws = w;
				break;
			}
		}
		assertFalse(ws == null);
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_DOWN + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Down);
		assertTrue(post.events.containsString("Lowered"));
		post.events.clear();
		ws1.events.clear();
		trans.events.clear();
		popup.events.clear();
		p = new Part("x");
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		System.out.println("Here");	
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_UP + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedProcessing);
		assertTrue(popup.popupState == PopupState.Up);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws2.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		ws = null;
		for (MyWorkstation w : popup.workstations) {
			if (w.state == WorkstationState.Stopped) {
				ws = w;
				break;
			}
		}
		assertFalse(ws == null);
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_DOWN + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Down);
		assertFalse(post.events.containsString("Lowered"));
	}
	
	@Test
	public void testByPass() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		Object args[] = new Object[1];
		args[0] = 0;
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		assertTrue(popup.currentPart == null);
		Part p = new Part("y");
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.Leaving);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_RELEASE_GLASS + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("Released"));
		assertTrue(nextConv.events.containsString("Receive parts y"));
		assertTrue(post.events.containsString("Lowered"));
	}
	
	@Test
	public void testReleaseFail() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		Object args[] = new Object[1];
		args[0] = 0;
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		Part p = new Part("y");
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart.partState == PartState.Leaving);
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		popup.msgConveyorStopped();
		assertTrue(popup.conveyorState == PopupAgent.ConveyorState.Stopped);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.conveyorState == PopupAgent.ConveyorState.Stopped);
		assertTrue(popup.events.containsString("Blocked"));
	}

	@Test
	public void testPartsFinished() {
		Transducer trans = new Transducer();
		PopupAgent popup = new PopupAgent("popup", trans, 0);
		Object args[] = new Object[1];
		args[0] = 0;
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		assertTrue(popup.workstations.isEmpty());
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		assertFalse(popup.workstations.isEmpty());
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		Part p = new Part("x");
		popup.msgMovePopup(ws1);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		assertTrue(popup.workstations.get(0).needMove);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_UP + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Up);
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Available"));
		assertTrue(popup.events.containsString("I'm ready"));
		popup.msgSendGlassToPopup(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.isProcessed);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_DO_MOVE_DOWN + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(popup.events.containsString("Move finished"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, args);
		popup.pickAndExecuteAnAction();
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("Released"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.POPUP_RELEASE_GLASS + " on channel " + TChannel.POPUP + " to queue."));
		assertTrue(nextConv.events.containsString("Receive parts x"));
		assertTrue(post.events.containsString("Lowered"));
	}
}
