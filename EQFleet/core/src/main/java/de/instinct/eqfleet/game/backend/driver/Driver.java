package de.instinct.eqfleet.game.backend.driver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.engine.FleetEngine;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.main.Menu;
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
		if (GameModel.activeGameState != null && GameModel.activeGameState.started && !GameModel.paused) {
    		long currentTime = System.currentTimeMillis();
        	engine.update(GameModel.activeGameState, currentTime - GameModel.lastUpdateTimestampMS);
        	GameModel.lastUpdateTimestampMS = currentTime;
    	}
	}
	
	protected abstract void postEngineUpdate();
	
	public void stop() {
		AudioManager.stopAllSfx();
		AudioManager.stopAllVoices();
		if (GameModel.activeGameState.winner == EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId).teamId) {
			if (EngineUtility.winIsWiped(GameModel.activeGameState)) {
				AudioManager.playVoice("domination");
			} else {
				AudioManager.playVoice("victory");
			}
		} else {
			if (GameModel.activeGameState.surrendered != 0) {
				AudioManager.playVoice("surrender");
			} else {
				if (EngineUtility.winIsWiped(GameModel.activeGameState)) {
					AudioManager.playVoice("wiped_out");
				} else {
					AudioManager.playVoice("defeat");
				}
			}
		}
		
		scheduler.schedule(() -> {
			
			try {
				GameModel.active = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}, 2000, TimeUnit.MILLISECONDS);
		
		long delay = finish();
		scheduler.schedule(() -> {
			
			try {
				if (PlayModel.lobbyStatus != null && PlayModel.lobbyStatus.getType() != null && PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) {
					Menu.openModule(MenuModule.STARMAP);
				}
				Game.dispose();
				Menu.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}, delay, TimeUnit.MILLISECONDS);
	}
	
	public abstract long finish();
	
	public void dispose() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		cleanup();
		GameModel.lastGameUUID = GameModel.activeGameState != null ? GameModel.activeGameState.gameUUID : null;
		GameModel.activeGameState = null;
	}
	
	public abstract void cleanup();
	
}
