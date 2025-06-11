package de.instinct.engine.combat;

import de.instinct.engine.combat.projectile.ProjectileProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
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
        updatePlanetShields(state, deltaTime);
    }

    private void updatePlanetShields(GameState state, long progressionMS) {
        for (Planet planet : state.planets) {
            if (planet.defense != null) {
                planet.defense.currentShield += planet.defense.shieldRegenerationSpeed * ((float) progressionMS / 1000f);
                if (planet.defense.currentShield >= planet.defense.shield) {
                    planet.defense.currentShield = planet.defense.shield;
                }
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
