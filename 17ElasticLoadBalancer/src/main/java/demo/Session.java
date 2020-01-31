package demo;

import akka.actor.Props;
import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.dispatch.*;
import akka.pattern.*;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Await;
import scala.concurrent.Promise;
import akka.util.*;
import java.time.Duration;
import demo.Balancer.*;

public class Session extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public Session() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Session.class, () -> {
			return new Session();
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof Request){
			Request rq = (Request) message;
            ActorRef emitter = getSender();
			log.info("["+getSelf().path().name()+"] received request from ["+ emitter.path().name() +"] with data: ["+rq.data+"]");
            Response rp = new Response ("Response to " + rq.data);
            emitter.tell(rp, getSelf());
		}

		if(message instanceof Response){
			Response rp = (Response) message;
            log.info("["+getSelf().path().name()+"] received response from ["+ getSender().path().name() +"] with data: ["+rp.data+"]");
		}
	}
    public void postStop() {
        log.info("["+getSelf().path().name()+"] has ended.");
    }
}
