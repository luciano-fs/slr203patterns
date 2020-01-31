package demo;

import akka.actor.ActorRef;
import akka.actor.ActorIdentity;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.actor.ActorSystem;
import demo.ActorCreator.*;
import demo.MyActor.*;

/**
 * @author Luciano Freitas
 * @description
 */

public class SearchActorsWithNameOrPath {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
        final String identifyId = "1";
        final ActorRef ac = system.actorOf(ActorCreator.createActor(system), "a");
        final ActorRef receiver = system.actorOf(MyActor.createActor(), "receiver");
        final CreateActor ca = new CreateActor();

        ac.tell(ca, ActorRef.noSender());
        ac.tell(ca, ActorRef.noSender());

        ActorSelection a0 = system.actorSelection("/user/actor0");
        ActorSelection a1 = system.actorSelection("/user/actor1");

        a0.tell("OK", ActorRef.noSender());
        a1.tell("OK", ActorRef.noSender());

        ActorSelection everyone = system.actorSelection("/*");
        everyone.tell(new Identify(identifyId), receiver);

        ActorSelection user = system.actorSelection("/user/*");
        user.tell(new Identify(identifyId), receiver);

        ActorSelection systemSel = system.actorSelection("/system");
        systemSel.tell(new Identify(identifyId), receiver);

        ActorSelection dead = system.actorSelection("/deadLetters");
        dead.tell(new Identify(identifyId), receiver);

        ActorSelection temp = system.actorSelection("/temp/*");
        temp.tell(new Identify(identifyId), receiver);

        ActorSelection remote = system.actorSelection("/remote/*");
        remote.tell(new Identify(identifyId), receiver);
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
