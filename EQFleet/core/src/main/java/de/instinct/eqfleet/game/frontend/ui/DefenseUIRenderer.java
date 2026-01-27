package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.ship.components.types.ShieldType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class DefenseUIRenderer {
	
	private Color armorColor = new Color(1f, 0.5f, 0f, 0.8f);
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Turret turret : state.turrets) {
			if (turret.hull != null) {
				Rectangle defenseArea = new Rectangle(turret.position.x - 30, turret.position.y, 60, 14);
				renderDefense(camera, turret, defenseArea, true);
			}
		}
		
		List<Rectangle> occupiedAreas = new ArrayList<>();
	    for (Ship ship : state.ships) {
	        if (ship.hull != null) {
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
	            renderDefense(camera, ship, defenseArea, false);
	        }
	    }
	}
	
	private void renderDefense(PerspectiveCamera camera, Unit unit, Rectangle bounds, boolean seperate) {
		if (unit.hull.currentStrength > 0) {
			Rectangle armorBounds = new Rectangle(bounds);
			if (seperate) {
				armorBounds.y = armorBounds.y - bounds.height;
			}
			renderBar(camera, armorBounds, armorColor, (unit.hull.currentStrength / unit.hull.data.strength));
			
			List<Shield> reversedShields = new ArrayList<>(unit.shields);
			Collections.reverse(reversedShields);
			int i = 0;
			for (Shield shield : reversedShields) {
				Rectangle shieldBounds = new Rectangle(bounds);
				shieldBounds.y = shieldBounds.y + (i * (bounds.height));
				switch (shield.data.type) {
				case PLASMA:
					renderBar(camera, shieldBounds, getShieldColor(shield.data.type), (shield.currentStrength / shield.data.strength));
					break;
				case NULLPOINT:
					renderBar(camera, shieldBounds, getShieldColor(shield.data.type), ((int)shield.currentStrength / shield.data.strength));
					break;
				case GRAVITON:
					break;
				}
				i++;
			}
		}
	}
	
	private Color getShieldColor(ShieldType type) {
		switch (type) {
		case GRAVITON:
			return new Color(0f, 0.8f, 0.8f, 0.9f);
		case PLASMA:
			return new Color(0f, 0.8f, 0.8f, 0.9f);
		case NULLPOINT:
			return new Color(0.8f, 0f, 0.8f, 0.9f);
		}
		return null;
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
