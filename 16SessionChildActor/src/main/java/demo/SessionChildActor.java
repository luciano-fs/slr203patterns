package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.SessionManager.*;
import demo.Client.*;

/**
 * @author Luciano Freitas
 * @description
 */

public class SessionChildActor {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
	    final ActorRef client1 = system.actorOf(Client.createActor(), "client1");
        final ActorRef sesMan = system.actorOf(SessionManager.createActor(system), "SessionManager");

        endSession es = new endSession();
        Request m1 = new Request("foo");
        Response m2 = new Response("bar");
        GetSession gs = new GetSession(sesMan);
        GoTell gt1 = new GoTell(m1);
        GoTell gt2 = new GoTell(m2);

        client1.tell(gs, ActorRef.noSender());
        client1.tell(gt1, ActorRef.noSender());
        client1.tell(gt2, ActorRef.noSender());
        sesMan.tell(es, client1);

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
