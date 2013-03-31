package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;
import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.ConveyorAgent.ConveyorState;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestConveyor {

	@Test
	public void testStartConv() {
		ConveyorAgent conv = new ConveyorAgent("Conv");
		conv.conveyorState = ConveyorState.Stopped;
		assertTrue(conv.conveyorState == ConveyorState.Stopped);
		conv.msgStartConveyor();
		assertTrue(conv.conveyorState == ConveyorState.Running);
		assertTrue(conv.changed);
		conv.pickAndExecuteAnAction();
		assertFalse(conv.changed);
		assertTrue(conv.events.containsString("Start Conveyor"));
	}
	
	@Test
	public void testStopConv() {
		ConveyorAgent conv = new ConveyorAgent("Conv");
		assertTrue(conv.conveyorState == ConveyorState.Running);
		conv.msgStopConveyor();
		assertTrue(conv.conveyorState == ConveyorState.Stopped);
		assertTrue(conv.changed);
		conv.pickAndExecuteAnAction();
		assertFalse(conv.changed);
		assertTrue(conv.events.containsString("Stop Conveyor"));
	}
	
	@Test
	public void testSendPart() {
		ConveyorAgent conv = new ConveyorAgent("Conv");
		MockPostSensor post = new MockPostSensor();
		conv.setPostSensor(post);
		Part p = new Part("x");
		assertTrue(conv.conveyorState == ConveyorState.Running);
		assertTrue(conv.parts.isEmpty());
		conv.msgHereIsParts(p);
		assertFalse(conv.parts.isEmpty());
		conv.pickAndExecuteAnAction();
		assertTrue(conv.conveyorState == ConveyorState.Running);
		assertTrue(conv.events.containsString("Give parts to post sensor"));
		assertTrue(post.events.containsString("Receive part x"));
	}

}
