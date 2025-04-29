package de.instinct.eqfleet.menu.module.main.tab.play;

import com.badlogic.gdx.utils.Timer;

import de.instinct.api.core.API;
import de.instinct.api.matchmaking.dto.InvitesStatusResponse;
import de.instinct.api.matchmaking.dto.LobbyStatusCode;
import de.instinct.api.matchmaking.dto.LobbyStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponseCode;
import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.module.main.MainMenu;
import de.instinct.eqfleet.net.WebManager;

public class PlayTab {
	
	private static PlayTabRenderer renderer;
	private static PlayTabLogic logic;
	
	public static String lobbyUUID;
	public static LobbyStatusResponse lobbyStatus;
	public static MatchmakingStatusResponse currentMatchmakingStatus;
	public static InvitesStatusResponse inviteStatus;
	
	private static float QUEUE_UPDATE_CLOCK_MS = 200;

	public static void init() {
		renderer = new PlayTabRenderer();
		renderer.init();
		logic = new PlayTabLogic();
		logic.init();
	}
	
	public static void update() {
		logic.update();
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}
	
	private static void connectToGameserver() {
		System.out.println("starting game for: " + currentMatchmakingStatus);
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

	public static void loadData() {
		Timer.schedule(new Timer.Task() {
        	
            @Override
            public void run() {
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
        			if (lobbyStatus != null && lobbyStatus.getCode() == LobbyStatusCode.MATCHING) {
        				WebManager.enqueue(
        	        			() -> API.matchmaking().matchmaking(lobbyUUID),
        					    result -> {
        					    	currentMatchmakingStatus = result;
        					    	if (result.getCode() == MatchmakingStatusResponseCode.READY) {
        					    		connectToGameserver();
        					    	}
        					    }
        				);
        			}
        		}
            	if (MainMenu.currentTab != MenuTab.PLAY) {
            		this.cancel();
            	}
            }
            
        }, 0, QUEUE_UPDATE_CLOCK_MS / 1000f);
	}
	
}
