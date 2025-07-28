package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.util.EngineUtility;

public class CombatProcessor {

    private ShipProcessor shipProcessor;
    private PlanetCombatProcessor planetProcessor;
    private ProjectileProcessor projectileProcessor;

    public CombatProcessor() {
        shipProcessor = new ShipProcessor();
        planetProcessor = new PlanetCombatProcessor();
        projectileProcessor = new ProjectileProcessor();
    }

    public void update(GameState state, long deltaTime) {
    	planetProcessor.updatePlanets(state, deltaTime);
    	shipProcessor.updateShips(state, deltaTime);
        projectileProcessor.updateProjectiles(state, deltaTime);
        updateShields(state, deltaTime);
    }

    private void updateShields(GameState state, long progressionMS) {
    	for (Ship ship : state.ships) {
			updateShield(ship.defense, progressionMS);
		}
        for (Planet planet : state.planets) {
        	updateShield(planet.defense, progressionMS);
        }
    }

    private void updateShield(Defense defense, long progressionMS) {
    	if (defense != null) {
			defense.currentShield += defense.shieldRegenerationSpeed * ((float) progressionMS / 1000f);
			if (defense.currentShield >= defense.shield) {
				defense.currentShield = defense.shield;
			}
		}
	}

	public void integrateNewOrders(GameState state) {
        for (GameOrder order : state.orders) {
            if (!order.processed) {
                if (order instanceof ShipMovementOrder) {
                    ShipMovementOrder movement = (ShipMovementOrder) order;

                    Ship newShip = shipProcessor.createShipInstance(movement, state);
                    state.ships.add(newShip);

                    Planet fromPlanet = EngineUtility.getPlanet(state.planets, movement.fromPlanetId);
                    fromPlanet.currentResources -= newShip.cost;
                    order.processed = true;
                }
            }
        }
    }
    
}
