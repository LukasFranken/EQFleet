package de.instinct.engine.core.meta;

import java.util.List;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.fleet.net.data.PlayerConnectionStatus;

public class MetaProcessor {

	public void update(GameState state, long deltaTime) {
		if (state.metaData.pauseData.teamPause != 0) {
			state.metaData.pauseData.currentPauseElapsedMS += deltaTime;
			state.metaData.pauseData.teamPausesMS.put(state.metaData.pauseData.teamPause, state.metaData.pauseData.teamPausesMS.get(state.metaData.pauseData.teamPause) + deltaTime);
			if (state.metaData.pauseData.teamPausesMS.get(state.metaData.pauseData.teamPause) > state.metaData.pauseData.maxPauseMS) {
				state.metaData.pauseData.teamPause = 0;
			}
		} else {
			if (state.metaData.pauseData.resumeCountdownMS > 0) {
				state.metaData.pauseData.resumeCountdownMS -= deltaTime;
			}
		}
	}
	
	public PlayerConnectionStatus getPlayerConnectionStatus(List<PlayerConnectionStatus> playerConnectionStatus, int playerId) {
		for (PlayerConnectionStatus connectionStatus : playerConnectionStatus) {
			if (connectionStatus.playerId == playerId) {
				return connectionStatus;
			}
		}
		return null;
	}

}
