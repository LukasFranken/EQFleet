package de.instinct.eqfleet.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.instinct.eqfleet.net.model.NetworkRequest;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebController {

	private static final String LOGTAG = "WebController";
	
    private static final long TIMEOUT_MS = 2000;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public WebController() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .build();
    }

    public void sendGetRequest(String baseUrl, NetworkRequest request) {
        String uri = baseUrl + buildUri(request);
        String authKey = GlobalStaticData.authKey == null ? "" : GlobalStaticData.authKey;

        Request httpRequest = new Request.Builder()
                .url(uri)
                .addHeader("token", authKey)
                .get()
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.body() != null) {
                handleResponseString(request, response.body().string());
            }
        } catch (IOException e) {
        	Logger.log(LOGTAG, e.getMessage());
        }
    }

    public void sendPostRequest(String baseUrl, NetworkRequest request) {
        String uri = baseUrl + buildUri(request);
        String authKey = GlobalStaticData.authKey == null ? "" : GlobalStaticData.authKey;

        String jsonBody = request.getPayload() instanceof String
                ? (String) request.getPayload()
                : "{}";

        RequestBody requestBody = RequestBody.create(jsonBody, JSON);

        Request httpRequest = new Request.Builder()
                .url(uri)
                .addHeader("token", authKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (response.body() != null) {
                handleResponseString(request, response.body().string());
            }
        } catch (IOException e) {
        	Logger.log(LOGTAG, e.getMessage());
        }
    }

    public boolean isServiceAvailable(String baseUrl) {
        Request request = new Request.Builder()
                .url(baseUrl + "/ping")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    private String buildUri(NetworkRequest request) {
        String endpoint = request.getEndpoint() == null ? "" : "/" + request.getEndpoint();
        String param = request.getRequestParam() == null ? "" : "/" + request.getRequestParam();
        return endpoint + param;
    }

    private void handleResponseString(NetworkRequest request, String responseBodyString) {
        NetworkResponse response = NetworkResponse.builder()
                .payload(responseBodyString)
                .build();
        if (request.getResponseAction() == null) {
            WebManager.responseQueue.add(response);
        } else {
            request.getResponseAction().execute(response);
        }
    }
}