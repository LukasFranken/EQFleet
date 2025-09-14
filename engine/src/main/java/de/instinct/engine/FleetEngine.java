package de.instinct.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.combat.CombatProcessor;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.initialization.PlanetInitialization;
import de.instinct.engine.meta.MetaProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.planet.PlanetProcessor;
import de.instinct.engine.player.PlayerProcessor;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine.util.VictoryCalculator;

public class FleetEngine {
	
	private final int UPDATE_INTERVAL_MS = 10;
	
	private PlanetProcessor planetProcessor;
	private PlayerProcessor playerProcessor;
	private CombatProcessor combatProcessor;
	private MetaProcessor metaProcessor;
	
	private boolean updateContainedValidOrder;
	
	public void initialize() {
		planetProcessor = new PlanetProcessor();
		playerProcessor = new PlayerProcessor();
		combatProcessor = new CombatProcessor();
		metaProcessor = new MetaProcessor();
	}
	
	public GameState initializeGameState(GameStateInitialization initialization) {
		GameState state = new GameState();
		state.orders = new ArrayList<>();
		state.unprocessedOrders = new ConcurrentLinkedQueue<>();
		state.entityCounter = 0;
		state.orderCounter = 0;
		state.gameUUID = initialization.gameUUID;
		state.players = initializePlayers(initialization.players);
		state.connectionStati = generateConnectionStati(initialization.players);
		state.planets = generateInitialPlanets(initialization, state);
		state.zoomFactor = initialization.map.zoomFactor;
		state.ships = new ArrayList<>();
		state.turrets = new ArrayList<>();
		state.projectiles = new ArrayList<>();
		state.gameTimeMS = 0;
		state.maxGameTimeMS = initialization.gameTimeLimitMS;
		state.winner = 0;
		state.atpToWin = initialization.atpToWin;
		state.teamATPs = new HashMap<>();
		state.teamATPs.put(0, 0D);
		state.teamATPs.put(1, 0D);
		state.teamATPs.put(2, 0D);
		state.ancientPlanetResourceDegradationFactor = initialization.ancientPlanetResourceDegradationFactor;
		state.started = false;
		state.maxPauseMS = initialization.pauseTimeLimitMS;
		state.minPauseMS = 1000L;
		state.resumeCountdownMS = 3000L;
		state.teamPausesMS = new HashMap<>();
		state.teamPausesMS.put(0, 0L);
		state.teamPausesMS.put(1, 0L);
		state.teamPausesMS.put(2, 0L);
		state.teamPausesCount = new HashMap<>();
		state.teamPausesCount.put(0, 0);
		state.teamPausesCount.put(1, initialization.pauseCountLimit);
		state.teamPausesCount.put(2, initialization.pauseCountLimit);
		combatProcessor.initialize(state);
		return state;
	}
	
	private List<Player> initializePlayers(List<Player> players) {
		for (Player player : players) {
			player.currentCommandPoints = player.startCommandPoints;
		}
		return players;
	}

	private List<PlayerConnectionStatus> generateConnectionStati(List<Player> players) {
		List<PlayerConnectionStatus> connectionStati = new ArrayList<>();
		for (Player player : players) {
			PlayerConnectionStatus status = new PlayerConnectionStatus();
			status.playerId = player.id;
			connectionStati.add(status);
		}
		return connectionStati;
	}

	private List<Planet> generateInitialPlanets(GameStateInitialization initialization, GameState state) {
		List<Planet> initialPlanets = new ArrayList<>();
		for (PlanetInitialization init : initialization.map.planets) {
			Player planetOwner = EngineUtility.getPlayer(initialization.players, init.ownerId);
			Planet initialPlanet = planetProcessor.createPlanet(planetOwner.planetData, state);
			initialPlanet.ownerId = init.ownerId;
			initialPlanet.position = init.position;
			if (init.ancient) {
				initialPlanet.ancient = true;
			}
			initialPlanets.add(initialPlanet);
		}
		return initialPlanets;
	}

	public void update(GameState state, long progressionMS) {
		try {
			if (state.started) {
				advanceTime(state, progressionMS);
			    integrateNewOrders(state);
			    VictoryCalculator.checksVictory(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void integrateNewOrders(GameState state) {
		updateContainedValidOrder = false;
		while (!state.unprocessedOrders.isEmpty()) {
			GameOrder order = state.unprocessedOrders.poll();
			if (processOrder(state, order)) {
				order.orderId = state.orderCounter++;
				order.acceptedTimeMS = state.gameTimeMS;
				state.orders.add(order);
				updateContainedValidOrder = true;
			}
		}
	}

	private boolean processOrder(GameState state, GameOrder order) {
		if (combatProcessor.integrateNewOrder(state, order)) return true;
		if (metaProcessor.integrateNewOrder(state, order)) return true;
		return false;
	}

	private void advanceTime(GameState state, long progressionMS) {
		long remainingTime = progressionMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			metaProcessor.update(state, deltaTime);
			if (state.teamPause == 0 && state.resumeCountdownMS <= 0) {
				combatProcessor.update(state, deltaTime);
			    planetProcessor.update(state, deltaTime);
			    playerProcessor.update(state, deltaTime);
			    state.gameTimeMS += deltaTime;
			}
		}
	}
	
	public void queue(GameState state, GameOrder order) {
		state.unprocessedOrders.add(order);
	}
	
	public boolean containedValidOrders() {
		return updateContainedValidOrder;
	}
	
}
