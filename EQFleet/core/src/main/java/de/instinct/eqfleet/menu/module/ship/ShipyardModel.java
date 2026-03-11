package de.instinct.eqfleet.menu.module.ship;

import java.util.Queue;

import de.instinct.api.shipyard.dto.PlayerShipyardData;
import de.instinct.api.shipyard.dto.ShipyardData;
import de.instinct.eqfleet.menu.module.ship.message.ShipyardMessage;

public class ShipyardModel {
	
	public static volatile Queue<ShipyardMessage> messageQueue;
	
	public static volatile boolean dataUpdated;
	public static volatile PlayerShipyardData playerShipyard;
	public static volatile ShipyardData shipyard;

}
