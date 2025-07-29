package de.instinct.engine.order;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.order.types.SurrenderOrder;
import de.instinct.engine.util.EngineUtility;

public class OrderValidator {
	
	public boolean isValid(GameState state, GameOrder order) {
		if (order instanceof ShipMovementOrder) {
			ShipMovementOrder shipMovementOrder = (ShipMovementOrder)order;
			Planet fromPlanet = EngineUtility.getPlanet(state.planets, shipMovementOrder.fromPlanetId);
			Player player = EngineUtility.getPlayer(state.players, shipMovementOrder.playerId);
			ShipData playerShip = player.ships.get(shipMovementOrder.playerShipId);
			
			if (state.teamPause != 0) return false;
			if (fromPlanet.ownerId != shipMovementOrder.playerId) return false;
			if (shipMovementOrder.fromPlanetId == shipMovementOrder.toPlanetId) return false;
			if (fromPlanet.currentResources < playerShip.cost) return false;
			if (player.currentCommandPoints < playerShip.commandPointsCost) return false;
			return true;
		}
		if (order instanceof GamePauseOrder) {
			GamePauseOrder gamePauseOrder = (GamePauseOrder)order;
			Player player = EngineUtility.getPlayer(state.players, gamePauseOrder.playerId);
			
			if (gamePauseOrder.pause) {
				if (state.teamPause != 0) return false;
				if (state.teamPausesMS.get(player.teamId) >= state.maxPauseMS) return false;
				if (state.teamPausesCount.get(player.teamId) <= 0) return false;
			} else {
				if (state.teamPause == 0) return false;
				if (player.teamId != state.teamPause) return false;
				if (state.currentPauseElapsedMS < state.minPauseMS) return false;
			}
			return true;
		}
		if (order instanceof SurrenderOrder) {
			SurrenderOrder surrenderOrder = (SurrenderOrder)order;
			if (surrenderOrder.playerId == 0) return false;
			return true;
		}
		return false;
	}

}
