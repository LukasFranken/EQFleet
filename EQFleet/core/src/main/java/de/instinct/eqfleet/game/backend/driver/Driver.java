package de.instinct.eqfleet.game.backend.driver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.instinct.engine.FleetEngine;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.GameModel;

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
		if (!GameModel.activeGameState.gameUUID.equals("tutorial")) {
			if (GameModel.activeGameState.resultData.winner == EngineUtility.getPlayer(GameModel.activeGameState.staticData.playerData.players, GameModel.playerId).teamId) {
				if (GameModel.activeGameState.resultData.wiped) {
					AudioManager.playVoice("game", "domination");
				} else {
					AudioManager.playVoice("game", "victory");
				}
			} else {
				if (GameModel.activeGameState.resultData.surrendered != 0) {
					AudioManager.playVoice("game", "surrender");
				} else {
					if (GameModel.activeGameState.resultData.wiped) {
						AudioManager.playVoice("game", "wiped_out");
					} else {
						AudioManager.playVoice("game", "defeat");
					}
				}
			}
		}
		
		long delay = finish();
		
        scheduler.schedule(() -> {
			
			try {
				Game.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}, delay, TimeUnit.MILLISECONDS);
	}
	
	public abstract long finish();
	
	public void dispose() {
		GameModel.lastGameUUID = GameModel.activeGameState != null ? GameModel.activeGameState.gameUUID : null;
		GameModel.activeGameState = null;
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		cleanup();
	}
	
	public abstract void cleanup();
	
}
