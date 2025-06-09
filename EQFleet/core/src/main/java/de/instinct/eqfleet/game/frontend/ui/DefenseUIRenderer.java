package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Combat;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class DefenseUIRenderer {
	
	public DefenseUIRenderer() {
		TextureManager.createShapeTexture("ui_defense_border_planet",
			    ComplexShapeType.ROUNDED_RECTANGLE,
			    new Rectangle(100, 100, 40, 8),
			    new Color(0.5f, 0.5f, 0.5f, 1f), 
			    0.3f);
		TextureManager.createShapeTexture("ui_defense_border_ship",
			    ComplexShapeType.ROUNDED_RECTANGLE,
			    new Rectangle(100, 100, 20, 6),
			    new Color(0.5f, 0.5f, 0.5f, 1f), 
			    0.3f);
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Planet planet : state.planets) {
			if (planet.defense != null) {
				Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
				TextureManager.draw("ui_defense_border_planet", 
						new Rectangle(screenPos.x - 20 - 100, 
								screenPos.y - 32 - 100, 
								Gdx.graphics.getWidth(),
								Gdx.graphics.getHeight()),
						1f);
				
				PlainRectangularLoadingBar armorBar = createBar(Color.ORANGE);
				armorBar.setBounds(new Rectangle(screenPos.x - 19, screenPos.y - 31, 38, 6));
				armorBar.setCurrentValue(planet.currentArmor);
				armorBar.setMaxValue(planet.defense.armor);
				armorBar.render();
				
				PlainRectangularLoadingBar shieldBar = createBar(new Color(0, 0.8f, 0.8f, 1f));
				shieldBar.setBounds(new Rectangle(screenPos.x - 19, screenPos.y - 31, 38, 6));
				shieldBar.setCurrentValue(planet.currentShield);
				shieldBar.setMaxValue(planet.defense.shield);
				shieldBar.render();
			}
		}
		for (Combat combat : state.activeCombats) {
			for (Ship ship : combat.ships) {
				if (ship.defense != null) {
					Vector3 screenPos = camera.project(new Vector3(ship.position.x, ship.position.y, 0f));
					TextureManager.draw("ui_defense_border_ship", 
							new Rectangle(screenPos.x - 10 - 100, 
									screenPos.y - 17 - 100, 
									Gdx.graphics.getWidth(),
									Gdx.graphics.getHeight()),
							1f);
					
					PlainRectangularLoadingBar armorBar = createBar(Color.ORANGE);
					armorBar.setBounds(new Rectangle(screenPos.x - 9, screenPos.y - 16, 18, 4));
					armorBar.setCurrentValue(ship.currentArmor);
					armorBar.setMaxValue(ship.defense.armor);
					armorBar.render();
					
					PlainRectangularLoadingBar shieldBar = createBar(new Color(0, 0.8f, 0.8f, 1f));
					shieldBar.setBounds(new Rectangle(screenPos.x - 9, screenPos.y - 16, 18, 4));
					shieldBar.setCurrentValue(ship.currentShield);
					shieldBar.setMaxValue(ship.defense.shield);
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
