package de.instinct.eqfleet.net;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.auth.dto.TokenVerificationResponse;
import de.instinct.api.core.API;
import de.instinct.api.core.logging.LoggingHook;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqfleet.net.model.Request;
import de.instinct.eqfleet.net.model.RequestConsumer;
import de.instinct.eqfleet.net.model.RequestErrorConsumer;
import de.instinct.eqfleet.net.model.RequestSupplier;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.types.NumberMetric;
import de.instinct.eqlibgdxutils.debug.metrics.types.StringMetric;

public class WebManager {

	private static final String LOGTAG = "WebManager";
	private static final String PING_METRIC_TAG = "server_ping_MS";
	private static final String CONNECTION_STATUS_TAG = "online";
	
    public static ConcurrentLinkedQueue<Request<?>> requestQueue;

    private static final long NETWORK_UPDATE_CLOCK_MS = 50;
    private static final int PING_UPDATE_CLOCK_MS = 500;
    
    private static long lastPingTime;
    
    private static ScheduledExecutorService scheduler;
    
    private static boolean online;

    public static void init() {
    	initializeAPI();
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
		
		Console.registerMetric(NumberMetric.builder()
				.tag(PING_METRIC_TAG)
				.build());
		Console.registerMetric(StringMetric.builder()
        		.tag(CONNECTION_STATUS_TAG)
        		.build());
    }

    private static void initializeAPI() {
    	API.authKey = "";
    	API.setLoggingHook(new LoggingHook() {
			
			@Override
			public void log(String message) {
				Logger.log("API", message, ConsoleColor.MAGENTA);
			}
			
		});
        API.initialize(GlobalStaticData.configuration);
	}

	private static void verifyAuthKey() {
		final String authKey = PreferenceManager.load("authkey");
		if (!authKey.isEmpty()) {
			TokenVerificationResponse result = API.authentication().verify(authKey);
			if (result == TokenVerificationResponse.VERIFIED) {
				API.authKey = authKey;
				PreferenceManager.save("authkey", authKey);
			} else {
				API.authKey = "invalid";
			}
		}
	}

	public static void update() {
    	Console.updateMetric(CONNECTION_STATUS_TAG, "" + online);
    	
    	lastPingTime += NETWORK_UPDATE_CLOCK_MS;
    	if (lastPingTime > PING_UPDATE_CLOCK_MS) {
    		ping();
    		lastPingTime = 0;
    		if (API.authKey.isEmpty()) {
        		verifyAuthKey();
        	}
    	}
    	
    	if (online) {
    		if (!API.authKey.isEmpty()) {
    			Request<?> request = requestQueue.peek();
                if (request != null) {
                    try {
                    	request.getRequestAction().execute();
        			} catch (Exception e) {
        				if (request.getErrorConsumer() != null) request.getErrorConsumer().onError(e);
        			}
                    if (!requestQueue.isEmpty()) requestQueue.remove();
                }
        	}
    	} else {
    		//TODO work against offline engine
    	}
    }
    
    private static void ping() {
    	long ping = API.discovery().ping();
		Console.updateMetric(PING_METRIC_TAG, ping);
		if (ping == -1) {
			online = false;
			API.authKey = "offline";
		} else {
			online = true;
			if (API.authKey.equals("offline")) {
				API.authKey = "";
			}
		}
	}

	public static <T> void enqueue(RequestSupplier<T> requestSupplier, RequestConsumer<T> responseHandler) {
        Request<T> request = new Request<>();
        request.setRequestAction(() -> {
            T result = requestSupplier.get();
            responseHandler.accept(result);
        });
        requestQueue.add(request);
    }
    
    public static <T> void enqueue(RequestSupplier<T> requestSupplier, RequestConsumer<T> responseHandler, RequestErrorConsumer errorHandler) {
        Request<T> request = new Request<>();
        request.setRequestAction(() -> {
            T result = requestSupplier.get();
            responseHandler.accept(result);
        });
        request.setErrorConsumer(errorHandler);
        requestQueue.add(request);
    }
    
    public static void dispose() {
    	Logger.log(LOGTAG, "Disposing...", ConsoleColor.YELLOW);
    	Console.remove(PING_METRIC_TAG);
    	Console.remove(CONNECTION_STATUS_TAG);
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (requestQueue != null) requestQueue.clear();
    }

	public static boolean isOnline() {
		return online;
	}
    
}