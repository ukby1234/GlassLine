package engine.factory.conveyorFamily;

import engine.factory.ConveyorFamilyAgent;
import engine.factory.shared.Part;

public class ConveyorFamilyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException{
		ConveyorFamilyAgent prev = new ConveyorFamilyAgent();
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor");
		ConveyorEntrySensorAgent entry = new ConveyorEntrySensorAgent("Entry");
		ConveyorExitSensorAgent exit = new ConveyorExitSensorAgent("Exit");
		entry.setPrevConv(prev);
		PopupAgent popup = new PopupAgent("Popup");
		popup.setNextConvFamily(prev);
		popup.setPostSensor(exit);
		conveyor.setPostSensor(exit);
		entry.setConveyor(conveyor);
		entry.startThread();
		exit.setPopupAgent(popup);
		exit.setConveyor(conveyor);
		conveyor.startThread();
		exit.startThread();
		entry.msgHereIsParts(new Part("x"));
		entry.msgPressed();
		//popup.msgLoadingFinished();
		popup.startThread();
		Thread.sleep(1000);
		//System.out.println("main");
		//popup.msgPartsDone(new Part("x"));
		popup.msgHereIsParts(new Part("x"));
		Thread.sleep(1000);
		popup.msgHereIsParts(new Part("x"));
		System.out.println("Here");
		//entry.msgPressed();
	}

}
