package de.instinct.eqfleet.menu.module.main.tab.play;

import com.badlogic.gdx.utils.Timer;

import de.instinct.api.core.API;
import de.instinct.api.matchmaking.dto.MatchmakingRegistrationResponse;
import de.instinct.api.matchmaking.dto.MatchmakingRegistrationResponseCode;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponseCode;
import de.instinct.eqfleet.net.WebManager;

public class PlayTab {
	
	private static PlayTabRenderer renderer;
	private static PlayTabLogic logic;
	public static MatchmakingStatusResponse currentMatchmakingStatus;
	
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

	public static void processMatchmakingResponse(MatchmakingRegistrationResponse matchmakingRegistrationResponse) {
		System.out.println(matchmakingRegistrationResponse);
		if (matchmakingRegistrationResponse.getCode() == MatchmakingRegistrationResponseCode.SUCCESS) {
			startQueueUpdateLoop(matchmakingRegistrationResponse);
		}
	}

	private static void startQueueUpdateLoop(MatchmakingRegistrationResponse matchmakingRegistrationResponse) {
		Timer.schedule(new Timer.Task() {
        	
            @Override
            public void run() {
            	WebManager.enqueue(
            			() -> API.matchmaking().status(matchmakingRegistrationResponse.getLobbyUUID()),
					    result -> {
					    	System.out.println(result);
					    	if (result.getCode() == MatchmakingStatusResponseCode.READY) {
					    		connectToGameserver();
					    		currentMatchmakingStatus = null;
					    		this.cancel();
					    	} else {
					    		currentMatchmakingStatus = result;
					    	}
					    }
				);
            }
            
        }, 0, QUEUE_UPDATE_CLOCK_MS / 1000f);
	}
	
	private static void connectToGameserver() {
		System.out.println("starting game");
	}
	
}
