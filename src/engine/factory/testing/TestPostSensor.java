package engine.factory.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import engine.factory.conveyorFamily.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPostSensor {

	@Test
	public void testReceivePart() {
		//fail("Not yet implemented");
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		Part p = new Part("x");
		assertTrue(exit.parts.isEmpty());
		exit.msgHereIsParts(p);
		assertFalse(exit.parts.isEmpty());
	}

	@Test
	public void testPressed() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		exit.msgPressed();
		assertTrue(exit.events.containsString("Pressed"));
	}
	
	@Test
	public void testStopConv() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockConveyor conv = new MockConveyor();
		exit.setConveyor(conv);
		exit.msgPressed();
		exit.msgPopupRaise();
		exit.events.containsString("Pop Raised");
		exit.pickAndExecuteAnAction();
		assertTrue(conv.events.containsString("Stop"));
	}
	
	@Test
	public void testStartConv() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockConveyor conv = new MockConveyor();
		exit.setConveyor(conv);
		exit.msgPressed();
		exit.msgPopupRaise();
		exit.events.containsString("Pop Raised");
		exit.pickAndExecuteAnAction();
		assertTrue(conv.events.containsString("Stop"));
		exit.msgPopupLow();
		exit.events.containsString("Pop Lowered");
		exit.pickAndExecuteAnAction();
		assertTrue(conv.events.containsString("Start"));
	}
	
	@Test
	public void testPassToConv() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockPopup popup = new MockPopup();
		exit.setPopupAgent(popup);
		Part p = new Part("x");
		exit.msgHereIsParts(p);
		exit.pickAndExecuteAnAction();
		assertTrue(exit.events.containsString("Released"));
		exit.pickAndExecuteAnAction();
		assertTrue(exit.events.containsString("Give parts to popup"));
		assertTrue(popup.events.containsString("Receive part x"));
	}
}
