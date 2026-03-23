package de.instinct.engine.combat;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class CombatOrderValidator {
	
	public static boolean isValid(BuildTurretOrder buildTurretOrder, GameState state) {
		Planet buildPlanet = EngineUtility.getPlanet(state.entityData.planets, buildTurretOrder.planetId);
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, buildTurretOrder.playerId);
		TurretData playerTurret = player.turrets.get(buildTurretOrder.turretId);
		
		if (state.pauseData.teamPause != 0) return false;
		if (playerTurret == null) return false;
		if (buildPlanet.ownerId != buildTurretOrder.playerId) return false;
		if (player.currentResources < playerTurret.resourceCost) return false;
		for (Turret turret : state.entityData.turrets) if (turret.originPlanetId == buildTurretOrder.planetId) return false;
		return true;
	}

	public static boolean isValid(ShipMovementOrder shipMovementOrder, GameState state) {
		Planet fromPlanet = EngineUtility.getPlanet(state.entityData.planets, shipMovementOrder.fromPlanetId);
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, shipMovementOrder.playerId);
		ShipData playerShip = player.ships.get(shipMovementOrder.playerShipId);
		
		if (state.pauseData.teamPause != 0) return false;
		if (fromPlanet.ownerId != shipMovementOrder.playerId) return false;
		if (shipMovementOrder.fromPlanetId == shipMovementOrder.toPlanetId) return false;
		if (player.currentResources < playerShip.resourceCost) return false;
		return true;
	}

}
