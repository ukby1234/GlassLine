package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;
import engine.factory.conveyorFamily.*;
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
		popup.msgConveyorReady();
		assertTrue(popup.events.containsString("Conv Start"));
		popup.msgConveyorStopped();
		assertTrue(popup.events.containsString("Conv Stop"));
	}
	
	@Test
	public void testProcessPart() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		popup.setPostSensor(post);
		Part p = new Part("x");
		popup.msgHereIsParts(p);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(popup.events.containsString("Move finished"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(popup.events.containsString("Move finished"));
	}
	
	@Test
	public void testByPass() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		Part p = new Part("y");
		popup.msgHereIsParts(p);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		popup.pickAndExecuteAnAction();	
		assertTrue(popup.events.containsString("Released"));
		assertTrue(nextConv.events.containsString("Receive parts y"));
		assertTrue(post.events.containsString("Lowered"));
	}
	
	@Test
	public void testReleaseFail() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		popup.setPostSensor(post);
		Part p = new Part("y");
		popup.msgHereIsParts(p);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		popup.msgConveyorStopped();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Blocked"));
	}

	@Test
	public void testPartsFinished() {
		PopupAgent popup = new PopupAgent("popup");
		MockPostSensor post = new MockPostSensor();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		popup.setPostSensor(post);
		popup.setNextConvFamily(nextConv);
		Part p = new Part("x");
		p.processed = true;
		popup.msgPartsDone();
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Up"));
		assertTrue(popup.events.containsString("Move finished"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("I'm ready"));
		popup.msgHereIsParts(p);
		popup.pickAndExecuteAnAction();
		assertTrue(post.events.containsString("Raised"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Loading popup"));
		assertTrue(popup.events.containsString("Loading finished"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Move Down"));
		assertTrue(popup.events.containsString("Move finished"));
		popup.pickAndExecuteAnAction();
		assertTrue(popup.events.containsString("Released"));
		assertTrue(nextConv.events.containsString("Receive parts x"));
		assertTrue(post.events.containsString("Lowered"));
	}
}
