package de.instinct.eqfleet.mining;

import java.util.ArrayList;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.MiningEngine;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.MiningPlayerShipProcessor;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine_api.mining.MiningGameStateInitializer;
import de.instinct.engine_api.mining.model.MiningGameStateInitialization;
import de.instinct.engine_api.mining.model.MiningMap;

public class MiningEngineAPI {
	
	private static MiningEngine engine = new MiningEngine();
	private static MiningGameStateInitializer initializer = new MiningGameStateInitializer();

	public static void initialize() {
		MiningModel.state = initializer.initializeMining(getMiningGameStateInitialization());
		MiningModel.playerId = 1;
	}

	private static MiningGameStateInitialization getMiningGameStateInitialization() {
		MiningGameStateInitialization initialization = new MiningGameStateInitialization();
		initialization.setMap(new MiningMap());
		initialization.setGameUUID("custom");
		initialization.setPauseTimeLimitMS(60);
		initialization.setPauseCountLimit(3);
		
		initialization.setPlayers(new ArrayList<>());
		MiningPlayer player1 = new MiningPlayer();
		player1.id = 1;
		player1.teamId = 1;
		player1.name = "Player 1";
		initialization.getPlayers().add(player1);
		MiningPlayer player2 = new MiningPlayer();
		player2.id = 2;
		player2.teamId = 1;
		player2.name = "Player 2";
		initialization.getPlayers().add(player2);
		return initialization;
	}

	public static void update(MiningGameState gameState, long progressionMS) {
		engine.update(gameState, progressionMS);
	}
	
	public static boolean addOrder(MiningGameState gameState, GameOrder order) {
		return gameState.orderData.unprocessedOrders.add(order);
	}

	public static MiningPlayerShip getShip(int playerId) {
		return MiningPlayerShipProcessor.getPlayerShip(MiningModel.state, playerId);
	}
	
	public static boolean shipIsRecallable(MiningPlayerShip ship) {
		return MiningPlayerShipProcessor.shipIsInRecallArea(MiningModel.state, ship) 
				&& ship.speed <= 0.1f && ship.speed >= -0.1f 
				&& MiningModel.state.metaData.pauseData.resumeCountdownMS <= 0;
	}

}
