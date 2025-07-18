package de.instinct.eqfleet.game.backend.driver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.engine.FleetEngine;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;

public abstract class Driver {
	
	protected FleetEngine engine;
	
	private ScheduledExecutorService scheduler;
	
	public Driver() {
		engine = new FleetEngine();
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public void start() {
		engine.initialize();
		setup();
	}
	
	public void update() {
		preEngineUpdate();
		updateEngine();
		postEngineUpdate();
	}

	public abstract void setup();
	
	protected abstract void preEngineUpdate();
	
	protected void updateEngine() {
		if (GameModel.activeGameState != null && GameModel.activeGameState.started) {
    		long currentTime = System.currentTimeMillis();
        	engine.update(GameModel.activeGameState, currentTime - GameModel.lastUpdateTimestampMS);
        	GameModel.lastUpdateTimestampMS = currentTime;
    	}
	}
	
	protected abstract void postEngineUpdate();
	
	public void stop() {
		finish();
		scheduler.schedule(() -> {
			
			GameModel.activeGameState = null;
			if (PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) {
				Play.leaveLobby();
				Menu.openModule(MenuModule.STARMAP);
			}
			Menu.open();
			GameModel.active = false;
			
			
		}, 3000, TimeUnit.MILLISECONDS);
	}
	
	public abstract void finish();
	
	public void dispose() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		cleanup();
	}
	
	public abstract void cleanup();
	
}
