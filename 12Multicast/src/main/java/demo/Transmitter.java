package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import demo.MyActor.MyMessage;

public class Transmitter extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ArrayList<GroupDeclaration> groups;

    static public class GroupDeclaration {
        String name;
        ArrayList<ActorRef> members;

        public GroupDeclaration (String name, ArrayList<ActorRef> members) {
            this.name = name;
            this.members = members;
        }
    }

    static public class GroupMessage {
        String dest;
        MyMessage m;

        public GroupMessage(String dest, MyMessage m) {
            this.dest = dest;
            this.m = m;
        }
    }

	public Transmitter () {
        groups = new ArrayList<GroupDeclaration>();
    }

	// Static function creating actor
	public static Props createActor() {
		return Props.create(Transmitter.class, () -> {
			return new Transmitter();
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof MyMessage){
			MyMessage m = (MyMessage) message;
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] saying ["+m.data+"]");
		}
		if(message instanceof GroupDeclaration){
            GroupDeclaration gd = (GroupDeclaration) message;
            groups.add(gd);
            String names = "";
            for (ActorRef ar : gd.members)
                names = ar.path().name() + " " + names;
			log.info("["+getSelf().path().name()+"] added the new group [" +
                    gd.name + "] with members [" + names + "]");
		}
		if(message instanceof GroupMessage){
            ActorRef senderRef = getSender();
            GroupMessage gm = (GroupMessage) message;
            for (GroupDeclaration gd : groups)
                if (gm.dest.equals(gd.name)) {
                    for (ActorRef ar : gd.members)
                        ar.tell(gm.m, senderRef);
                    break;
                }
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to tell [" + 
                    gm.m.data + "] for group [" + gm.dest + "]");
		}
	}
}
