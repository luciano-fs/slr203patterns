package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import demo.MyActor.MyMessage;

public class MyBroadcaster extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ArrayList<ActorRef> distList;

	public MyBroadcaster() {
        distList = new ArrayList<ActorRef>();
    }

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyBroadcaster.class, () -> {
			return new MyBroadcaster();
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof MyMessage){
			MyMessage m = (MyMessage) message;
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to dsitriute [" +
                    m.data +"]");
            for (ActorRef ar : distList) 
                ar.tell(m, getSender());
		}
		if(message instanceof ActorRef){
			ActorRef ar = (ActorRef) message;
            distList.add(ar);
			log.info("["+getSelf().path().name()+"] added [" + ar.path().name() +
                    "] to the distribution list by request of [" +
                    getSender().path().name() + "]");
		}
	}
}
