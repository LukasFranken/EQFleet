package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeRenderer;

public class DefenseUIRenderer {
	
	private ComplexShapeRenderer shapeRenderer;
	private Color shieldColor = new Color(0, 0.8f, 0.8f, 1f);
	private Color armorColor = new Color(1f, 0.5f, 0f, 1f);
	
	public DefenseUIRenderer() {
		shapeRenderer = new ComplexShapeRenderer();
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		for (Planet planet : state.planets) {
			if (planet.defense != null) {
				Rectangle defenseArea = new Rectangle(planet.position.x - 30, planet.position.y, 60, 12);
				renderBar(planet.defense, defenseArea, true);
			}
		}
		
		List<Rectangle> occupiedAreas = new ArrayList<>();
	    for (Ship ship : state.ships) {
	        if (ship.defense != null) {;
	            Rectangle defenseArea = new Rectangle(ship.position.x - 13, ship.position.y - 25, 30, 6);
	            boolean overlapping = true;
	            while (overlapping) {
	                overlapping = false;
	                for (Rectangle occupied : occupiedAreas) {
	                    if (defenseArea.overlaps(occupied)) {
	                        defenseArea.y = occupied.y - 6.1f;
	                        overlapping = true;
	                        break;
	                    }
	                }
	            }
	            occupiedAreas.add(new Rectangle(defenseArea));
	            renderBar(ship.defense, defenseArea, false);
	        }
	    }
	}
	
	private void renderBar(Defense defense, Rectangle bounds, boolean seperate) {
		if (defense.currentArmor > 0) {
			shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
			shapeRenderer.filledRoundRectangle(bounds);
			
			Rectangle armorBounds = new Rectangle(bounds);
			if (seperate) {
				armorBounds.y = armorBounds.y - bounds.height;
				shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
				shapeRenderer.filledRoundRectangle(armorBounds);
			}
			armorBounds.width = (armorBounds.width - 2) * (defense.currentArmor / defense.armor);
			shapeRenderer.setColor(armorColor);
			shapeRenderer.filledRoundRectangle(new Rectangle(armorBounds.x + 1, armorBounds.y + 1, armorBounds.width, armorBounds.height - 2));
			
			Rectangle shieldBounds = new Rectangle(bounds);
			if (defense.currentShield > 0) {
				shieldBounds.width = (shieldBounds.width - 2) * (defense.currentShield / defense.shield);
				shapeRenderer.setColor(shieldColor);
				shapeRenderer.filledRoundRectangle(new Rectangle(shieldBounds.x + 1, shieldBounds.y + 1, shieldBounds.width, shieldBounds.height - 2));
			}
		}
	}

}
