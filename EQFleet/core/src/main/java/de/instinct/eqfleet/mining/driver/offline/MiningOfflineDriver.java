package de.instinct.eqfleet.mining.driver.offline;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.data.map.MiningMap;
import de.instinct.engine.mining.data.map.node.types.AsteroidMapNode;
import de.instinct.engine.mining.data.map.node.types.RecallAreaNode;
import de.instinct.engine.mining.entity.asteroid.ResourceType;
import de.instinct.engine.mining.entity.ship.data.MiningShipData;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine_api.mining.model.MiningGameStateInitialization;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.driver.MiningDriver;

public class MiningOfflineDriver extends MiningDriver {

	@Override
	public void initialize() {
		MiningModel.state = stateManager.initializeMining(getMiningGameStateInitialization());
		MiningModel.playerId = 1;
		MiningModel.state.metaData.started = true;
	}
	
	private MiningGameStateInitialization getMiningGameStateInitialization() {
		MiningGameStateInitialization initialization = new MiningGameStateInitialization();
		initialization.setMap(getOfflineMap());
		initialization.setGameUUID("custom");
		initialization.setPauseTimeLimitMS(60);
		initialization.setPauseCountLimit(3);
		
		initialization.setPlayers(new ArrayList<>());
		MiningPlayer player1 = getTestPlayer();
		player1.id = 1;
		initialization.getPlayers().add(player1);
		return initialization;
	}
	
	public MiningPlayer getTestPlayer() {
		MiningPlayer player = new MiningPlayer();
		player.shipData = new MiningShipData();
		player.shipData.radius = 10f;
		player.shipData.coreCharge = 20f;
		
		player.shipData.cargoCapacity = 20f;
		
		player.shipData.acceleration = 20f;
		player.shipData.deceleration = 10f;
		player.shipData.maxSpeed = 200f;
		player.shipData.maxSpeedReverse = -50f;
		player.shipData.rotationAcceleration = 10f;
		player.shipData.maxRotationSpeed = 40f;
		player.shipData.chargePerSecond = 0.2f;
		player.shipData.inertiaDampening = 50f;
		
		player.shipData.cooldownMS = 500;
		player.shipData.lifetimeMS = 2000;
		player.shipData.damage = 5f;
		player.shipData.projectileSpeed = 500f;
		player.shipData.chargePerShot = 0.5f;
		return player;
	}

	private MiningMap getOfflineMap() {
		MiningMap map = new MiningMap();
		map.nodes = new ArrayList<>();
		RecallAreaNode recallAreaNode = new RecallAreaNode();
		recallAreaNode.position = new Vector2(0, 0);
		recallAreaNode.radius = 40f;
		map.nodes.add(recallAreaNode);
		
		AsteroidMapNode asteroidNode = new AsteroidMapNode();
		asteroidNode.position = new Vector2(200, 0);
		asteroidNode.radius = 10f;
		asteroidNode.health = 5f;
		asteroidNode.resourceType = ResourceType.ALUMINUM;
		asteroidNode.value = 2f;
		
		map.nodes.add(asteroidNode);
		return map;
	}

	@Override
	public void updateDriver() {
		
	}
	
	@Override
	public void integrateOrder(GameOrder order) {
		stateManager.integrateOrder(MiningModel.state, order);
	}

	@Override
	public void dispose() {
		MiningModel.state = null;
		MiningModel.playerId = 0;
	}

}
