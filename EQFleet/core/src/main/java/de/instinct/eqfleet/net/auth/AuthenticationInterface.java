package de.instinct.eqfleet.net.auth;

import de.instinct.eqfleet.net.GlobalStaticData;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.WebService;
import de.instinct.eqfleet.net.model.NetworkRequest;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqfleet.net.model.SupportedRequestType;
import de.instinct.eqlibgdxutils.PreferenceUtil;

public class AuthenticationInterface {

	public static void verify(String authKey, ResponseAction responseAction) {
		WebManager.requestQueue.add(NetworkRequest.builder()
				.service(WebService.AUTHENTICATION)
				.type(SupportedRequestType.GET)
				.endpoint("verify")
				.requestParam(authKey)
				.responseAction(responseAction)
				.build());
	}

	public static void register(ResponseAction chainedResponseAction) {
		WebManager.requestQueue.add(NetworkRequest.builder()
				.service(WebService.AUTHENTICATION)
				.type(SupportedRequestType.GET)
				.endpoint("register")
				.responseAction(new ResponseAction() {
					
					@Override
					public void execute(NetworkResponse response) {
						String token = response.getPayload();
						if (!token.contentEquals("")) {
							GlobalStaticData.authKey = token;
							PreferenceUtil.save("authkey", token);
							System.out.println("Authkey saved: " + token);
						}
						if (chainedResponseAction != null) chainedResponseAction.execute(response);
					}
					
				})
				.build());
	}

}
