package de.instinct.eqfleet.net.discovery;

import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.WebService;
import de.instinct.eqfleet.net.model.NetworkRequest;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqfleet.net.model.SupportedRequestType;

public class DiscoveryInterface {

	public static void single(WebService service, ResponseAction responseAction) {
		WebManager.requestQueue.add(NetworkRequest.builder()
				.service(WebService.DISCOVERY)
				.type(SupportedRequestType.GET)
				.endpoint("single")
				.requestParam(service.getTag())
				.responseAction(responseAction)
				.build());
	}

}
