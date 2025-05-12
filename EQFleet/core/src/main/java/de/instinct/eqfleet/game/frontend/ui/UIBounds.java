package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.math.Rectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UIBounds {

	private Rectangle ownCPBar;
	private Rectangle teammate1CPBar;
	private Rectangle teammate2CPBar;
	private Rectangle enemy1CPBar;
	private Rectangle enemy2CPBar;
	private Rectangle enemy3CPBar;
	
	private Rectangle teamAPBar;
	private Rectangle enemyAPBar;
	
}
