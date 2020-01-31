package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.Request;

/**
 * @author Luciano Freitas
 * @description
 */

public class RequestDontWaitForResponse {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    
        Request rq1 = new Request("First Request");
        Request rq2 = new Request("Second Request");

	    b.tell(rq1, a);
	    b.tell(rq2, a);
	    
	
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
