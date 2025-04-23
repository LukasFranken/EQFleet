package de.instinct.eqfleetgameserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class GameserverConfig {
	
	@Value("${gameserver.port.tcp:missing_tcpport}")
	private int tcpPort;
	
	@Value("${gameserver.port.udp:missing_udpport}")
	private int udpPort;

}
