package engine.factory.testing;

import engine.factory.*;
import engine.factory.conveyorFamily.*;
import engine.factory.shared.Part;
import engine.factory.testing.mockAgent.MockConveyorFamily;
import engine.factory.testing.mockAgent.MockWorkstation;
import static org.junit.Assert.*;

import org.junit.Test;
import transducer.*;

public class ConveyorFamilyTest {

	@Test
	public void testOnePartProcess() throws InterruptedException{
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		MockConveyorFamily prevConv = new MockConveyorFamily();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", trans, 0);
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		entry.setPrevConv(prevConv);
		PopupAgent popup = new PopupAgent("Popup", trans, 0);
		popup.setNextConvFamily(nextConv);
		popup.setPostSensor(exit);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		conveyor.setPostSensor(exit);
		entry.setConveyor(conveyor);
		exit.setPopupAgent(popup);
		exit.setConveyor(conveyor);
		conveyor.startThread();
		entry.startThread();
		exit.startThread();
		popup.startThread();
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		Thread.sleep(5);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Lowered"));
		//System.out.println("main");
		//popup.msgPartsDone(new Part("x"));
		//popup.msgHereIsParts(new Part("x"));
		//Thread.sleep(1000);
		//popup.msgHereIsParts(new Part("x"));
		//System.out.println("Here");
		//entry.msgPressed();
	}
	@Test
	public void testOnePartByPass() throws InterruptedException{
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		MockConveyorFamily prevConv = new MockConveyorFamily();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", trans, 0);
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		entry.setPrevConv(prevConv);
		PopupAgent popup = new PopupAgent("Popup", trans, 0);
		popup.setNextConvFamily(nextConv);
		popup.setPostSensor(exit);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		conveyor.setPostSensor(exit);
		entry.setConveyor(conveyor);
		exit.setPopupAgent(popup);
		exit.setConveyor(conveyor);
		conveyor.startThread();
		entry.startThread();
		exit.startThread();
		popup.startThread();
		entry.msgHereIsParts(new Part("y"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part y"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED, args);
		Thread.sleep(5);
		nextConv.events.containsString("Receive parts y");
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Lowered"));
	}
	
	@Test
	public void testTwoPartsProcess() throws InterruptedException{
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		MockConveyorFamily prevConv = new MockConveyorFamily();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", trans, 0);
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		entry.setPrevConv(prevConv);
		PopupAgent popup = new PopupAgent("Popup", trans, 0);
		popup.setNextConvFamily(nextConv);
		popup.setPostSensor(exit);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		conveyor.setPostSensor(exit);
		entry.setConveyor(conveyor);
		exit.setPopupAgent(popup);
		exit.setConveyor(conveyor);
		conveyor.startThread();
		entry.startThread();
		exit.startThread();
		popup.startThread();
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(ws1.events.containsString("Not Available"));
		assertTrue(ws2.events.containsString("Not Available"));
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		Thread.sleep(5);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Lowered"));
		Thread.sleep(100);
		entry.events.clear();
		exit.events.clear();
		popup.events.clear();
		trans.events.clear();
		conveyor.events.clear();
		args[0] = 0;
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		Thread.sleep(5);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		Thread.sleep(5);
		//assertTrue(exit.events.containsString("Pop Lowered"));
	}
	@Test
	public void testThreePartsProcess() throws InterruptedException{
		Transducer trans = new Transducer();
		Object args[] = new Object[1];
		args[0] = 0;
		MockConveyorFamily prevConv = new MockConveyorFamily();
		MockConveyorFamily nextConv = new MockConveyorFamily();
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", trans, 0);
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry", trans, 0);
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit", trans, 1);
		entry.setPrevConv(prevConv);
		PopupAgent popup = new PopupAgent("Popup", trans, 0);
		popup.setNextConvFamily(nextConv);
		popup.setPostSensor(exit);
		MockWorkstation ws1 = new MockWorkstation();
		MockWorkstation ws2 = new MockWorkstation();
		popup.addWorkStation(ws1);
		popup.addWorkStation(ws2);
		conveyor.setPostSensor(exit);
		entry.setConveyor(conveyor);
		exit.setPopupAgent(popup);
		exit.setConveyor(conveyor);
		conveyor.startThread();
		entry.startThread();
		exit.startThread();
		popup.startThread();
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		Thread.sleep(5);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Lowered"));
		Thread.sleep(100);
		entry.events.clear();
		exit.events.clear();
		popup.events.clear();
		trans.events.clear();
		conveyor.events.clear();
		args[0] = 0;
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Released"));
		args[0] = 0;
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_LOAD_FINISHED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pop Raised"));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		Thread.sleep(5);
		assertTrue(popup.events.containsString("x is processing"));
		assertTrue(ws1.events.containsString("Receive parts x"));
		//assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.WORKSTATION_DO_LOAD_GLASS + " on channel " + TChannel.DRILL + " to queue."));
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_DOWN, args);
		Thread.sleep(5);
		Thread.sleep(100);
		entry.events.clear();
		exit.events.clear();
		popup.events.clear();
		trans.events.clear();
		conveyor.events.clear();
		args[0] = 0;
		entry.msgHereIsParts(new Part("x"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Pressed"));
		assertTrue(entry.events.containsString("Stop Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Stopping"));
		entry.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_RELEASED, args);
		Thread.sleep(5);
		assertTrue(entry.events.containsString("Sensor Released"));
		assertTrue(entry.events.containsString("Start Conveyor"));
		assertTrue(prevConv.events.containsString("Conveyor Working"));
		assertTrue(exit.events.containsString("Receive Part x"));
		args[0] = 1;
		exit.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		Thread.sleep(5);
		assertTrue(exit.events.containsString("Pressed"));
		Thread.sleep(5);
		assertTrue(conveyor.events.containsString("Stop Conveyor"));
		assertTrue(trans.events.containsString("Transducer: " + "Adding event " + TEvent.CONVEYOR_DO_STOP + " on channel " + TChannel.CONVEYOR + " to queue."));
		Thread.sleep(5);
	}
}
