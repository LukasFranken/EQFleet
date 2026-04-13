package de.instinct.engine.core.meta;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.order.types.GamePauseOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.player.PlayerProcessor;

public class MetaOrderProcessor {
	
	protected PlayerProcessor playerProcessor;
	
	public MetaOrderProcessor() {
		playerProcessor = new PlayerProcessor();
	}
	
	public boolean integrateNewOrder(GameState state, GameOrder order) {	
		if (order instanceof GamePauseOrder) {
        	GamePauseOrder gamePauseOrder = (GamePauseOrder)order;
        	if (isValid(gamePauseOrder, state)) {
        		Player player = playerProcessor.getPlayer(state.playerData.players, gamePauseOrder.playerId);
        		if (gamePauseOrder.pause) {
        			state.metaData.pauseData.teamPause = player.teamId;
        			state.metaData.pauseData.teamPausesCount.put(state.metaData.pauseData.teamPause, state.metaData.pauseData.teamPausesCount.get(state.metaData.pauseData.teamPause) - 1);
        			state.metaData.pauseData.currentPauseReason = gamePauseOrder.reason;
        		} else {
        			endPause(state, 3000L);
        		}
        		return true;
        	}
        }
        return false;
	}
	
	protected void endPause(GameState state, long resumeCountdownMS) {
		state.metaData.pauseData.currentPauseElapsedMS = 0;
		state.metaData.pauseData.teamPause = 0;
		state.metaData.pauseData.currentPauseReason = null;
		state.metaData.pauseData.resumeCountdownMS = resumeCountdownMS;
	}
	
	private boolean isValid(GamePauseOrder gamePauseOrder, GameState state) {
		Player player = playerProcessor.getPlayer(state.playerData.players, gamePauseOrder.playerId);
		if (gamePauseOrder.pause) {
			if (state.metaData.pauseData.teamPause != 0) return false;
			if (state.metaData.pauseData.teamPausesMS.get(player.teamId) >= state.metaData.pauseData.maxPauseMS) return false;
			if (state.metaData.pauseData.teamPausesCount.get(player.teamId) <= 0) return false;
		} else {
			if (state.metaData.pauseData.teamPause == 0) return false;
			if (player.teamId != state.metaData.pauseData.teamPause) return false;
			if (state.metaData.pauseData.currentPauseElapsedMS < state.metaData.pauseData.minPauseMS) return false;
		}
		return true;
	}

}
