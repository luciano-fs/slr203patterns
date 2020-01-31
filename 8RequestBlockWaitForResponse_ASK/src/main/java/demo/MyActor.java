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

    static public class GoAsk {
        Request rq;
        ActorRef dest;
        public GoAsk(Request rq, ActorRef dest) {
            this.rq = rq;
            this.dest = dest;
        }
    }

	static public class Request {
        public final ActorRef emitter;
		public final String data;
		public Request(ActorRef emitter, String data) {
            this.emitter = emitter;
			this.data = data;
		}
    }

	static public class Response {
		public final String data;
		public Response(String data) {
			this.data = data;
		}
    }

    private void goAsk(ActorRef ar, Request rq) {
        Timeout timeout = Timeout.create(Duration.ofSeconds(5));
        Future<Object> future = Patterns.ask(ar, rq, timeout);
        try {
        Response result = (Response) Await.result(future, timeout.duration());
        log.info("["+getSelf().path().name()+"] received response from ["+ ar.path().name() +"] with data: ["+result.data+"]");
        } catch (Exception e) {};
        return;
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof Request){
			Request rq = (Request) message;
			log.info("["+getSelf().path().name()+"] received request from ["+ rq.emitter.path().name() +"] with data: ["+rq.data+"]");
            Response rp = new Response ("Response to " + rq.data);
            getSender().tell(rp, getSelf());
		}

		if(message instanceof Response){
			Response rp = (Response) message;
            log.info("["+getSelf().path().name()+"] received response from ["+ getSender().path().name() +"] with data: ["+rp.data+"]");
		}
		if(message instanceof GoAsk){
			GoAsk ga = (GoAsk) message;
            goAsk(ga.dest, ga.rq);
		}
	}

}
