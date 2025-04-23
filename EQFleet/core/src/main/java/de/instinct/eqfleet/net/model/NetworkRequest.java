package de.instinct.eqfleet.net.model;

import de.instinct.eqfleet.net.WebService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetworkRequest {
	
	private WebService service;
	private SupportedRequestType type;
	private String endpoint;
	private String requestParam;
	private Object payload;
	private ResponseAction responseAction;

}
