package demo;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

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

	static public class Request {
		public final String data;
		public Request(String data) {
			this.data = data;
		}
    }

	static public class Response {
		public final String data;
		public Response(String data) {
			this.data = data;
		}
    }

	@Override
	public void onReceive(Object message) throws Throwable {
		if(message instanceof Request){
			Request rq = (Request) message;
			log.info("["+getSelf().path().name()+"] received request from ["+ getSender().path().name() +"] with data: ["+rq.data+"]");
            Response rp = new Response ("Response to " + rq.data);
            getSender().tell(rp, getSelf());
		}

		if(message instanceof Response){
			Response rp = (Response) message;
			log.info("["+getSelf().path().name()+"] received response from ["+ getSender().path().name() +"] with data: ["+rp.data+"]");
		}
	}

}
