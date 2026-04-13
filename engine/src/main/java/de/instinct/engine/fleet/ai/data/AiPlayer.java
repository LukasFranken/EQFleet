package de.instinct.engine.fleet.ai.data;

import de.instinct.engine.fleet.ai.configuration.AiBehaviorParameters;
import de.instinct.engine.fleet.ai.configuration.AiDifficulty;
import de.instinct.engine.fleet.player.FleetPlayer;
import lombok.ToString;

@ToString
public class AiPlayer extends FleetPlayer {
	
	public AiDifficulty difficulty;
	public AiBehaviorParameters behaviorParameters;

}
