package demo;

import java.util.ArrayList;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;
import demo.MyActor.MyMessage;

public class MyMerger extends UntypedAbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef dest;

    private class MsgSenders {
        public MyMessage m;
        public ArrayList<ActorRef> s;

        public MsgSenders(MyMessage m, ArrayList<ActorRef> s) {
            this.m = m;
            this.s = s;
        }
    }

    private ArrayList<ActorRef> recList;
    private ArrayList<MsgSenders> ml;

	public MyMerger (ActorRef ar) {
        recList = new ArrayList<ActorRef>();
        ml = new ArrayList<MsgSenders>();
        dest = ar;
        log.info("I was linked to actor reference {}", this.dest.path().name());
    }

	// Static function creating actor
	public static Props createActor(ActorRef ar) {
		return Props.create(MyMerger.class, () -> {
			return new MyMerger(ar);
		});
	}

    static public class CastMessage {
        public final ArrayList<ActorRef>senders;
        public final MyMessage m;
        public CastMessage (MyMessage m, ArrayList<ActorRef>senders) {
            this.m = m;
            this.senders = senders;
        }
    }

    static public class JoinMessage {
        public JoinMessage() {}
    }
    static public class UnjoinMessage {
        public UnjoinMessage() {}
    }


	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof MyMessage){
			MyMessage m = (MyMessage) message;
			ActorRef ar = getSender();
            int i;
            boolean ready = true;

			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to dsitriute [" +
                    m.data +"]");

            //Checks to see if the message is new or not
            for (i = 0; i < ml.size(); ++i)
                if (ml.get(i).m == m)
                    break;
            //If new, add new sender
            if (i < ml.size())
                ml.get(i).s.add(ar);
            //Else, add new message and sender
            else {
                ArrayList<ActorRef> al = new ArrayList<ActorRef>();
                al.add(ar);
                ml.add(new MsgSenders(m, al));
            }
            //Sends the message if everyone in the receipient list sent the msg
            for (ActorRef aux : recList)
                if (!ml.get(i).s.contains(aux))
                    ready = false;
            if (ready) {
                CastMessage cm = new CastMessage (m, new ArrayList<ActorRef>(recList));
                ml.remove(i);
                dest.tell(cm, getSelf());
            }
		}
		if(message instanceof JoinMessage){
			ActorRef ar = getSender();
            recList.add(ar);
			log.info("["+getSelf().path().name()+"] added [" + ar.path().name() +
                    "] to the recipient list");
		}
		if(message instanceof UnjoinMessage){
			ActorRef ar = getSender();
            recList.remove(ar);
			log.info("["+getSelf().path().name()+"] deleted [" + ar.path().name() +
                    "] of the recipient list");
		}
	}
}
