package de.instinct.engine.mining.player;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.core.player.PlayerProcessor;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.data.map.node.MiningMapNode;
import de.instinct.engine.mining.data.map.node.types.RecallAreaNode;

public class MiningPlayerProcessor extends PlayerProcessor {

	public void update(MiningGameState miningState, long deltaTime) {
		
	}
	
	public MiningPlayer getMiningPlayer(List<Player> players, int playerId) {
		return (MiningPlayer) getPlayer(players, playerId);
	}

	public void initializeRecallArea(MiningGameState state) {
		state.recallPosition = new Vector2(0, 0);
		state.recallRadius = 10f;
		for (MiningMapNode node : state.map.nodes) {
			if (node instanceof RecallAreaNode) {
				RecallAreaNode recallAreaNode = (RecallAreaNode) node;
				state.recallPosition = recallAreaNode.position;
				state.recallRadius = recallAreaNode.radius;
			}
		}
	}

}
