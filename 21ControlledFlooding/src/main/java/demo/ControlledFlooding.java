package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.MyMessage;
import java.util.ArrayList;

/**
 * @author Luciano Freitas
 * @description 
 */
public class ControlledFlooding {

	public static void main(String[] args) {
		// Instantiate an actor system
		final ActorSystem system = ActorSystem.create("system");

        //First topology
        final MyMessage m0 = new MyMessage("Hello0");

        final boolean[][] adjMatrix0 = new boolean[][]{
                                    {false, true, true, false, false},
                                    {false, false, false, true, false},
                                    {false, false, false, true, false},
                                    {false, false, false, false, true},
                                    {false, false, false, false, false}
                                };

        final int nbActors0 = adjMatrix0.length;
	    ActorRef[] actorArray0 = new ActorRef[nbActors0];
        for (int i = 0; i < nbActors0; ++i)
            actorArray0[i] = system.actorOf(MyActor.createActor(), "actor0" + i);
	    
        for (int i = 0; i < nbActors0; ++i)
            for (int j = 0; j < adjMatrix0[i].length; ++j)
                if (adjMatrix0[i][j] == true)
                    actorArray0[i].tell(actorArray0[j], ActorRef.noSender());

        //Second topology
        final MyMessage m1 = new MyMessage("Hello1");

        final boolean[][] adjMatrix1 = new boolean[][]{
                                    {false, true, true, false, false},
                                    {false, false, false, true, false},
                                    {false, false, false, true, false},
                                    {false, false, false, false, true},
                                    {false, true, false, false, false}
                                };

        final int nbActors1 = adjMatrix1.length;
	    ActorRef[] actorArray1 = new ActorRef[nbActors1];
        for (int i = 0; i < nbActors1; ++i)
            actorArray1[i] = system.actorOf(MyActor.createActor(), "actor1" + i);
	    
        for (int i = 0; i < nbActors1; ++i)
            for (int j = 0; j < adjMatrix1[i].length; ++j)
                if (adjMatrix1[i][j] == true)
                    actorArray1[i].tell(actorArray1[j], ActorRef.noSender());

        actorArray0[0].tell(m0, ActorRef.noSender());
        actorArray1[0].tell(m1, ActorRef.noSender());

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
