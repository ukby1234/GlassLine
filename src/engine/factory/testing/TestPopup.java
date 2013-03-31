package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;
import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.PopupAgent.ConveyorState;
import engine.factory.conveyorFamily.PopupAgent.MyWorkstation;
import engine.factory.conveyorFamily.PopupAgent.PartState;
import engine.factory.conveyorFamily.PopupAgent.PopupState;
import engine.factory.conveyorFamily.PopupAgent.WorkstationState;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPopup {

	@Test
	public void testReceiveParts() {
		PopupAgent popup = new PopupAgent("popup");
		Part p = new Part("x");
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertTrue(popup.currentPart != null);
		assertTrue(popup.events.containsString("Parts Received"));
	}
	
	@Test
	public void testStartStopConv() {
		PopupAgent popup = new PopupAgent("popup");
		popup.conveyorState = ConveyorState.Stopped;
		popup.msgConveyorReady();
		assertTrue(popup.conveyorState == ConveyorState.Running);
		assertTrue(popup.events.containsString("Conv Start"));
		popup.msgConveyorStopped();
		assertTrue(popup.conveyorState == ConveyorState.Stopped);
		assertTrue(popup.events.containsString("Conv Stop"));
	}
	
	@Test
	public void testProcessPart() {
		PopupAgent popup = new PopupAgent("popup");
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
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.msgLoadingFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		System.out.println("Here");	
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.msgMoveFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedProcessing);
		assertTrue(popup.popupState == PopupState.Up);
		popup.msgMoveFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("x is processing"));
		MyWorkstation ws = null;
		for (MyWorkstation w : popup.workstations) {
			if (w.state == WorkstationState.Stopped) {
				ws = w;
				break;
			}
		}
		assertFalse(ws == null);
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Down);
	}
	
	@Test
	public void testByPass() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		assertTrue(popup.currentPart == null);
		Part p = new Part("y");
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.msgLoadingFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.Leaving);
		popup.msgPopupReleased();
		popup.pickAndExecuteAnAction();	
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("Released"));
		assertTrue(nextConv.events.containsString("Receive parts y"));
		assertTrue(post.events.containsString("Lowered"));
	}
	
	@Test
	public void testReleaseFail() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		Part p = new Part("y");
		assertTrue(popup.currentPart == null);
		popup.msgHereIsParts(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.msgLoadingFinished();
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
		PopupAgent popup = new PopupAgent("popup");
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
		Part p = new Part("x");
		popup.msgMovePopup(ws1);
		popup.msgMoveFinished();
		assertTrue(popup.workstations.get(0).needMove);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(popup.events.containsString("Move finished"));
		assertTrue(popup.popupState == PopupState.Up);
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("I'm ready"));
		popup.msgSendGlassToPopup(p);
		assertFalse(popup.currentPart == null);
		assertTrue(popup.currentPart.isProcessed);
		assertTrue(popup.currentPart.partState == PartState.PartsComing);
		popup.msgLoadingFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		assertTrue(popup.currentPart.partState == PartState.NeedMoving);
		popup.msgMoveFinished();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(popup.events.containsString("Move finished"));
		popup.msgPopupReleased();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.currentPart == null);
		assertTrue(popup.events.containsString("Released"));
		assertTrue(nextConv.events.containsString("Receive parts x"));
		assertTrue(post.events.containsString("Lowered"));
	}
}
