package de.instinct.engine.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.Engine;
import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.meta.data.MetaData;
import de.instinct.engine.core.meta.data.PauseData;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.data.PlayerData;
import de.instinct.engine.fleet.order.data.OrderData;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.ship.PlayerShip;
import de.instinct.engine.mining.entity.ship.PlayerShipProcessor;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine.mining.player.MiningPlayerProcessor;

public class MiningEngine extends Engine {
	
	private PlayerShipProcessor playerShipProcessor;
	private MiningPlayerProcessor playerProcessor;
	
	public MiningEngine() {
		super();
		playerShipProcessor = new PlayerShipProcessor();
		playerProcessor = new MiningPlayerProcessor();
	}

	@Override
	public void initialize(GameState state) {
		MiningGameState miningState = (MiningGameState) state;
		state.gameUUID = "test";
		state.started = true;
		
		state.metaData = new MetaData();
		state.metaData.pauseData = new PauseData();
		state.metaData.pauseData.resumeCountdownMS = 3000L;
		state.metaData.pauseData.teamPausesMS = new HashMap<>();
		state.metaData.pauseData.teamPausesMS.put(0, 0L);
		state.metaData.pauseData.teamPausesMS.put(1, 0L);
		state.metaData.pauseData.teamPausesMS.put(2, 0L);
		state.metaData.pauseData.teamPausesCount = new HashMap<>();
		state.metaData.pauseData.teamPausesCount.put(0, 0);
		state.metaData.pauseData.teamPausesCount.put(1, 3);
		state.metaData.pauseData.teamPausesCount.put(2, 3);
		state.metaData.pauseData.maxPauseMS = 60_000L;
		
		state.orderData = new OrderData();
		state.orderData.unprocessedOrders = new ConcurrentLinkedQueue<>();
		state.orderData.processedOrders = new ArrayList<>();
		
		state.playerData = new PlayerData();
		state.playerData.connectionStati = new ArrayList<>();
		state.playerData.players = new ArrayList<>();
		
		MiningPlayer player1 = new MiningPlayer();
		player1.id = 1;
		player1.teamId = 1;
		player1.name = "Player 1";
		state.playerData.players.add(player1);
		
		miningState.playerShips = new ArrayList<>();
		PlayerShip player1Ship = new PlayerShip();
		player1Ship.id = 0;
		player1Ship.ownerId = player1.id;
		player1Ship.radius = 20f;
		player1Ship.direction = new Vector2(0, 1);
		
		player1Ship.acceleration = 2f;
		player1Ship.maxSpeed = 20f;
		player1Ship.deceleration = 3f;
		player1Ship.maxReverseSpeed = -5f;
		player1Ship.rotationSpeed = 10f;
		player1Ship.laserSpeed = 10f;
		player1Ship.laserDamage = 10f;
		player1Ship.laserCooldownMS = 500;
		player1Ship.laserLifetimeMS = 2000;
		miningState.playerShips.add(player1Ship);
	}

	@Override
	protected void advanceStateTime(GameState state, long deltaTime) {
		MiningGameState miningState = (MiningGameState) state;
		playerShipProcessor.update(miningState, deltaTime);
		playerProcessor.update(miningState, deltaTime);
	}

	@Override
	protected boolean integrateOrder(GameState state, GameOrder order) {
		MiningGameState miningState = (MiningGameState) state;
		if (playerShipProcessor.integrateNewOrder(miningState, order)) return true;
		return false;
	}

}
