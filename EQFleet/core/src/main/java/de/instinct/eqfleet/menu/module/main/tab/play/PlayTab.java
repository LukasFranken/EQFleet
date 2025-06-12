package de.instinct.eqfleet.menu.module.main.tab.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.dto.InviteResponse;
import de.instinct.api.matchmaking.dto.InvitesStatusResponse;
import de.instinct.api.matchmaking.dto.LobbyStatusCode;
import de.instinct.api.matchmaking.dto.LobbyStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponseCode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.net.WebManager;

public class PlayTab {
	
	public static String lobbyUUID;
	public static LobbyStatusResponse lobbyStatus;
	public static MatchmakingStatusResponse currentMatchmakingStatus;
	public static InvitesStatusResponse inviteStatus;
	public static InviteResponse inviteResponse;
	
	private static Task updateTask;
	
	private static float QUEUE_UPDATE_CLOCK_MS = 1000;
	
	public static void update() {
		
	}
	
	public static void render() {
		
	}
	
	public static void dispose() {
		
	}
	
	private static void connectToGameserver() {
		Gdx.app.postRunnable(() -> {
		    Game.start();
		    Menu.close();
		  });
	}

	public static void startMatchmaking() {
		WebManager.enqueue(
			    () -> API.matchmaking().start(lobbyUUID),
			    result -> {}
		);
	}

	public static void createLobby() {
		WebManager.enqueue(
			    () -> API.matchmaking().create(),
			    result -> {
			    	lobbyUUID = result.getLobbyUUID();
			    }
		);
	}
	
	public static void leaveLobby() {
		WebManager.enqueue(
			    () -> API.matchmaking().leave(),
			    result -> {
			    	lobbyUUID = null;
			    }
		);
	}
	
	public static void accept(String value) {
		WebManager.enqueue(
			    () -> API.matchmaking().accept(value),
			    result -> {}
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
			    () -> API.matchmaking().settype(lobbyUUID, type),
			    result -> {}
		);
	}
	
	public static void resetType() {
		WebManager.enqueue(
			    () -> API.matchmaking().resettype(lobbyUUID),
			    result -> {}
		);
	}
	
	public static void invite(String username) {
		WebManager.enqueue(
			    () -> API.matchmaking().invite(username),
			    result -> {
			    	inviteResponse = result;
			    }
		);
	}

	public static void loadData() {
		lobbyUUID = null;
		lobbyStatus = null;
		currentMatchmakingStatus = null;
		if (updateTask == null) {
			updateTask = Timer.schedule(new Timer.Task() {
	        	
	            @Override
	            public void run() {
	            	if (MenuModel.activeModule == MenuModule.PLAY && MenuModel.active) {
	            		if (lobbyUUID == null || lobbyUUID.contentEquals("")) {
	                		WebManager.enqueue(
	                    			() -> API.matchmaking().get(),
	            				    result -> {
	            				    	lobbyUUID = result;
	            				    }
	            			);
	                		WebManager.enqueue(
	                    			() -> API.matchmaking().invites(),
	            				    result -> {
	            				    	inviteStatus = result;
	            				    }
	            			);
	            		} else {
	            			WebManager.enqueue(
	                    			() -> API.matchmaking().status(lobbyUUID),
	        					    result -> {
	        					    	lobbyStatus = result;
	        					    }
	        				);
	            			if (lobbyStatus != null && (lobbyStatus.getCode() == LobbyStatusCode.MATCHING || lobbyStatus.getCode() == LobbyStatusCode.IN_GAME)) {
	            				WebManager.enqueue(
	            	        			() -> API.matchmaking().matchmaking(lobbyUUID),
	            					    result -> {
	            					    	currentMatchmakingStatus = result;
	            					    	System.out.println("Matchmaking status: " + currentMatchmakingStatus);
	            					    	if (result.getCode() == MatchmakingStatusResponseCode.READY) {
	            					    		connectToGameserver();
	            					    	}
	            					    }
	            				);
	            			}
	            		}
	            	}
	            }
	            
	        }, 0, QUEUE_UPDATE_CLOCK_MS / 1000f);
		}
	}
	
}
