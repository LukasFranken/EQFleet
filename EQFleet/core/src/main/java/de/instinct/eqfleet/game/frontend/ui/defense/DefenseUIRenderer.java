package de.instinct.eqfleet.game.frontend.ui.defense;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.GameState;

public class DefenseUIRenderer {
	
	private PlanetDefenseRenderer planetDefenseRenderer;
	private ShipDefenseRenderer shipDefenseRenderer;
	
	public DefenseUIRenderer() {
		planetDefenseRenderer = new PlanetDefenseRenderer();
		shipDefenseRenderer = new ShipDefenseRenderer();
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Turret turret : state.entityData.turrets) {
			if (turret.currentHull > 0) {
				planetDefenseRenderer.renderDefense(camera, turret);
			}
		}
		
		List<Rectangle> occupiedAreas = new ArrayList<>();
	    for (Ship ship : state.entityData.ships) {
	        if (ship.currentHull > 0) {
	            Rectangle defenseArea = new Rectangle(ship.position.x - 19, ship.position.y - 28, 40, 10);
	            boolean overlapping = true;
	            while (overlapping) {
	                overlapping = false;
	                for (Rectangle occupied : occupiedAreas) {
	                    if (defenseArea.overlaps(occupied)) {
	                        defenseArea.y = occupied.y - (defenseArea.getHeight() + 0.1f);
	                        overlapping = true;
	                        break;
	                    }
	                }
	            }
	            occupiedAreas.add(new Rectangle(defenseArea));
	            shipDefenseRenderer.renderDefense(camera, ship, defenseArea);
	        }
	    }
	}

}
