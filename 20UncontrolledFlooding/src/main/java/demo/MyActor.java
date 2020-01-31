package demo;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;
import java.lang.*;

public class MyActor extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	// Actor reference
	private ArrayList<ActorRef> contacts;

	public MyActor() {
        contacts = new ArrayList<ActorRef>();
    }

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyActor.class, () -> {
			return new MyActor();
		});
	}

    static public class ShowMSG {
        public ShowMSG() {}
    }

    static public class MyMessage {
        String data;
        public MyMessage (String data) {
            this.data = data;
        }
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof ActorRef) {
            ActorRef ar = (ActorRef) message;
            contacts.add(ar);
		}
        if(message instanceof ShowMSG) {
            StringBuilder names = new StringBuilder();
            for(ActorRef c : contacts) {
                names.append(c.path().name() + " ");
            }
            log.info("["+getSelf().path().name()+"] has the following contacts: ["+ names.toString() +"]");
        }
        if(message instanceof MyMessage) {
            MyMessage m = (MyMessage) message;
            log.info("["+getSelf().path().name()+"] has received a message: ["+ m.data +"] from [" + getSender().path().name() + "]");
            for(ActorRef c : contacts)
                c.tell(m, getSelf());
        }
	}
}
