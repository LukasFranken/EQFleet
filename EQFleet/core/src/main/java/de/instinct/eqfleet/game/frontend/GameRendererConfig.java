package de.instinct.eqfleet.game.frontend;

import de.instinct.eqfleet.game.frontend.ui.UIElementConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRendererConfig {
	
	private boolean visible;
	private UIElementConfig uiElementConfig;

}
