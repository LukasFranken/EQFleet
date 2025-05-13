package de.instinct.eqfleet.game.frontend.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UIElementConfig {
	
	private boolean isOwnCPVisible;
	private boolean isTeammate1CPVisible;
	private boolean isTeammate2CPVisible;
	private boolean isEnemyCPVisible;
	private boolean isEnemy2CPVisible;
	private boolean isEnemy3CPVisible;
	private boolean isOwnAPVisible;
	private boolean isEnemyAPVisible;
	private boolean isTimeVisible;

}
