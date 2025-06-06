package de.instinct.eqfleet.net;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.utils.Timer;

import de.instinct.api.core.API;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.net.model.Request;
import de.instinct.eqfleet.net.model.RequestConsumer;
import de.instinct.eqfleet.net.model.RequestSupplier;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class WebManager {

	private static final String LOGTAG = "WebManager";
	
    public static ConcurrentLinkedQueue<Request<?>> requestQueue;

    private static final float NETWORK_UPDATE_CLOCK_MS = 20;

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
    
    public static <T> void enqueue(RequestSupplier<T> requestSupplier, RequestConsumer<T> responseHandler) {
        Request<T> request = new Request<>();
        request.setRequestAction(() -> {
            T result = requestSupplier.get();
            responseHandler.accept(result);
            if (result != null) Logger.log(LOGTAG, "received response: " + result, ConsoleColor.BLUE);
        });
        requestQueue.add(request);
    }
    
}