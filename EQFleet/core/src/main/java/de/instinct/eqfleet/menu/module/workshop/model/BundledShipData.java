package de.instinct.eqfleet.menu.module.workshop.model;

import de.instinct.api.shipyard.dto.ship.PlayerShipData;
import de.instinct.api.shipyard.dto.ship.ShipBlueprint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BundledShipData {
	
	private PlayerShipData playerShipData;
	private ShipBlueprint blueprint;

}
