package de.instinct.eqfleet.game.frontend.ui.model;

import com.badlogic.gdx.math.Rectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UIBounds {

	private Rectangle time;
	private Rectangle ownCPBar;
	private Rectangle ownCPBarLabel;
	private Rectangle teammate1CPBar;
	private Rectangle teammate1CPBarLabel;
	private Rectangle teammate2CPBar;
	private Rectangle teammate2CPBarLabel;
	private Rectangle enemy1CPBar;
	private Rectangle enemy1CPBarLabel;
	private Rectangle enemy2CPBar;
	private Rectangle enemy2CPBarLabel;
	private Rectangle enemy3CPBar;
	private Rectangle enemy3CPBarLabel;
	
	private Rectangle teamAPBar;
	private Rectangle teamAPBarLabel;
	private Rectangle enemyAPBar;
	private Rectangle enemyAPBarLabel;
	
}
