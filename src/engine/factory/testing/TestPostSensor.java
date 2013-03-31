package engine.factory.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.ConveyorExitSensorAgent.ConveyorState;
import engine.factory.conveyorFamily.ConveyorExitSensorAgent.PopupState;
import engine.factory.conveyorFamily.ConveyorExitSensorAgent.SensorState;
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
		assertTrue(exit.sensorState == SensorState.Nothing);
		exit.msgPressed();
		assertTrue(exit.sensorState == SensorState.Pressed);
		assertTrue(exit.events.containsString("Pressed"));
	}
	
	@Test
	public void testStopConv() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockConveyor conv = new MockConveyor();
		exit.setConveyor(conv);
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		exit.msgPressed();
		exit.msgPopupRaise();
		assertTrue(exit.sensorState == SensorState.Pressed);
		assertTrue(exit.popupState == PopupState.Up);
		exit.events.containsString("Pop Raised");
		exit.pickAndExecuteAnAction();
		assertTrue(exit.conveyorState == ConveyorState.Stopped);
		assertTrue(conv.events.containsString("Stop"));
	}
	
	@Test
	public void testStartConv() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockConveyor conv = new MockConveyor();
		exit.setConveyor(conv);
		exit.conveyorState = ConveyorState.Stopped;
		exit.msgPopupLow();
		exit.events.containsString("Pop Lowered");
		exit.pickAndExecuteAnAction();
		assertTrue(exit.conveyorState == ConveyorState.Running);
		assertTrue(conv.events.containsString("Start"));
	}
	
	@Test
	public void testPassToPopup() {
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		MockPopup popup = new MockPopup();
		exit.setPopupAgent(popup);
		Part p = new Part("x");
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		assertTrue(exit.parts.isEmpty());
		exit.msgHereIsParts(p);
		exit.msgPressed();
		assertTrue(exit.sensorState == SensorState.Pressed);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		assertFalse(exit.parts.isEmpty());
		assertTrue(exit.events.containsString("Pressed"));
		exit.msgReleased();
		assertTrue(exit.sensorState == SensorState.Released);
		assertTrue(exit.events.containsString("Released"));
		exit.pickAndExecuteAnAction();
		assertTrue(exit.parts.isEmpty());
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.events.containsString("Give parts to popup"));
		assertTrue(popup.events.containsString("Receive part x"));
	}
}
