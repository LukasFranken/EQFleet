package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.planet.TurretData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class CombatProcessor {

    private ShipProcessor shipProcessor;
    private TurretProcessor turretProcessor;
    private ProjectileProcessor projectileProcessor;

    public CombatProcessor() {
        shipProcessor = new ShipProcessor();
        turretProcessor = new TurretProcessor();
        projectileProcessor = new ProjectileProcessor();
    }
    
    public void initialize(GameState state) {
		turretProcessor.initializeTurrets(state);
	}

    public void update(GameState state, long deltaTime) {
    	turretProcessor.updateTurrets(state, deltaTime);
    	shipProcessor.updateShips(state, deltaTime);
        projectileProcessor.updateProjectiles(state, deltaTime);
    }

	public boolean integrateNewOrder(GameState state, GameOrder order) {
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
        		turretProcessor.createTurretInstance(buildTurretOrder.planetId, state, true);
        		return true;
        	}
        }
        return false;
    }

	private boolean isValid(BuildTurretOrder buildTurretOrder, GameState state) {
		Planet buildPlanet = EngineUtility.getPlanet(state.planets, buildTurretOrder.planetId);
		Player player = EngineUtility.getPlayer(state.players, buildTurretOrder.playerId);
		TurretData playerTurret = player.planetData.turret;
		
		if (state.teamPause != 0) return false;
		if (playerTurret == null) return false;
		if (buildPlanet.ownerId != buildTurretOrder.playerId) return false;
		if (buildPlanet.currentResources < playerTurret.cost) return false;
		if (player.currentCommandPoints < playerTurret.commandPointsCost) return false;
		for (Turret turret : state.turrets) if (turret.planetId == buildTurretOrder.planetId) return false;
		return true;
	}

	private boolean isValid(ShipMovementOrder shipMovementOrder, GameState state) {
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
    
}
