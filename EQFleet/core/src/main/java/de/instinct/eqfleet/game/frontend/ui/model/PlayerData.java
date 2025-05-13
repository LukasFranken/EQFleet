package de.instinct.eqfleet.game.frontend.ui.model;

import de.instinct.engine.model.Player;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerData {
	
	private Player self;
	private Player teammate1;
	private Player teammate2;
	private Player enemy1;
	private Player enemy2;
	private Player enemy3;

}
