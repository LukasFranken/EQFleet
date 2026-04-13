package de.instinct.eqfleet.game.frontend.ui.model;

import de.instinct.engine.fleet.player.FleetPlayer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerData {
	
	private FleetPlayer self;
	private FleetPlayer teammate1;
	private FleetPlayer teammate2;
	private FleetPlayer enemy1;
	private FleetPlayer enemy2;
	private FleetPlayer enemy3;

}
