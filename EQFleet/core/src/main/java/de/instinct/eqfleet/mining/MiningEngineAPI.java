package de.instinct.eqfleet.mining;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.MiningPlayerShipProcessor;

public class MiningEngineAPI {

	public static MiningPlayerShip getShip(int playerId) {
		return MiningPlayerShipProcessor.getPlayerShip(MiningModel.state, playerId);
	}
	
	public static boolean shipIsRecallable(MiningPlayerShip ship) {
		return MiningPlayerShipProcessor.shipIsInRecallArea(MiningModel.state, ship) 
				&& ship.speed <= 0.1f && ship.speed >= -0.1f 
				&& MiningModel.state.metaData.pauseData.resumeCountdownMS <= 0;
	}

}
