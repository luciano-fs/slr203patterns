package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import demo.MyActor.StopMe;

/**
 * @author Remi SHARROCK
 * @description
 */
public class StoppingActors {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
		// Instantiate first and second actor
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c");
	    
        MyMessage m1 = new MyMessage("Hello1");
        MyMessage m2 = new MyMessage("Hello2");
        MyMessage m3 = new MyMessage("Hello3");
        MyMessage m4 = new MyMessage("Hello4");

	    a.tell(m1, ActorRef.noSender());
	    a.tell(m2, ActorRef.noSender());
        a.tell(new StopMe(), ActorRef.noSender());
	    a.tell(m3, ActorRef.noSender());
	    a.tell(m4, ActorRef.noSender());
	    
	    b.tell(m1, ActorRef.noSender());
	    b.tell(m2, ActorRef.noSender());
	    b.tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
	    b.tell(m3, ActorRef.noSender());
	    b.tell(m4, ActorRef.noSender());

        try {
            c.tell(m1, ActorRef.noSender());
            c.tell(m2, ActorRef.noSender());
            c.tell(akka.actor.Kill.getInstance(), ActorRef.noSender());
            c.tell(m3, ActorRef.noSender());
            c.tell(m4, ActorRef.noSender());
        } catch (Exception e) {}
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
