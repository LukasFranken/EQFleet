package de.instinct.eqfleet.game.frontend.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class DefenseUIRenderer {
	
	public DefenseUIRenderer() {
		TextureManager.createShapeTexture("ui_defense_border_planet",
			    ComplexShapeType.ROUNDED_RECTANGLE,
			    new Rectangle(100, 100, 32, 6),
			    new Color(0.5f, 0.5f, 0.5f, 1f), 
			    0.3f);
		TextureManager.createShapeTexture("ui_defense_border_ship",
			    ComplexShapeType.ROUNDED_RECTANGLE,
			    new Rectangle(100, 100, 20, 4),
			    new Color(0.5f, 0.5f, 0.5f, 1f), 
			    0.3f);
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Planet planet : state.planets) {
			if (planet.defense != null) {
				Defense defense = planet.defense;
				Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
				TextureManager.draw("ui_defense_border_planet", 
						new Rectangle(screenPos.x - 16 - 100, 
								screenPos.y - 1 - 100, 
								Gdx.graphics.getWidth(),
								Gdx.graphics.getHeight()),
						1f);
				TextureManager.draw("ui_defense_border_planet", 
						new Rectangle(screenPos.x - 16 - 100, 
								screenPos.y - 5 - 100, 
								Gdx.graphics.getWidth(),
								Gdx.graphics.getHeight()),
						1f);
				
				if (defense.currentArmor > 0) {
					PlainRectangularLoadingBar armorBar = createBar(Color.ORANGE);
					armorBar.setBounds(new Rectangle(screenPos.x - 15, screenPos.y - 4, 30, 4));
					armorBar.setCurrentValue(defense.currentArmor);
					armorBar.setMaxValue(defense.armor);
					armorBar.render();
					
					PlainRectangularLoadingBar shieldBar = createBar(new Color(0, 0.8f, 0.8f, 1f));
					shieldBar.setBounds(new Rectangle(screenPos.x - 15, screenPos.y, 30, 4));
					shieldBar.setCurrentValue(defense.currentShield);
					shieldBar.setMaxValue(defense.shield);
					shieldBar.render();
				}
			}
		}
		
		List<Rectangle> occupiedAreas = new ArrayList<>();
	    for (Ship ship : state.ships) {
	        if (ship.defense != null) {
	        	Defense defense = ship.defense;
	            Vector3 screenPos = camera.project(new Vector3(ship.position.x, ship.position.y, 0f));

	            Rectangle defenseArea = new Rectangle(screenPos.x - 10, screenPos.y - 17, 20, 4);
	            float originalY = defenseArea.y;
	            boolean overlapping = true;

	            while (overlapping) {
	                overlapping = false;
	                for (Rectangle occupied : occupiedAreas) {
	                    if (defenseArea.overlaps(occupied)) {
	                        defenseArea.y = occupied.y - 4;
	                        overlapping = true;
	                        break;
	                    }
	                }
	            }

	            float yOffset = defenseArea.y - originalY;
	            occupiedAreas.add(new Rectangle(defenseArea));

	            TextureManager.draw("ui_defense_border_ship",
	                    new Rectangle(screenPos.x - 10 - 100,
	                            screenPos.y - 17 - 100 + yOffset,
	                            Gdx.graphics.getWidth(),
	                            Gdx.graphics.getHeight()),
	                    1f);

	            if (defense.currentArmor > 0) {
	            	PlainRectangularLoadingBar armorBar = createBar(Color.ORANGE);
		            armorBar.setBounds(new Rectangle(screenPos.x - 9, screenPos.y - 16 + yOffset, 18, 2));
		            armorBar.setCurrentValue(defense.currentArmor);
		            armorBar.setMaxValue(defense.armor);
		            armorBar.render();

		            PlainRectangularLoadingBar shieldBar = createBar(new Color(0, 0.8f, 0.8f, 1f));
		            shieldBar.setBounds(new Rectangle(screenPos.x - 9, screenPos.y - 16 + yOffset, 18, 2));
		            shieldBar.setCurrentValue(defense.currentShield);
		            shieldBar.setMaxValue(defense.shield);
		            shieldBar.render();
	            }
	        }
	    }
	}

	private PlainRectangularLoadingBar createBar(Color color) {
		PlainRectangularLoadingBar bar = new PlainRectangularLoadingBar();
		bar.setBar(TextureManager.createTexture(color));
		bar.getBorder().setSize(0);
		bar.setDirection(Direction.EAST);
		bar.setCustomDescriptor("");
		bar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		return bar;
	}

}
