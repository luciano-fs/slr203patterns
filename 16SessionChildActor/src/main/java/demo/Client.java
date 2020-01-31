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
import demo.SessionManager.*;

public class Client extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef s;

	public Client() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Client.class, () -> {
			return new Client();
		});
	}

    static public class GetSession {
        ActorRef dest;
        public GetSession(ActorRef dest) {
            this.dest = dest;
        }
    }

    static public class GoTell {
        MyMessage m;
        public GoTell(MyMessage m) {
            this.m = m;
        }
    }

    private void getSession(ActorRef ar) {
        Timeout timeout = Timeout.create(Duration.ofSeconds(5));
        Future<Object> future = Patterns.ask(ar, new createSession(getSelf()), timeout);
        try {
        s = (ActorRef) Await.result(future, timeout.duration());
        log.info("["+getSelf().path().name()+"] received a session from ["+ ar.path().name() +"] with name: ["+ s.path().name()+"]");
        } catch (Exception e) {};
        return;
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
		if(message instanceof GetSession){
			GetSession gs = (GetSession) message;
            getSession(gs.dest);
		}
		if(message instanceof GoTell){
			GoTell gt = (GoTell) message;
            s.tell(gt.m, getSelf());
            log.info("["+getSelf().path().name()+"] should tell ["+ gt.m.data +"] to [" + s.path().name() + "]");
		}
	}
}
