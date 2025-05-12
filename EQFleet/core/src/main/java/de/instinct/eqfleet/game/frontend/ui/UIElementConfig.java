package de.instinct.eqfleet.game.frontend.ui;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UIElementConfig {
	
	private boolean isOwnCPVisible;
	private boolean isEnemyCPVisible;
	private boolean isOwnAPVisible;
	private boolean isEnemyAPVisible;
	private boolean isTimeVisible;

}
