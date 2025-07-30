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

	public void integrateNewOrders(GameState state) {	
		for (GameOrder order : state.orders) {
            if (!order.processed) {
                if (order instanceof GamePauseOrder) {
                	GamePauseOrder gamePauseOrder = (GamePauseOrder)order;
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
            		order.processed = true;
                }
                if (order instanceof SurrenderOrder) {
                	SurrenderOrder surrenderOrder = (SurrenderOrder)order;
                	Player player = EngineUtility.getPlayer(state.players, surrenderOrder.playerId);
                	state.surrendered = player.teamId;
                }
            }
        }
	}

}
