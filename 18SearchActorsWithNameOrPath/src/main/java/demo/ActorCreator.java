package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor;

public class ActorCreator extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSystem system;
    private int nbActors;

	public ActorCreator(ActorSystem system) {
        this.system = system;
        nbActors = 0;
    }

	// Static function creating actor
	public static Props createActor(ActorSystem as) {
		return Props.create(ActorCreator.class, () -> {
			return new ActorCreator(as);
		});
	}

    public static class CreateActor {
        public CreateActor() { }
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof CreateActor){
            ActorRef a = system.actorOf(MyActor.createActor(), "actor" + nbActors);
            nbActors++;
			log.info("["+getSelf().path().name()+"] created actor [" + 
                    a.path().name() + "].");
		}
    }
}
