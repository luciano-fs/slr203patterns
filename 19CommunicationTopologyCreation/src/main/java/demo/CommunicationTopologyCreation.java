package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.ShowMSG;
import java.util.ArrayList;

/**
 * @author Luciano Freitas
 * @description 
 */
public class CommunicationTopologyCreation {

	public static void main(String[] args) {
		// Instantiate an actor system
		final ActorSystem system = ActorSystem.create("system");
        final ShowMSG sm = new ShowMSG();
        final boolean[][] adjMatrix = new boolean[][]{
                                    {false, true, true, false},
                                    {false, false, false, true},
                                    {true, false, false, true},
                                    {true, false, false, true}
                                };

        final int nbActors = adjMatrix.length;
	    ActorRef[] actorArray = new ActorRef[nbActors];
        for (int i = 0; i < nbActors; ++i)
            actorArray[i] = system.actorOf(MyActor.createActor(), "actor" + i);
	    
        for (int i = 0; i < nbActors; ++i)
            for (int j = 0; j < adjMatrix[i].length; ++j)
                if (adjMatrix[i][j] == true)
                    actorArray[i].tell(actorArray[j], ActorRef.noSender());

        for (ActorRef ar : actorArray)
            ar.tell(sm, ActorRef.noSender());

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
