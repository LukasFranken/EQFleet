package de.instinct.eqfleet.game;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;

import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.eqfleet.game.backend.driver.Driver;
import de.instinct.eqfleet.game.backend.driver.local.custom.CustomDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialDriver;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialMode;
import de.instinct.eqfleet.game.backend.driver.online.OnlineDriver;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.net.MessageQueue;

public class Game {

    private static GameRenderer renderer;
    
    private static Driver currentDriver;

    public static void init() {
    	GameModel.outputMessageQueue = new MessageQueue<>();
    	GameModel.inputMessageQueue = new MessageQueue<>();
    	GameModel.receivedGameState = new ConcurrentLinkedQueue<>();
    	GameModel.receivedOrders = new ConcurrentLinkedQueue<>();
    	renderer = new GameRenderer();
    }
    
    public static void start() {
    	currentDriver = new OnlineDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
    	GameModel.inputEnabled = true;
    	GameModel.visible = true;
    }
    
    public static void startTutorial(TutorialMode mode) {
    	currentDriver = new TutorialDriver();
		Gdx.app.postRunnable(() -> {
			renderer.init();
		});
		GameModel.active = true;
		((TutorialDriver)currentDriver).setMode(mode);
		currentDriver.start();
		GameModel.visible = false;
	}
    
    public static void startCustom() {
    	currentDriver = new CustomDriver();
    	renderer.init();
    	GameModel.active = true;
    	currentDriver.start();
		GameModel.inputEnabled = true;
		GameModel.visible = true;
    }

    public static void stop() {
    	currentDriver.stop();
    	GameModel.inputEnabled = false;
	}

    public static void render() {
        if (GameModel.active) {
            currentDriver.update();
            if (GameModel.activeGameState != null && GameModel.visible) {
                renderer.render(GameModel.activeGameState);
            }
        }
    }

	public static void dispose() {
		GameModel.visible = false;
		GameModel.active = false;
		if (renderer != null) {
			renderer.dispose();
		}
		if (currentDriver != null) {
			currentDriver.dispose();
		}
		
		try {
			Menu.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void assignPlayer(PlayerAssigned playerAssigned) {
		GameModel.playerId = playerAssigned.playerId;
        Logger.log("GAME", "Assigned player ID: " + GameModel.playerId, ConsoleColor.GRAY);
	}

	public static void pause() {
		GameModel.paused = true;
	}
	
	public static void unpause() {
		GameModel.paused = false;
		GameModel.lastUpdateTimestampMS = System.currentTimeMillis();
	}

	public static void setUIElementVisible(String tag, boolean visible) {
		renderer.setUIElementVisible(tag, visible);
	}

}
