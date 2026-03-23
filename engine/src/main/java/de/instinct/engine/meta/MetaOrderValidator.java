package de.instinct.engine.meta;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.SurrenderOrder;
import de.instinct.engine.util.EngineUtility;

public class MetaOrderValidator {
	
	public static boolean isValid(GamePauseOrder gamePauseOrder, GameState state) {
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
	
	public static boolean isValid(SurrenderOrder surrenderOrder, GameState state) {
		if (surrenderOrder.playerId == 0) return false;
		return true;
	}

}
