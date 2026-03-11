package de.instinct.eqfleet.menu.module.ship.message.types;

import de.instinct.eqfleet.menu.module.ship.message.ShipyardMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BuildShipMessage extends ShipyardMessage {
	
	private String shipUUID;
	
}
