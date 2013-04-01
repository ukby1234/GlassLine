package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

import engine.factory.conveyorFamily.ConveyorEntrySensorAgent;
import engine.factory.conveyorFamily.ConveyorEntrySensorAgent.SensorState;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestPreSensor {
	
	@Test
	public void testHereIsPart() {
		//fail("Not yet implemented");
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		Part p = new Part("x");
		entry.msgHereIsParts(p);
		assertFalse(entry.parts.isEmpty());
	}
	
	@Test
	public void testPressing() {
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setPrevConv(prevConv);
		Part p = new Part("x");
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		assertFalse(entry.flag);
		entry.msgHereIsParts(p);
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(entry.flag);
	}
	
	@Test
	public void testReleased() {
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		MockConveyor conv = new MockConveyor();
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setConveyor(conv);
		entry.setPrevConv(prevConv);
		Part p = new Part("x");
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		entry.msgHereIsParts(p);
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(conv.events.containsString("Parts Receiving x"));
	}
	
	@Test
	public void testInforPrevConv() {
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		MockConveyor conv = new MockConveyor();
		MockConveyorFamily prevConv = new MockConveyorFamily();
		entry.setConveyor(conv);
		entry.setPrevConv(prevConv);
		assertTrue(entry.sensorState == SensorState.Nothing);
		assertTrue(entry.parts.isEmpty());
		Part p = new Part("x");
		entry.msgHereIsParts(p);
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		assertFalse(entry.parts.isEmpty());
		assertTrue(entry.sensorState == SensorState.Pressed);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		assertTrue(entry.sensorState == SensorState.Released);
		assertTrue(entry.pickAndExecuteAnAction());
		assertTrue(prevConv.events.containsString("Conveyor Working"));
	}

}
