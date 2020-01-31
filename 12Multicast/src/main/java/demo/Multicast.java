package demo;

import java.util.ArrayList;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import demo.Transmitter.GroupMessage;
import demo.Transmitter.GroupDeclaration;

/**
 * @author Luciano Freitas
 * @description
 */

public class Multicast {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef sender = system.actorOf(MyActor.createActor(), "sender");
	    final ActorRef receiver1 = system.actorOf(MyActor.createActor(), "receiver1");
	    final ActorRef receiver2 = system.actorOf(MyActor.createActor(), "receiver2");
	    final ActorRef receiver3 = system.actorOf(MyActor.createActor(), "receiver3");
	    final ActorRef multicaster = system.actorOf(Transmitter.createActor(), "multicaster");
        ArrayList<ActorRef> g1 = new ArrayList<ActorRef>();
        ArrayList<ActorRef> g2 = new ArrayList<ActorRef>();

        g1.add(receiver1);
        g1.add(receiver2);
        g2.add(receiver2);
        g2.add(receiver3);

        MyMessage m1 = new MyMessage("Hello");
        MyMessage m2 = new MyMessage("World");

        GroupDeclaration gd1 = new GroupDeclaration("Group 1", g1);
        GroupDeclaration gd2 = new GroupDeclaration("Group 2", g2);
        //Group Message uses group name as identifier
        //It wouldn't make sense to refer to the whole group, as it would be useless
        //for the transmitter to keep them saved
        GroupMessage gm1 = new GroupMessage("Group 1", m1);
        GroupMessage gm2 = new GroupMessage("Group 2", m2);

        multicaster.tell(gd1, sender);
        multicaster.tell(gd2, sender);
        multicaster.tell(gm1, sender);
        multicaster.tell(gm2, sender);

	    // We wait 5 seconds before ending system (by default)
	    // But this is not the best solution.
	    try {
			waitBeforeTerminate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			system.terminate();
		}
	}

	public static void waitBeforeTerminate() throws InterruptedException {
		Thread.sleep(5000);
	}
}
