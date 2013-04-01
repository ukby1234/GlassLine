package engine.factory.testing;

import static org.junit.Assert.*;

import org.junit.Test;

import transducer.*;

import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.ConveyorExitSensorAgent.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPostSensor {

	@Test
	public void testReceivePart() {
		//fail("Not yet implemented");
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 1;
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		Part p = new Part("x");
		assertTrue(exit.parts.isEmpty());
		exit.msgHereIsParts(p);
		assertFalse(exit.parts.isEmpty());
	}

	@Test
	public void testPressed() {
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 1;
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		assertTrue(exit.sensorState == SensorState.Nothing);
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		assertTrue(exit.sensorState == SensorState.Pressed);
		assertTrue(exit.events.containsString("Pressed"));
	}
	
	@Test
	public void testStopConv() {
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 1;
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		MockConveyor conv = new MockConveyor();
		exit.setConveyor(conv);
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
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
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 1;
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
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
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 1;
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		MockPopup popup = new MockPopup();
		exit.setPopupAgent(popup);
		Part p = new Part("x");
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		assertTrue(exit.parts.isEmpty());
		exit.msgHereIsParts(p);
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		assertTrue(exit.sensorState == SensorState.Pressed);
		assertTrue(exit.popupState == PopupState.Down);
		assertTrue(exit.conveyorState == ConveyorState.Running);
		assertFalse(exit.parts.isEmpty());
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		assertTrue(exit.sensorState == SensorState.Released);
		assertTrue(exit.events.containsString("Released"));
		exit.pickAndExecuteAnAction();
		assertTrue(exit.parts.isEmpty());
		assertTrue(exit.sensorState == SensorState.Nothing);
		assertTrue(exit.events.containsString("Give parts to popup"));
		assertTrue(popup.events.containsString("Receive part x"));
	}
}
