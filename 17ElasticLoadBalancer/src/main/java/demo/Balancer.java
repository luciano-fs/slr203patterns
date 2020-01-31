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

public class Balancer extends UntypedAbstractActor{
	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorSystem system;
    private int maxSession;
    private ArrayList<SessionNbTask> sessionList;
    private int nextDest;
    private int activeSessions;
    private int nextId;

	public Balancer(ActorSystem system) {
        sessionList = new ArrayList<SessionNbTask>();
        nextDest = 0;
        maxSession = 0;
        nextId = 0;
        this.system = system;
    }

	// Static function creating actor
	public static Props createActor(ActorSystem as) {
		return Props.create(Balancer.class, () -> {
			return new Balancer(as);
		});
	}

    public static class MaxSessionMSG {
        int max;
        public MaxSessionMSG(int max) {
            this.max = max;
        }
    }

    static private class SessionNbTask {
        ActorRef session;
        int nbTask;
        public SessionNbTask(ActorRef session) {
            this.session = session;
            nbTask = 1;
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
		if(message instanceof Response) {
            ArrayList<SessionNbTask> sl = new ArrayList<>(sessionList);
			Response rp = (Response) message;
            ActorRef msgSender = getSender();
			log.info("["+getSelf().path().name()+"] received response from ["+
                    getSender().path().name() +"] saying [" +
                    rp.data +"]");
            for (SessionNbTask snt : sl) {
                if (snt.session == msgSender)
                    if(--snt.nbTask == 0) {
                        snt.session.tell(akka.actor.PoisonPill.getInstance(), getSelf());
                        sessionList.remove(snt);
                    }
            }
		}
        else if(message instanceof MyMessage) {
			MyMessage m = (MyMessage) message;
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to dsitriute [" +
                    m.data +"]");
            if(activeSessions < maxSession) {
                ++activeSessions;
                ActorRef s = system.actorOf(Session.createActor(), "session" + Integer.toString(nextId++));
                log.info("["+getSelf().path().name()+"] created session [" + s.path().name() + "]");
                s.tell(new Request(m.data), getSelf());
                sessionList.add(new SessionNbTask(s));
            }
            else {
                SessionNbTask snt = sessionList.get(nextDest);
                ++snt.nbTask;
                ++nextDest;
                if (nextDest > activeSessions)
                    nextDest = 0;
                snt.session.tell(new Request(m.data), getSelf());
            }
        }
        else if(message instanceof MaxSessionMSG) {
            MaxSessionMSG msm = (MaxSessionMSG) message;
            maxSession = msm.max;
			log.info("["+getSelf().path().name()+"] set its maximum number of sessions to ["+
                    maxSession +"]");
        }
	}
}
