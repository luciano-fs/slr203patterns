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
import demo.ActorCreator.*;
import akka.actor.ActorIdentity;

public class MyActor extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public MyActor() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyActor.class, () -> {
			return new MyActor();
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof String){
			String s = (String) message;
            log.info("["+getSelf().path().name()+"] received ["+s+"]");
		}
        if (message instanceof ActorIdentity) {
            log.info("["+getSender().path()+"] presents himself");
        }
	}
}
