package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

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

	static public class MiddleM {
		public final String data;
		public final ActorRef goal;
		public MiddleM(String data, ActorRef goal) {
			this.data = data;
			this.goal = goal;
		}
    }
    
	static public class EndM {
		public final String data;
		public EndM(String data) {
			this.data = data;
		}
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof MiddleM){
			MiddleM mm = (MiddleM) message;
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] asking him to tell [" +
                    mm.goal.path().name() + "] this : [" + mm.data+"]");
            EndM em = new EndM ("Here's my response to " + mm.data);
            mm.goal.tell(em, getSelf());
		}
		if(message instanceof EndM){
			EndM em = (EndM) message;
			log.info("["+getSelf().path().name()+"] received message from ["+
                    getSender().path().name() +"] saying ["+em.data+"]");
		}
	}

}
