package de.instinct.eqfleet.net;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.core.API;
import de.instinct.api.core.logging.LoggingHook;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.net.model.Request;
import de.instinct.eqfleet.net.model.RequestConsumer;
import de.instinct.eqfleet.net.model.RequestSupplier;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class WebManager {

	private static final String LOGTAG = "WebManager";
	
    public static ConcurrentLinkedQueue<Request<?>> requestQueue;

    private static final long NETWORK_UPDATE_CLOCK_MS = 50;
    
    private static ScheduledExecutorService scheduler;

    public static void init() {
        requestQueue = new ConcurrentLinkedQueue<>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			try {
				update();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.log(LOGTAG, "Error during network update: " + e.getMessage(), ConsoleColor.RED);
			}
		}, 0, NETWORK_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
    }

    private static void update() {
    	if (!API.isInitialized()) {
    		API.setLoggingHook(new LoggingHook() {
    			
    			@Override
    			public void log(String message) {
    				Logger.log("API", message, ConsoleColor.MAGENTA);
    			}
    			
    		});
            API.initialize(GlobalStaticData.configuration);
    	}
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
    
    public static void dispose() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        requestQueue.clear();
    }
    
}