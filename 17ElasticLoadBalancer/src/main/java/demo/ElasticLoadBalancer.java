package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.Client.*;
import demo.Balancer.*;

/**
 * @author Luciano Freitas
 * @description
 */

public class ElasticLoadBalancer {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
	    final ActorRef client = system.actorOf(Client.createActor(), "client");
        final ActorRef lb = system.actorOf(Balancer.createActor(system), "balancer");

        MaxSessionMSG msm = new MaxSessionMSG(2);
        MyMessage m1 = new MyMessage("Hello1");
        MyMessage m2 = new MyMessage("Hello2");
        MyMessage m3 = new MyMessage("Hello3");

        lb.tell(msm, ActorRef.noSender());
        lb.tell(m1, client);
        lb.tell(m2, client);
        lb.tell(m3, client);
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
