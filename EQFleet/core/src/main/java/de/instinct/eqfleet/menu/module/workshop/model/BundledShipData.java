package de.instinct.eqfleet.menu.module.workshop.model;

import de.instinct.api.shipyard.dto.PlayerShipData;
import de.instinct.api.shipyard.dto.ShipBlueprint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BundledShipData {
	
	private PlayerShipData playerShipData;
	private ShipBlueprint blueprint;

}
