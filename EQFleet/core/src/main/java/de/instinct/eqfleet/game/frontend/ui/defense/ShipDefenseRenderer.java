package de.instinct.eqfleet.game.frontend.ui.defense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.fleet.entity.unit.Unit;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class ShipDefenseRenderer {
	
	private List<Shield> reversedShields;
	
	private EQRectangle barOutlineShape;
	private EQRectangle barShape;
	
	public ShipDefenseRenderer() {
		reversedShields = new ArrayList<>();
		
		barOutlineShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(DefenseUIConfiguration.barColor)
				.thickness(2f)
				.round(true)
				.build();
		
		barShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.round(true)
				.filled(true)
				.build();
	}
	
	public void renderDefense(PerspectiveCamera camera, Unit unit, Rectangle bounds) {
		renderBar(camera, bounds, DefenseUIConfiguration.armorColor, (float) (unit.currentHull / unit.data.hullStrength));
		
		reversedShields.clear();
		reversedShields.addAll(unit.shields);
		Collections.reverse(reversedShields);
		int i = 0;
		for (Shield shield : reversedShields) {
			Rectangle shieldBounds = new Rectangle(bounds);
			shieldBounds.y = shieldBounds.y + (i * (bounds.height));
			switch (shield.data.type) {
			case PLASMA:
				renderBar(camera, shieldBounds, DefenseUIConfiguration.getShieldColor(shield.data.type), (float) (shield.currentStrength / shield.data.strength));
				break;
			case NULLPOINT:
				renderBar(camera, shieldBounds, DefenseUIConfiguration.getShieldColor(shield.data.type), (float) ((int)shield.currentStrength / shield.data.strength));
				break;
			case GRAVITON:
				break;
			}
			i++;
		}
	}

	private void renderBar(PerspectiveCamera camera, Rectangle bounds, Color color, float current) {
		barShape.getBounds().set(bounds.x + 1, bounds.y + 1, (bounds.width - 2) * current, bounds.height - 2);
		barShape.setProjectionMatrix(camera.combined);
		barShape.getColor().set(color);
		Shapes.draw(barShape);
		
		barOutlineShape.getBounds().set(bounds);
		barOutlineShape.setProjectionMatrix(camera.combined);
		Shapes.draw(barOutlineShape);
	}

}
