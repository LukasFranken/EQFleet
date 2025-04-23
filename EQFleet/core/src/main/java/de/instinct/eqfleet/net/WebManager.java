package de.instinct.eqfleet.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.utils.Timer;

import de.instinct.eqfleet.net.discovery.DiscoveryInterface;
import de.instinct.eqfleet.net.discovery.dto.ServiceDiscoveryResponse;
import de.instinct.eqfleet.net.model.ClientConfiguration;
import de.instinct.eqfleet.net.model.NetworkRequest;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;

public class WebManager {

	private static final String LOGTAG = "WebManager";
	
    public static ConcurrentLinkedQueue<NetworkRequest> requestQueue;
    public static ConcurrentLinkedQueue<NetworkResponse> responseQueue;

    private static Map<WebService, String> serviceBaseUrls;

    private static final long NETWORK_UPDATE_CLOCK_MS = 20;

    private static WebController controller;

    public static void init() {
        controller = new WebController();
        requestQueue = new ConcurrentLinkedQueue<>();
        responseQueue = new ConcurrentLinkedQueue<>();
        
        serviceBaseUrls = new HashMap<>();
        buildBaseClient();

        Timer.schedule(new Timer.Task() {
        	
            @Override
            public void run() {
                update();
            }
            
        }, 0, NETWORK_UPDATE_CLOCK_MS / 1000f);
    }

    private static void buildBaseClient() {
        ClientConfiguration discoveryServerConfig = ClientConfiguration.builder()
                .protocol("http")
                .address("eqgame.dev")
                .port(6000)
                .endpoint("discover")
                .build();
        buildClient(WebService.DISCOVERY, discoveryServerConfig);
    }

    private static void update() {
        NetworkRequest request = requestQueue.peek();
        if (request != null && serviceBaseUrls.get(request.getService()) != null) {
            sendRequest(request);
            requestQueue.remove();
        }
    }

    public static void sendRequest(NetworkRequest request) {
        try {
            String baseUrl = serviceBaseUrls.get(request.getService());
            if (baseUrl == null) return;

            switch (request.getType()) {
                case GET:
                    controller.sendGetRequest(baseUrl, request);
                    break;
                case POST:
                    controller.sendPostRequest(baseUrl, request);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(LOGTAG, "Sending of NetworkRequest has failed: " + request);
        }
    }

    public static void buildClient(WebService service) {
        DiscoveryInterface.single(service, new ResponseAction() {
            @Override
            public void execute(NetworkResponse response) {
                if (response.getPayload() != null) {
                    ServiceDiscoveryResponse discoveryResponse = ObjectJSONMapper.mapJSON(response.getPayload(), ServiceDiscoveryResponse.class);
                    buildClient(service, discoveryResponse.getServiceUrl());
                } else {
                	Logger.log(LOGTAG, "Discovery server found no service for tag: " + service.getTag());
                }
            }
        });
    }

    public static void buildClient(WebService service, ClientConfiguration config) {
        String baseUrl = config.getProtocol() + "://" + config.getAddress() + ":" + config.getPort() + "/" + config.getEndpoint();
        buildClient(service, baseUrl);
    }

    public static void buildClient(WebService service, String baseUrl) {
        if (controller.isServiceAvailable(baseUrl)) {
            serviceBaseUrls.put(service, baseUrl);
            Logger.log(LOGTAG, "Client created for Service '" + service + "' with tag: " + service.getTag() + " at Base URL: " + baseUrl);
        } else {
        	Logger.log(LOGTAG, "Service unavailable: '" + service + "' with tag: " + service.getTag() + " at Base URL: " + baseUrl);
        }
    }
    
}