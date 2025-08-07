package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.ShipMovementOrder;

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

	public void integrateNewOrders(GameState state) {
        for (GameOrder order : state.orders) {
            if (!order.processed) {
                if (order instanceof ShipMovementOrder) {
                    ShipMovementOrder movement = (ShipMovementOrder) order;
                    shipProcessor.createShipInstance(movement, state);
                    movement.processed = true;
                }
                if (order instanceof BuildTurretOrder) {
                	BuildTurretOrder buildOrder = (BuildTurretOrder) order;
                	turretProcessor.createTurretInstance(buildOrder.planetId, state, true);
                    buildOrder.processed = true;
                }
            }
        }
    }
    
}
