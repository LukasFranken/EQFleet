package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.ship.Defense;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class DefenseUIRenderer {
	
	private Color shieldColor = new Color(0, 0.8f, 0.8f, 0.8f);
	private Color armorColor = new Color(1f, 0.5f, 0f, 0.8f);
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Turret turret : state.turrets) {
			if (turret.defense != null) {
				Rectangle defenseArea = new Rectangle(turret.position.x - 30, turret.position.y, 60, 14);
				renderDefense(camera, turret.defense, defenseArea, true);
			}
		}
		
		List<Rectangle> occupiedAreas = new ArrayList<>();
	    for (Ship ship : state.ships) {
	        if (ship.defense != null) {;
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
	            renderDefense(camera, ship.defense, defenseArea, false);
	        }
	    }
	}
	
	private void renderDefense(PerspectiveCamera camera, Defense defense, Rectangle bounds, boolean seperate) {
		if (defense.currentArmor > 0) {
			Rectangle armorBounds = new Rectangle(bounds);
			if (seperate) {
				armorBounds.y = armorBounds.y - bounds.height;
			}
			renderBar(camera, armorBounds, armorColor, (defense.currentArmor / defense.armor));
			
			if (defense.shield > 0) {
				renderBar(camera, bounds, shieldColor, (defense.currentShield / defense.shield));
			}
		}
	}

	private void renderBar(PerspectiveCamera camera, Rectangle bounds, Color color, float current) {
		Shapes.draw(EQRectangle.builder()
				.projectionMatrix(camera.combined)
				.bounds(new Rectangle(bounds.x + 1, bounds.y + 1, (bounds.width - 2) * current, bounds.height - 2))
				.color(color)
				.round(true)
				.build());
		
		Shapes.draw(EQRectangle.builder()
				.projectionMatrix(camera.combined)
				.bounds(bounds)
				.color(new Color(0.5f, 0.5f, 0.5f, 1f))
				.thickness(2f)
				.round(true)
				.build());
	}

}
