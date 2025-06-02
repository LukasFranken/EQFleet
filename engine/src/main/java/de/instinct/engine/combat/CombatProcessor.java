package de.instinct.engine.combat;

import java.util.ArrayList;
import java.util.Iterator;

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

    public void update(GameState state, long progressionMS) {
        for (Combat combat : state.activeCombats) {
            updateCombat(combat, state, progressionMS);
        }
        processFinishedCombats(state);
        updatePlanetShields(state, progressionMS);
    }

    private void updateCombat(Combat combat, GameState state, long deltaTime) {
        planetProcessor.updatePlanets(combat, state, deltaTime);
        shipProcessor.updateShips(combat, state, deltaTime);
        projectileProcessor.updateProjectiles(state, combat, deltaTime);
    }

    private void processFinishedCombats(GameState state) {
        Iterator<Combat> iterator = state.activeCombats.iterator();
        while (iterator.hasNext()) {
            Combat combat = iterator.next();
            if (isCombatExpired(state, combat)) {
            	planetProcessor.resetPlanetCooldowns(state, combat);
                state.finishedCombats.add(combat);
                iterator.remove();
            }
        }
    }

    private boolean isCombatExpired(GameState state, Combat combat) {
        return combat.ships.isEmpty() && combat.projectiles.isEmpty();
    }

    private void updatePlanetShields(GameState state, long progressionMS) {
        for (Planet planet : state.planets) {
            if (planet.defense != null) {
                planet.currentShield += planet.defense.shieldRegenerationSpeed * ((float) progressionMS / 1000f);
                if (planet.currentShield >= planet.defense.shield) {
                    planet.currentShield = planet.defense.shield;
                }
            }
        }
    }

    public void integrateNewOrders(GameState state) {
        for (GameOrder order : state.orders) {
            if (!order.processed) {
                if (order instanceof ShipMovementOrder) {
                    ShipMovementOrder movement = (ShipMovementOrder) order;
                    Combat combat = getCombat(movement, state);
                    if (combat == null) {
                        combat = createCombat(movement, state);
                    }
                    Ship newShip = shipProcessor.createShipInstance(movement, state);
                    combat.ships.add(newShip);

                    Planet fromPlanet = EngineUtility.getPlanet(state.planets, movement.fromPlanetId);
                    fromPlanet.currentResources -= newShip.cost;
                    order.processed = true;
                }
            }
        }
    }

    private Combat getCombat(ShipMovementOrder movement, GameState state) {
        for (Combat combat : state.activeCombats) {
            if (combat.planetIds.contains(movement.fromPlanetId) && combat.planetIds.contains(movement.toPlanetId)) {
                return combat;
            }
        }
        return null;
    }

    private Combat createCombat(ShipMovementOrder movement, GameState state) {
        Combat newCombat = new Combat();
        newCombat.planetIds = new ArrayList<>();
        newCombat.planetIds.add(movement.fromPlanetId);
        newCombat.planetIds.add(movement.toPlanetId);
        newCombat.ships = new ArrayList<>();
        newCombat.projectiles = new ArrayList<>();
        newCombat.startTime = state.gameTimeMS;
        state.activeCombats.add(newCombat);
        return newCombat;
    }
}
