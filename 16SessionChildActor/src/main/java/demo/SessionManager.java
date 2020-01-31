package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.Client;
import demo.Session;

public class SessionManager extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSystem system;
    private ArrayList<sesClientPair> list;

	public SessionManager(ActorSystem system) {
        list = new ArrayList<sesClientPair>();
        this.system = system;
    }

	// Static function creating actor
	public static Props createActor(ActorSystem as) {
		return Props.create(SessionManager.class, () -> {
			return new SessionManager(as);
		});
	}

    public static class createSession {
        ActorRef emitter;
        public createSession(ActorRef emitter) {
            this.emitter = emitter;
        }
    }

    public static class endSession {
        public endSession() {}
    }

    public static class sesClientPair {
        ActorRef c;
        ActorRef s;
        public sesClientPair(ActorRef c, ActorRef s) {
            this.c = c;
            this.s = s;
        }
    }

	static public class MyMessage {
		public final String data;
		public MyMessage(String data) {
			this.data = data;
		}
    }

	static public class Request extends MyMessage {
		public Request (String data) {
            super (data);
		}
    }

	static public class Response extends MyMessage {
		public Response (String data) {
            super (data);
		}
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof createSession){
            createSession cs = (createSession) message;
			ActorRef c = cs.emitter;
            ActorRef s = system.actorOf(Session.createActor(), "session1");
            sesClientPair scp = new sesClientPair (c, s);
			log.info("["+getSelf().path().name()+"] created a session for [" + 
                    c.path().name() + "].");
            getSender().tell(s, getSelf());
		}
		if(message instanceof endSession){
			ActorRef c = getSender();
            for (sesClientPair scp : list) {
                if (scp.c == c) {
                    scp.s.tell(akka.actor.PoisonPill.getInstance(), getSelf());
                    break;
                }
            }
        }
    }
}
