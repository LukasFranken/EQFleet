package de.instinct.engine.fleet;

import de.instinct.engine.core.Engine;
import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.planet.PlanetProcessor;
import de.instinct.engine.fleet.entity.projectile.FleetProjectileProcessor;
import de.instinct.engine.fleet.entity.unit.ship.ShipProcessor;
import de.instinct.engine.fleet.entity.unit.turret.TurretProcessor;
import de.instinct.engine.fleet.order.processors.CombatOrderProcessor;
import de.instinct.engine.fleet.order.processors.FleetMetaOrderProcessor;
import de.instinct.engine.fleet.player.FleetPlayerProcessor;
import de.instinct.engine.fleet.util.VictoryCalculator;

public class FleetEngine extends Engine {
	
	private FleetPlayerProcessor playerProcessor;
	private PlanetProcessor planetProcessor;
	private CombatOrderProcessor combatOrderProcessor;
	private FleetMetaOrderProcessor metaOrderProcessor;
	private TurretProcessor turretProcessor;
	private ShipProcessor shipProcessor;
	private FleetProjectileProcessor projectileProcessor;
	private VictoryCalculator victoryCalculator;
	
	public FleetEngine() {
		super();
		playerProcessor = new FleetPlayerProcessor();
		planetProcessor = new PlanetProcessor();
		combatOrderProcessor = new CombatOrderProcessor();
		metaOrderProcessor = new FleetMetaOrderProcessor();
		turretProcessor = new TurretProcessor();
		shipProcessor = new ShipProcessor();
		projectileProcessor = new FleetProjectileProcessor();
		victoryCalculator = new VictoryCalculator();
	}
	
	@Override
	public void initialize(GameState state) {
		FleetGameState fleetState = (FleetGameState) state;
		playerProcessor.initialize(fleetState);
		planetProcessor.initialize(fleetState);
	}
	
	@Override
	protected boolean integrateOrder(GameState state, GameOrder order) {
		FleetGameState fleetState = (FleetGameState) state;
		if (combatOrderProcessor.integrateNewOrder(fleetState, order)) return true;
		if (metaOrderProcessor.integrateNewFleetOrder(fleetState, order)) return true;
		return false;
	}
	
	@Override
	protected void advanceStateTime(GameState state, long deltaTime) {
		FleetGameState fleetState = (FleetGameState) state;
		if (fleetState.resultData.winner == 0) {
			turretProcessor.updateTurrets(fleetState, deltaTime);
			shipProcessor.updateShips(fleetState, deltaTime);
			projectileProcessor.updateFleetProjectiles(fleetState, deltaTime);
	        planetProcessor.update(fleetState, deltaTime);
	        playerProcessor.update(fleetState, deltaTime);
	        victoryCalculator.checkVictory(fleetState);
		}
	}
	
}
