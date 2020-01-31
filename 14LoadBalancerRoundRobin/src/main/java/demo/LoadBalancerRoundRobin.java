package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import demo.Balancer.Join;
import demo.Balancer.Unjoin;

/**
 * @author Luciano Freitas
 * @description
 */

public class LoadBalancerRoundRobin {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c");
	    final ActorRef lb = system.actorOf(Balancer.createActor(), "lb");

        Join j = new Join();
        Unjoin uj = new Unjoin();
        MyMessage m1 = new MyMessage("W");
        MyMessage m2 = new MyMessage("X");
        MyMessage m3 = new MyMessage("Y");
        MyMessage m4 = new MyMessage("Z");
	    
        lb.tell(j, b);
        lb.tell(j, c);

        lb.tell(m1, a);
        lb.tell(m2, a);
        lb.tell(m3, a);
	
        lb.tell(uj, c);
        lb.tell(m4, a);

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
