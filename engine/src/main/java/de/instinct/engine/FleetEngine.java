package de.instinct.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.combat.CombatProcessor;
import de.instinct.engine.combat.unit.UnitManager;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.initialization.PlanetInitialization;
import de.instinct.engine.meta.MetaProcessor;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderValidator;
import de.instinct.engine.resource.ResourceProcessor;
import de.instinct.engine.util.EngineUtility;

public class FleetEngine {
	
	private final int UPDATE_INTERVAL_MS = 10;
	
	private OrderValidator orderValidator;
	private Queue<GameOrder> unprocessedOrders;
	
	private ResourceProcessor resourceProcessor;
	private CombatProcessor combatProcessor;
	private MetaProcessor metaProcessor;
	
	private long orderIdCounter;
	
	private boolean updateContainedValidOrder;
	
	public void initialize() {
		orderValidator = new OrderValidator();
		unprocessedOrders = new ConcurrentLinkedQueue<>();
		resourceProcessor = new ResourceProcessor();
		combatProcessor = new CombatProcessor();
		metaProcessor = new MetaProcessor();
		orderIdCounter = 0;
	}
	
	public GameState initializeGameState(GameStateInitialization initialization) {
		GameState state = new GameState();
		state.orders = new ArrayList<>();
		state.gameUUID = initialization.gameUUID;
		state.players = initializePlayers(initialization.players);
		state.connectionStati = generateConnectionStati(initialization.players);
		state.planets = generateInitialPlanets(initialization);
		state.ships = new ArrayList<>();
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

	private List<Planet> generateInitialPlanets(GameStateInitialization initialization) {
		List<Planet> initialPlanets = new ArrayList<>();
		for (PlanetInitialization init : initialization.map.planets) {
			Player planetOwner = EngineUtility.getPlayer(initialization.players, init.ownerId);
			Planet initialPlanet = UnitManager.createPlanet(planetOwner.planetData);
			initialPlanet.ownerId = init.ownerId;
			initialPlanet.position = init.position;
			initialPlanet.defense.currentArmor = initialPlanet.defense.armor * init.startArmorPercent;
			if (init.ancient) {
				initialPlanet.ancient = true;
				initialPlanet.weapon = null;
				initialPlanet.defense = null;
			}
			initialPlanets.add(initialPlanet);
		}
		return initialPlanets;
	}

	public void update(GameState state, long progressionMS) {
		try {
			if (state.started) {
				advanceTime(state, progressionMS);
			    workOnOrderQueue(state);
			    integrateNewOrders(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void integrateNewOrders(GameState state) {
		combatProcessor.integrateNewOrders(state);
		metaProcessor.integrateNewOrders(state);
	}

	private void advanceTime(GameState state, long progressionMS) {
		long remainingTime = progressionMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			metaProcessor.update(state, deltaTime);
			if (state.teamPause == 0 && state.resumeCountdownMS <= 0) {
				combatProcessor.update(state, deltaTime);
			    resourceProcessor.update(state, deltaTime);
			    state.gameTimeMS += deltaTime;
			}
		}
	}
	
	private void workOnOrderQueue(GameState state) {
		updateContainedValidOrder = false;
		while (!unprocessedOrders.isEmpty()) {
			GameOrder order = unprocessedOrders.poll();
			if (orderValidator.isValid(state, order)) {
				order.orderId = orderIdCounter++;
				state.orders.add(order);
				updateContainedValidOrder = true;
			}
		}
	}
	
	public void queue(GameState state, GameOrder order) {
		unprocessedOrders.add(order);
	}
	
	public boolean containedValidOrders() {
		return updateContainedValidOrder;
	}
	
}
