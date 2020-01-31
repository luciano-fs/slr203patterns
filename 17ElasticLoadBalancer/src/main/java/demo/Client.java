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

public class Client extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public Client() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Client.class, () -> {
			return new Client();
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof Response){
			Response rp = (Response) message;
            log.info("["+getSelf().path().name()+"] received response from ["+ getSender().path().name() +"] with data: ["+rp.data+"]");
		}
	}
}
