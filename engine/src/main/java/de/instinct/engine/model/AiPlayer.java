package de.instinct.engine.model;

import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.difficulty.AiBehaviorParameters;
import lombok.ToString;

@ToString
public class AiPlayer extends Player {
	
	public AiDifficulty difficulty;
	public AiBehaviorParameters behaviorParameters;

}
