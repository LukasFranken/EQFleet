package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;

public class CombatProcessor {
    
    public static void initialize(GameState state) {
    	
	}

    public static void update(GameState state, long deltaTime) {
    	TurretProcessor.updateTurrets(state, deltaTime);
    	ShipProcessor.updateShips(state, deltaTime);
        ProjectileProcessor.updateProjectiles(state, deltaTime);
    }

	public static boolean integrateNewOrder(GameState state, GameOrder order) {
		if (order instanceof ShipMovementOrder) {
            ShipMovementOrder shipMovementOrder = (ShipMovementOrder) order;
            if (CombatOrderValidator.isValid(shipMovementOrder, state)) {
            	ShipProcessor.createShipInstance(shipMovementOrder, state);
            	return true;
            }
        }
        if (order instanceof BuildTurretOrder) {
        	BuildTurretOrder buildTurretOrder = (BuildTurretOrder) order;
        	if (CombatOrderValidator.isValid(buildTurretOrder, state)) {
        		TurretProcessor.createTurret(buildTurretOrder.planetId, buildTurretOrder.turretId, state, true);
        		return true;
        	}
        }
        return false;
    }
    
}
