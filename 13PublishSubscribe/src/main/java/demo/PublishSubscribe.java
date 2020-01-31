package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import demo.Topic.Subscribe;
import demo.Topic.Unsubscribe;

/**
 * @author Luciano Freitas
 * @description
 */

public class PublishSubscribe {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		
	    final ActorRef a = system.actorOf(MyActor.createActor(), "a");
	    final ActorRef b = system.actorOf(MyActor.createActor(), "b");
	    final ActorRef c = system.actorOf(MyActor.createActor(), "c");
	    final ActorRef publisher1 = system.actorOf(MyActor.createActor(), "publisher1");
	    final ActorRef publisher2 = system.actorOf(MyActor.createActor(), "publisher2");
	    final ActorRef topic1 = system.actorOf(Topic.createActor(), "topic1");
	    final ActorRef topic2 = system.actorOf(Topic.createActor(), "topic2");

        Subscribe s = new Subscribe();
        Unsubscribe us = new Unsubscribe();
        MyMessage m1 = new MyMessage("Hello");
        MyMessage m2 = new MyMessage("World");
        MyMessage m3 = new MyMessage("Hello2");
	    
        topic1.tell(s, a);
        topic1.tell(s, b);
        topic2.tell(s, b);
        topic2.tell(s, c);

        topic1.tell(m1, publisher1);
        topic2.tell(m2, publisher2);
	
        topic1.tell(us, a);
        topic1.tell(m3, publisher1);

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
