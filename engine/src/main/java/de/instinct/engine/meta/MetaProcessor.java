package de.instinct.engine.meta;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.SurrenderOrder;
import de.instinct.engine.util.EngineUtility;

public class MetaProcessor {

	public void update(GameState state, long deltaTime) {
		if (state.teamPause != 0) {
			state.currentPauseElapsedMS += deltaTime;
			state.teamPausesMS.put(state.teamPause, state.teamPausesMS.get(state.teamPause) + deltaTime);
			if (state.teamPausesMS.get(state.teamPause) > state.maxPauseMS) {
				state.teamPause = 0;
			}
		} else {
			if (state.resumeCountdownMS > 0) {
				state.resumeCountdownMS -= deltaTime;
			}
		}
	}

	public boolean integrateNewOrder(GameState state, GameOrder order) {	
		if (order instanceof GamePauseOrder) {
        	GamePauseOrder gamePauseOrder = (GamePauseOrder)order;
        	if (isValid(gamePauseOrder, state)) {
        		Player player = EngineUtility.getPlayer(state.players, gamePauseOrder.playerId);
        		if (gamePauseOrder.pause) {
        			state.teamPause = player.teamId;
        			state.teamPausesCount.put(state.teamPause, state.teamPausesCount.get(state.teamPause) - 1);
        			state.currentPauseReason = gamePauseOrder.reason;
        		} else {
        			state.currentPauseElapsedMS = 0;
        			state.teamPause = 0;
        			state.currentPauseReason = null;
        			state.resumeCountdownMS = 3000L;
        		}
        		return true;
        	}
        }
        if (order instanceof SurrenderOrder) {
        	SurrenderOrder surrenderOrder = (SurrenderOrder)order;
        	if (isValid(surrenderOrder, state)) {
        		Player player = EngineUtility.getPlayer(state.players, surrenderOrder.playerId);
            	state.surrendered = player.teamId;
            	return true;
        	}
        }
        return false;
	}

	private boolean isValid(GamePauseOrder gamePauseOrder, GameState state) {
		Player player = EngineUtility.getPlayer(state.players, gamePauseOrder.playerId);
		if (gamePauseOrder.pause) {
			if (state.teamPause != 0) return false;
			if (state.teamPausesMS.get(player.teamId) >= state.maxPauseMS) return false;
			if (state.teamPausesCount.get(player.teamId) <= 0) return false;
		} else {
			if (state.teamPause == 0) return false;
			if (player.teamId != state.teamPause) return false;
			if (state.currentPauseElapsedMS < state.minPauseMS) return false;
		}
		return true;
	}
	
	private boolean isValid(SurrenderOrder surrenderOrder, GameState state) {
		if (surrenderOrder.playerId == 0) return false;
		return true;
	}

}
