package de.instinct.eqfleet.game.frontend.ui.model;

import com.badlogic.gdx.math.Rectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UIBounds {

	private Rectangle time;
	private Rectangle ownResBar;
	private Rectangle ownResBarLabel;
	private Rectangle teammate1ResBar;
	private Rectangle teammate1ResBarLabel;
	private Rectangle teammate2ResBar;
	private Rectangle teammate2ResBarLabel;
	private Rectangle enemy1ResBar;
	private Rectangle enemy1ResBarLabel;
	private Rectangle enemy2ResBar;
	private Rectangle enemy2ResBarLabel;
	private Rectangle enemy3ResBar;
	private Rectangle enemy3ResBarLabel;
	
	private Rectangle teamAPBar;
	private Rectangle teamAPBarLabel;
	private Rectangle enemyAPBar;
	private Rectangle enemyAPBarLabel;
	
}
