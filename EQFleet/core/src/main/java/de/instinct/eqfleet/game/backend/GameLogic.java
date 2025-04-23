package de.instinct.eqfleet.game.backend;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.audio.AudioManager;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialEngine;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialMode;
import de.instinct.eqfleet.menu.Menu;
import de.instinct.eqfleetshared.gamelogic.EventEngine;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.net.message.NetworkMessage;

public class GameLogic {
	
    private EventEngine engine;
    private GameClient gameClient;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> logicUpdateTask;
    private TutorialEngine tutorialEngine;
    private long BACKEND_UPDATE_CLOCK_MS = 20;
    
    private boolean oneMinutePlayed;
    
    public GameLogic() {
    	engine = new EventEngine();
    	gameClient = new GameClient();
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	tutorialEngine = new TutorialEngine();
    }
	
	private void update() {
		if (Game.activeGameState != null) {
    		long currentTime = System.currentTimeMillis();
        	engine.update(Game.activeGameState, currentTime - Game.lastUpdateTimestampMS);
        	Game.lastUpdateTimestampMS = currentTime;
        	if (!oneMinutePlayed && Game.activeGameState.gameTimeMS > Game.activeGameState.maxGameTimeMS - 61_000) {
        		AudioManager.playVoice("one_minute_remaining");
        		oneMinutePlayed = true;
        	}
    	}
	}
	
	public void update(GameState newGameState) {
		Game.activeGameState = newGameState;
		Game.lastUpdateTimestampMS = System.currentTimeMillis();
    	if (Game.activeGameState.winner != 0) {
    		Game.stop();
    	}
	}

	public void start() {
		oneMinutePlayed = false;
		AudioManager.play("neon_horizon", false);
		gameClient.start();
		logicUpdateTask = scheduler.scheduleAtFixedRate(() -> {
			NetworkMessage newMessage = Game.outputMessageQueue.next();
			if (newMessage != null) {
				gameClient.client.sendTCP(newMessage);
			}
			update();
		}, BACKEND_UPDATE_CLOCK_MS, BACKEND_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (logicUpdateTask != null && !logicUpdateTask.isCancelled()) {
			logicUpdateTask.cancel(true);
		}
		gameClient.stop();
		tutorialEngine.stop();
		scheduler.schedule(() -> {
			
			Game.activeGameState = null;
			Menu.activate();
			Game.active = false;
			
		}, 9000, TimeUnit.MILLISECONDS);
	}

	public void startTutorial(TutorialMode mode) {
		logicUpdateTask = scheduler.scheduleAtFixedRate(tutorialEngine.start(mode), BACKEND_UPDATE_CLOCK_MS, BACKEND_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
	}

	public void dispose() {
		gameClient.dispose();
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}

}
