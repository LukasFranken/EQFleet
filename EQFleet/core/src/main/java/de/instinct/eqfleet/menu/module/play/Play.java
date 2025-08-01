package de.instinct.eqfleet.menu.module.play;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.dto.LobbyStatusCode;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponseCode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.play.message.UpdateLobbyStatusMessage;
import de.instinct.eqfleet.net.WebManager;

public class Play extends BaseModule {
	
	private static ScheduledExecutorService scheduler;
	private static int QUEUE_UPDATE_CLOCK_MS = 500;

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.PLAY;
	}

	@Override
	public void init() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		startUpdateCycle();
	}

	@Override
	public void open() {
		WebManager.enqueue(
    			() -> API.matchmaking().get(),
			    result -> {
			    	PlayModel.lobbyUUID = result;
			    }
		);
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof UpdateLobbyStatusMessage) {
			updateLobby();
			return true;
		}
		return false;
	}
	
	private static void connectToGameserver() {
		Gdx.app.postRunnable(() -> {
		    Game.start();
		    Menu.close();
		  });
	}

	public static void startMatchmaking() {
		WebManager.enqueue(
			    () -> API.matchmaking().start(PlayModel.lobbyUUID),
			    result -> {
			    	updateLobby();
			    }
		);
	}

	public static void createLobby() {
		WebManager.enqueue(
			    () -> API.matchmaking().create(),
			    result -> {
			    	PlayModel.lobbyUUID = result.getLobbyUUID();
			    }
		);
	}
	
	public static void leaveLobby() {
		WebManager.enqueue(
			    () -> API.matchmaking().leave(),
			    result -> {
			    	PlayModel.lobbyUUID = null;
			    	PlayModel.lobbyStatus = null;
			    }
		);
	}
	
	public static void accept(String value) {
		WebManager.enqueue(
			    () -> API.matchmaking().accept(value),
			    result -> {
			    	
			    }
		);
	}
	
	public static void decline(String value) {
		WebManager.enqueue(
			    () -> API.matchmaking().decline(value),
			    result -> {}
		);
	}
	
	public static void setType(GameType type) {
		WebManager.enqueue(
			    () -> API.matchmaking().settype(PlayModel.lobbyUUID, type),
			    result -> {}
		);
	}
	
	public static void resetType() {
		WebManager.enqueue(
			    () -> API.matchmaking().resettype(PlayModel.lobbyUUID),
			    result -> {}
		);
	}
	
	public static void invite(String username) {
		WebManager.enqueue(
			    () -> API.matchmaking().invite(username),
			    result -> {
			    	PlayModel.inviteResponse = result;
			    }
		);
	}
	
	public static void stopMatching() {
		WebManager.enqueue(
			    () -> API.matchmaking().stop(PlayModel.lobbyUUID),
			    result -> {}
		);
	}
	
	public static void updateLobby() {
		WebManager.enqueue(
    			() -> API.matchmaking().get(),
			    resultUUID -> {
			    	if (resultUUID != null && !resultUUID.contentEquals("")) {
			    		PlayModel.lobbyUUID = resultUUID;
			    		WebManager.enqueue(
	                			() -> API.matchmaking().status(PlayModel.lobbyUUID),
	    					    resultStatus -> {
	    					    	PlayModel.lobbyStatus = resultStatus;
	    					    }
	    				);
					}
			    }
		);
	}

	public static void startUpdateCycle() {
		PlayModel.lobbyUUID = null;
		PlayModel.lobbyStatus = null;
		PlayModel.currentMatchmakingStatus = null;
		scheduler.scheduleAtFixedRate(() -> {
			try {
				if (MenuModel.active) {
					updateLobby();
					if (PlayModel.lobbyUUID == null || PlayModel.lobbyUUID.isEmpty()) {
		        		WebManager.enqueue(
		            			() -> API.matchmaking().invites(),
		    				    result -> {
		    				    	PlayModel.inviteStatus = result;
		    				    }
		        		);
		    		} else {
		    			if (PlayModel.lobbyStatus != null && (PlayModel.lobbyStatus.getCode() == LobbyStatusCode.MATCHING || PlayModel.lobbyStatus.getCode() == LobbyStatusCode.IN_GAME)) {
		    				WebManager.enqueue(
		    	        			() -> API.matchmaking().matchmaking(PlayModel.lobbyUUID),
		    					    result -> {
		    					    	PlayModel.currentMatchmakingStatus = result;
		    					    	if (result.getCode() == MatchmakingStatusResponseCode.READY) {
		    					    		connectToGameserver();
		    					    	}
		    					    }
		    				);
		    			}
		    		}
				}
			} catch (Exception e) {
				System.out.println("Play routine failed");
				e.printStackTrace();
			}
		}, 0, QUEUE_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
	}

	@Override
	public void close() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}

}
