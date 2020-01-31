package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;

/**
 * @author Luciano Freitas
 * @description
 */

public class BroadcastRoundRobin {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c");
	    final ActorRef bc = system.actorOf(MyBroadcaster.createActor(), "bc");

        MyMessage m = new MyMessage ("Hi");
	    
	    bc.tell(b, b);
	    bc.tell(c, c);

        bc.tell(m, a);
	
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
