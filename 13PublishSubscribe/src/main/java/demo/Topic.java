package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import demo.MyActor.MyMessage;

public class Topic extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ArrayList<ActorRef> distList;

	public Topic() {
        distList = new ArrayList<ActorRef>();
    }

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Topic.class, () -> {
			return new Topic();
		});
	}

    public static class Subscribe {
        public Subscribe() {}
    }

    public static class Unsubscribe {
        public Unsubscribe() {}
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
            for (ActorRef ar : dest) 
                ar.tell(m, msgSender);
		}
		if(message instanceof Subscribe){
			ActorRef ar = getSender();
            distList.add(ar);
			log.info("["+getSelf().path().name()+"] added [" + ar.path().name() +
                    "] to its distribution list.");
		}
		if(message instanceof Unsubscribe){
			ActorRef ar = getSender();
            distList.remove(ar);
			log.info("["+getSelf().path().name()+"] removed [" + ar.path().name() +
                    "] from its distribution list.");
		}
	}
}
