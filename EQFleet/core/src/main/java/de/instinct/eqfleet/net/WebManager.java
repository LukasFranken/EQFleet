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
import de.instinct.eqfleet.net.model.ConnectionStatus;
import de.instinct.eqfleet.net.model.Request;
import de.instinct.eqfleet.net.model.RequestConsumer;
import de.instinct.eqfleet.net.model.RequestErrorConsumer;
import de.instinct.eqfleet.net.model.RequestSupplier;
import de.instinct.eqlibgdxutils.StringUtils;
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
    private static final int PING_UPDATE_CLOCK_MS = 1000;
    
    private static long lastPingTime;
    
    private static ScheduledExecutorService scheduler;
    
    public static ConnectionStatus status;

    public static void init() {
    	status = ConnectionStatus.OFFLINE;
    	
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
		
		ping();
    }

    private static void initializeAPI() {
    	API.authKey = "";
    	API.setLoggingHook(new LoggingHook() {
			
			@Override
			public void log(String message) {
				Logger.log("API", StringUtils.limitWithAppendix(message, 500), ConsoleColor.MAGENTA);
			}
			
		});
        API.initialize(GlobalStaticData.configuration);
	}
    
    public static void register() {
    	String authKey = API.authentication().register();
    	PreferenceManager.save("authkey", authKey);
    	API.authKey = authKey;
    	status = ConnectionStatus.ONLINE;
    }
    
    public static boolean authenticate(String authKey) {
    	if (!authKey.isEmpty()) {
    		TokenVerificationResponse response = API.authentication().verify(authKey);
        	if (response == TokenVerificationResponse.VERIFIED) {
    			API.authKey = authKey;
    			PreferenceManager.save("authkey", authKey);
    			status = ConnectionStatus.ONLINE;
    			Logger.log(LOGTAG, "Logged in", ConsoleColor.GREEN);
    			return true;
    		}
        	if (response == TokenVerificationResponse.DOESNT_EXIST) {
    			PreferenceManager.save("authkey", "");
    		}
		}
    	status = ConnectionStatus.UNAUTHORIZED;
    	return false;
    }

    public static void loadAuthKey() {
		final String authKey = PreferenceManager.load("authkey");
		authenticate(authKey);
	}

	public static void update() {
    	Console.updateMetric(CONNECTION_STATUS_TAG, status.toString());
    	
    	lastPingTime += NETWORK_UPDATE_CLOCK_MS;
    	if (lastPingTime > PING_UPDATE_CLOCK_MS) {
    		ping();
    		lastPingTime = 0;
    	}
    	
    	if (status == ConnectionStatus.ONLINE) {
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
    	}
    	if (status == ConnectionStatus.OFFLINE) {
    		//work against offline engine
    	}
    	if (status == ConnectionStatus.AUTHENTICATING) {
    		loadAuthKey();
    	}
    }
    
    private static void ping() {
    	long ping = API.discovery().ping();
		Console.updateMetric(PING_METRIC_TAG, ping);
		if (ping == -1) {
			status = ConnectionStatus.OFFLINE;
			API.authKey = "";
			Logger.log(LOGTAG, "Disconnected", ConsoleColor.RED);
		} else {
			if (status == ConnectionStatus.OFFLINE) {
				status = ConnectionStatus.AUTHENTICATING;
				Logger.log(LOGTAG, "Connected", ConsoleColor.GREEN);
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
    
}