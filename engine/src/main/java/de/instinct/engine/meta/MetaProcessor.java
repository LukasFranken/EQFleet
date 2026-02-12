package de.instinct.engine.meta;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.SurrenderOrder;
import de.instinct.engine.util.EngineUtility;

public class MetaProcessor {

	public void update(GameState state, long deltaTime) {
		if (state.pauseData.teamPause != 0) {
			state.pauseData.currentPauseElapsedMS += deltaTime;
			state.pauseData.teamPausesMS.put(state.pauseData.teamPause, state.pauseData.teamPausesMS.get(state.pauseData.teamPause) + deltaTime);
			if (state.pauseData.teamPausesMS.get(state.pauseData.teamPause) > state.staticData.maxPauseMS) {
				state.pauseData.teamPause = 0;
			}
		} else {
			if (state.pauseData.resumeCountdownMS > 0) {
				state.pauseData.resumeCountdownMS -= deltaTime;
			}
		}
	}

	public boolean integrateNewOrder(GameState state, GameOrder order) {	
		if (order instanceof GamePauseOrder) {
        	GamePauseOrder gamePauseOrder = (GamePauseOrder)order;
        	if (isValid(gamePauseOrder, state)) {
        		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, gamePauseOrder.playerId);
        		if (gamePauseOrder.pause) {
        			state.pauseData.teamPause = player.teamId;
        			state.pauseData.teamPausesCount.put(state.pauseData.teamPause, state.pauseData.teamPausesCount.get(state.pauseData.teamPause) - 1);
        			state.pauseData.currentPauseReason = gamePauseOrder.reason;
        		} else {
        			state.pauseData.currentPauseElapsedMS = 0;
        			state.pauseData.teamPause = 0;
        			state.pauseData.currentPauseReason = null;
        			state.pauseData.resumeCountdownMS = 3000L;
        		}
        		return true;
        	}
        }
        if (order instanceof SurrenderOrder) {
        	SurrenderOrder surrenderOrder = (SurrenderOrder)order;
        	if (isValid(surrenderOrder, state)) {
        		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, surrenderOrder.playerId);
            	state.resultData.surrendered = player.teamId;
            	return true;
        	}
        }
        return false;
	}

	private boolean isValid(GamePauseOrder gamePauseOrder, GameState state) {
		Player player = EngineUtility.getPlayer(state.staticData.playerData.players, gamePauseOrder.playerId);
		if (gamePauseOrder.pause) {
			if (state.pauseData.teamPause != 0) return false;
			if (state.pauseData.teamPausesMS.get(player.teamId) >= state.staticData.maxPauseMS) return false;
			if (state.pauseData.teamPausesCount.get(player.teamId) <= 0) return false;
		} else {
			if (state.pauseData.teamPause == 0) return false;
			if (player.teamId != state.pauseData.teamPause) return false;
			if (state.pauseData.currentPauseElapsedMS < state.staticData.minPauseMS) return false;
		}
		return true;
	}
	
	private boolean isValid(SurrenderOrder surrenderOrder, GameState state) {
		if (surrenderOrder.playerId == 0) return false;
		return true;
	}

}
