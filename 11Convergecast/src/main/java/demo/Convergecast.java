package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import demo.MyMerger.JoinMessage;
import demo.MyMerger.UnjoinMessage;

/**
 * @author Luciano Freitas
 * @description
 */

public class Convergecast {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c");
	    final ActorRef d = system.actorOf(MyActor.createActor(), "d");
	    final ActorRef cc = system.actorOf(MyMerger.createActor(d), "cc");

        MyMessage m1 = new MyMessage("Hi1");
        MyMessage m2 = new MyMessage("Hi2");
        JoinMessage jm = new JoinMessage();
        UnjoinMessage um = new UnjoinMessage();
	    
        cc.tell(jm, a);
        cc.tell(jm, b);
        cc.tell(jm, c);

        cc.tell(m1, a);
        cc.tell(m1, b);
        cc.tell(m1, c);
	
        cc.tell(um, c);

        cc.tell(m2, a);
        cc.tell(m2, b);

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
