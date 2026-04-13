package de.instinct.engine.fleet.order.processors;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.planet.PlanetProcessor;
import de.instinct.engine.fleet.entity.unit.ship.ShipProcessor;
import de.instinct.engine.fleet.entity.unit.ship.data.ShipData;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.entity.unit.turret.TurretProcessor;
import de.instinct.engine.fleet.entity.unit.turret.data.TurretData;
import de.instinct.engine.fleet.order.types.BuildTurretOrder;
import de.instinct.engine.fleet.order.types.ShipMovementOrder;
import de.instinct.engine.fleet.player.FleetPlayer;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;

public class CombatOrderProcessor {
	
	private PlanetProcessor planetProcessor;
	private FleetPlayerProcessor playerProcessor;
	private TurretProcessor turretProcessor;
	private ShipProcessor shipProcessor;
	
	public CombatOrderProcessor() {
		planetProcessor = new PlanetProcessor();
		playerProcessor = new FleetPlayerProcessor();
		turretProcessor = new TurretProcessor();
		shipProcessor = new ShipProcessor();
	}
	
	public boolean integrateNewOrder(FleetGameState state, GameOrder order) {
		if (order instanceof ShipMovementOrder) {
            ShipMovementOrder shipMovementOrder = (ShipMovementOrder) order;
            if (isValid(shipMovementOrder, state)) {
            	shipProcessor.createShipInstance(shipMovementOrder, state);
            	return true;
            }
        }
        if (order instanceof BuildTurretOrder) {
        	BuildTurretOrder buildTurretOrder = (BuildTurretOrder) order;
        	if (isValid(buildTurretOrder, state)) {
        		turretProcessor.createTurret(planetProcessor.getPlanet(state.entityData.planets, buildTurretOrder.planetId), buildTurretOrder.turretId, state, true);
        		return true;
        	}
        }
        return false;
    }
	
	private boolean isValid(BuildTurretOrder buildTurretOrder, FleetGameState state) {
		Planet buildPlanet = planetProcessor.getPlanet(state.entityData.planets, buildTurretOrder.planetId);
		FleetPlayer player = playerProcessor.getFleetPlayer(state.playerData.players, buildTurretOrder.playerId);
		TurretData playerTurret = player.turrets.get(buildTurretOrder.turretId);
		
		if (state.metaData.pauseData.teamPause != 0) return false;
		if (playerTurret == null) return false;
		if (buildPlanet.ownerId != buildTurretOrder.playerId) return false;
		if (player.currentResources < playerTurret.resourceCost) return false;
		for (Turret turret : state.entityData.turrets) if (turret.originPlanetId == buildTurretOrder.planetId) return false;
		return true;
	}

	private boolean isValid(ShipMovementOrder shipMovementOrder, FleetGameState state) {
		Planet fromPlanet = planetProcessor.getPlanet(state.entityData.planets, shipMovementOrder.fromPlanetId);
		FleetPlayer player = playerProcessor.getFleetPlayer(state.playerData.players, shipMovementOrder.playerId);
		ShipData playerShip = player.ships.get(shipMovementOrder.playerShipId);
		
		if (state.metaData.pauseData.teamPause != 0) return false;
		if (fromPlanet.ownerId != shipMovementOrder.playerId) return false;
		if (shipMovementOrder.fromPlanetId == shipMovementOrder.toPlanetId) return false;
		if (player.currentResources < playerShip.resourceCost) return false;
		return true;
	}

}
