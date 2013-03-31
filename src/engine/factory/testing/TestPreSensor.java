package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;

import engine.factory.conveyorFamily.ConveyorEntrySensorAgent;
import engine.factory.conveyorFamily.ConveyorEntrySensorAgent.SensorState;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPreSensor {
	
	@Test
	public void testHereIsPart() {
		//fail("Not yet implemented");
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry");
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		Part p = new Part("x");
		entry.msgHereIsParts(p);
		assertFalse(entry.parts.isEmpty());
	}
	
	@Test
	public void testPressing() {
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry");
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setPrevConv(prevConv);
		Part p = new Part("x");
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		entry.msgHereIsParts(p);
		entry.msgPressed();
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
	}
	
	@Test
	public void testReleased() {
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry");
		MockConveyor conv = new MockConveyor();
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setConveyor(conv);
		entry.setPrevConv(prevConv);
		Part p = new Part("x");
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		entry.msgHereIsParts(p);
		entry.msgPressed();
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		entry.msgReleased();
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(conv.events.containsString("Parts Receiving x"));
	}
	
	@Test
	public void testInforPrevConv() {
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry");
		MockConveyor conv = new MockConveyor();
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setConveyor(conv);
		entry.setPrevConv(prevConv);
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		Part p = new Part("x");
		entry.msgHereIsParts(p);
		entry.msgPressed();
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.msgReleased();
		assertTrue(entry.sensorState == SensorState.Released);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(prevConv.events.containsString("Conveyor Working"));
	}

}
