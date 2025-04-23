package de.instinct.eqfleet.net.meta;

import de.instinct.eqfleet.net.GlobalStaticData;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.WebService;
import de.instinct.eqfleet.net.meta.dto.ProfileData;
import de.instinct.eqfleet.net.model.NetworkRequest;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqfleet.net.model.SupportedRequestType;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;

public class MetaInterface {

	public static void loadProfile() {
		WebManager.requestQueue.add(NetworkRequest.builder()
				.service(WebService.META)
				.type(SupportedRequestType.GET)
				.endpoint("profile")
				.responseAction(new ResponseAction() {
					
					@Override
					public void execute(NetworkResponse response) {
						ProfileData profile = ObjectJSONMapper.mapJSON(response.getPayload(), ProfileData.class);
						if (profile != null) {
							GlobalStaticData.profile = profile;
						}
					}
					
				})
				.build());
	}

	public static void registerName(String username, ResponseAction responseAction) {
		WebManager.requestQueue.add(NetworkRequest.builder()
				.service(WebService.META)
				.type(SupportedRequestType.POST)
				.endpoint("register")
				.requestParam(username)
				.responseAction(responseAction)
				.build());
	}
	
}
