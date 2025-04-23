package de.instinct.eqfleet.net;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.utils.Timer;

import de.instinct.api.core.API;
import de.instinct.eqfleet.net.model.Request;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class WebManager {

	private static final String LOGTAG = "WebManager";
	
    public static ConcurrentLinkedQueue<Request<?>> requestQueue;

    private static final long NETWORK_UPDATE_CLOCK_MS = 20;

    public static void init() {
        requestQueue = new ConcurrentLinkedQueue<>();
        
        API.initialize(GlobalStaticData.configuration);

        Timer.schedule(new Timer.Task() {
        	
            @Override
            public void run() {
                update();
            }
            
        }, 0, NETWORK_UPDATE_CLOCK_MS / 1000f);
    }

    private static void update() {
        Request<?> request = requestQueue.peek();
        if (request != null) {
            request.getRequestAction().execute();
            requestQueue.remove();
        }
    }
    
    public static <T> void enqueue(Supplier<T> requestSupplier, Consumer<T> responseHandler) {
        Request<T> request = new Request<>();
        request.setRequestAction(() -> {
            T result = requestSupplier.get();
            responseHandler.accept(result);
            if (result != null) Logger.log(LOGTAG, "received response of type: " + result.getClass().getName());
        });
        requestQueue.add(request);
    }
    
}