package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import demo.MyActor.MyMessage;

public class Balancer extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ArrayList<ActorRef> distList;
    private int nextDest;

	public Balancer() {
        distList = new ArrayList<ActorRef>();
        nextDest = 0;
    }

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Balancer.class, () -> {
			return new Balancer();
		});
	}

    public static class Join {
        public Join() {}
    }

    public static class Unjoin {
        public Unjoin() {}
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof MyMessage){
			MyMessage m = (MyMessage) message;
            ActorRef msgSender = getSender();
            ArrayList<ActorRef> dest = new ArrayList<ActorRef>(distList);
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to dsitriute [" +
                    m.data +"]");
            distList.get(nextDest).tell(m, msgSender);
            ++nextDest;
            if (nextDest >= distList.size())
                nextDest = 0;
		}
		if(message instanceof Join){
			ActorRef ar = getSender();
            distList.add(ar);
			log.info("["+getSelf().path().name()+"] added [" + ar.path().name() +
                    "] to its distribution list.");
		}
		if(message instanceof Unjoin){
			ActorRef ar = getSender();
            if (nextDest == distList.indexOf(ar)) {
                ++nextDest;
                if(nextDest >= distList.size())
                    nextDest = 0;
            }
            else if (nextDest > distList.indexOf(ar))
                --nextDest;
            distList.remove(ar);
			log.info("["+getSelf().path().name()+"] removed [" + ar.path().name() +
                    "] from its distribution list.");
		}
	}
}
