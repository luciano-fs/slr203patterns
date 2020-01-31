package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.Request;
import demo.MyActor.Request;
import demo.MyActor.GoAsk;

/**
 * @author Luciano Freitas
 * @description
 */

public class RequestBlockWaitForResponse {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b"); 
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c"); 

        Request rq1 = new Request(a,"First Request");
        Request rq2 = new Request(a,"Second Request");

        GoAsk ga1 = new GoAsk(rq1, b);
        GoAsk ga2 = new GoAsk(rq2, b);

        a.tell(ga1, ActorRef.noSender());
        a.tell(ga2, ActorRef.noSender());
	    
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
