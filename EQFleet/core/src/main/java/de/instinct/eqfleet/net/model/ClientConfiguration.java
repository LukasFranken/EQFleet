package de.instinct.eqfleet.net.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientConfiguration {
	
	private String protocol;
	private String address;
	private String endpoint;
	private int port;

}
