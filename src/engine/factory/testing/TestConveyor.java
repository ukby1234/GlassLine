package engine.factory.testing;

import static org.junit.Assert.*;
import org.junit.Test;

import transducer.*;
import engine.factory.conveyorFamily.*;
import engine.factory.conveyorFamily.ConveyorAgent.ConveyorState;
import engine.factory.interfaces.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.*;

public class TestConveyor {

	@Test
	public void testStartConv() {
		Transducer trans = new Transducer();
		ConveyorAgent conv = new ConveyorAgent("Conv", trans, 0);
		conv.conveyorState = ConveyorState.Stopped;
		assertTrue(conv.conveyorState == ConveyorState.Stopped);
		conv.msgStartConveyor();
		assertTrue(conv.conveyorState == ConveyorState.Running);
		assertTrue(conv.changed);
		conv.pickAndExecuteAnAction();
		assertFalse(conv.changed);
		assertTrue(conv.events.containsString("Start Conveyor"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.CONVEYOR_DO_START + " on channel " + TChannel.CONVEYOR + " to queue."));
	}
	
	@Test
	public void testStopConv() {
		Transducer trans = new Transducer();
		ConveyorAgent conv = new ConveyorAgent("Conv", trans, 0);
		assertTrue(conv.conveyorState == ConveyorState.Running);
		conv.msgStopConveyor();
		assertTrue(conv.conveyorState == ConveyorState.Stopped);
		assertTrue(conv.changed);
		conv.pickAndExecuteAnAction();
		assertFalse(conv.changed);
		assertTrue(conv.events.containsString("Stop Conveyor"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.CONVEYOR_DO_STOP + " on channel " + TChannel.CONVEYOR + " to queue."));
	}
	
	@Test
	public void testSendPart() {
		ConveyorAgent conv = new ConveyorAgent("Conv", 0);
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
