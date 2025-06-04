package de.instinct.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.engine.combat.CombatProcessor;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.initialization.GameStateInitialization;
import de.instinct.engine.initialization.PlanetInitialization;
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
	
	private long orderIdCounter;
	
	public void initialize() {
		orderValidator = new OrderValidator();
		unprocessedOrders = new ConcurrentLinkedQueue<>();
		resourceProcessor = new ResourceProcessor();
		combatProcessor = new CombatProcessor();
		orderIdCounter = 0;
	}
	
	public GameState initializeGameState(GameStateInitialization initialization) {
		GameState state = new GameState();
		state.orders = new ArrayList<>();
		state.gameUUID = UUID.randomUUID().toString();
		state.players = initializePlayers(initialization.players);
		state.connectionStati = generateConnectionStati(initialization.players);
		state.planets = generateInitialPlanets(initialization);
		state.gameTimeMS = 0;
		state.maxGameTimeMS = initialization.gameTimeLimitMS;
		state.winner = 0;
		state.atpToWin = initialization.atpToWin;
		state.teamATPs = new HashMap<>();
		state.teamATPs.put(0, 0D);
		state.teamATPs.put(1, 0D);
		state.teamATPs.put(2, 0D);
		state.ancientPlanetResourceDegradationFactor = initialization.ancientPlanetResourceDegradationFactor;
		state.activeCombats = new ArrayList<>();
		state.finishedCombats = new ArrayList<>();
		state.started = false;
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
			Planet initialPlanet = EntityManager.createPlanet(planetOwner.planetData);
			initialPlanet.ownerId = init.ownerId;
			initialPlanet.position = init.position;
			initialPlanet.currentArmor = initialPlanet.defense.armor * init.startArmorPercent;
			if (init.ancient) {
				initialPlanet.ancient = true;
				initialPlanet.currentShield = 0;
				initialPlanet.currentArmor = 0;
				initialPlanet.weapon = null;
				initialPlanet.defense = null;
			}
			initialPlanets.add(initialPlanet);
		}
		return initialPlanets;
	}

	public boolean update(GameState state, long progressionMS) {
		if (state.started) {
			long targetTime = state.gameTimeMS + progressionMS;
			advanceTime(state, progressionMS);
		    state.gameTimeMS = targetTime;
		    
		    boolean newOrder = workOnOrderQueue(state);
		    integrateNewOrders(state);
		    return newOrder;
		}
		return false;
	}
	
	private void integrateNewOrders(GameState state) {
		combatProcessor.integrateNewOrders(state);
	}

	private void advanceTime(GameState state, long deltaMS) {
		long remainingTime = deltaMS;
		while (remainingTime > 0) {
			long deltaTime = Math.min(UPDATE_INTERVAL_MS, remainingTime);
			remainingTime -= deltaTime;
			combatProcessor.update(state, deltaTime);
		    resourceProcessor.update(state, deltaTime);
		    state.gameTimeMS += deltaTime;
		}
	}
	
	private boolean workOnOrderQueue(GameState state) {
		boolean containedValidOrder = false;
		while (!unprocessedOrders.isEmpty()) {
			GameOrder order = unprocessedOrders.poll();
			if (orderValidator.isValid(state, order)) {
				order.orderId = orderIdCounter++;
				state.orders.add(order);
				containedValidOrder = true;
			}
		}
		return containedValidOrder;
	}
	
	public void queue(GameState state, GameOrder order) {
		unprocessedOrders.add(order);
	}

}
