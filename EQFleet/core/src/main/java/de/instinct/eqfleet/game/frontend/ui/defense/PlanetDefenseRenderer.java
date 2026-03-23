package de.instinct.eqfleet.game.frontend.ui.defense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class PlanetDefenseRenderer {
	
	private Rectangle armorBounds;
	private Rectangle shieldBounds;
	
	private Rectangle workingBounds;
	private EQRectangle workingShape;
	private EQCircle workingCircle;
	
	private Label workingLabel;
	private Vector3 projectedPosition;
	private Vector3 projectedRight;
	
	private List<Shield> reversedShields;
	
	public PlanetDefenseRenderer() {
		armorBounds = new Rectangle();
		shieldBounds = new Rectangle();
		workingBounds = new Rectangle();
		
		workingShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.filled(true)
				.thickness(0f)
				.build();
		
		workingCircle = EQCircle.builder()
				.position(new Vector2())
				.radius(5)
				.color(new Color())
				.build();
		
		workingLabel = new Label("");
		workingLabel.setType(FontType.MICRO_BOLD);
		workingLabel.setBounds(new Rectangle());
		workingLabel.setColor(new Color());
		
		projectedPosition = new Vector3();
		projectedRight = new Vector3();
		
		reversedShields = new ArrayList<>();
	}
	
	public void renderDefense(PerspectiveCamera camera, Unit unit) {
		Rectangle bounds = new Rectangle(unit.position.x - 30, unit.position.y, 60, 10);
		if (unit.currentHull > 0) {
			armorBounds.set(bounds);
			armorBounds.y = armorBounds.y - bounds.height;
			renderBar(camera, armorBounds, DefenseUIConfiguration.armorColor, (float) (unit.currentHull / unit.data.hullStrength));
			
			workingLabel.getColor().set(DefenseUIConfiguration.armorColor);
			renderLabel(camera, (long) unit.currentHull, armorBounds, false);
			
			reversedShields.clear();
			reversedShields.addAll(unit.shields);
			Collections.reverse(reversedShields);
			int i = 0;
			for (Shield shield : reversedShields) {
				shieldBounds.set(bounds);
				shieldBounds.y = shieldBounds.y + (i * (bounds.height));
				switch (shield.data.type) {
				case PLASMA:
					renderBar(camera, shieldBounds, DefenseUIConfiguration.getShieldColor(shield.data.type), (float) (shield.currentStrength / shield.data.strength));
					workingLabel.getColor().set(DefenseUIConfiguration.getShieldColor(shield.data.type));
					renderLabel(camera, (long) shield.currentStrength, shieldBounds, true);
					break;
				case NULLPOINT:
					renderBar(camera, shieldBounds, DefenseUIConfiguration.getShieldColor(shield.data.type), (float) ((int)shield.currentStrength / shield.data.strength));
					workingLabel.getColor().set(DefenseUIConfiguration.getShieldColor(shield.data.type));
					renderLabel(camera, (long) shield.currentStrength, shieldBounds, true);
					break;
				case GRAVITON:
					break;
				}
				i++;
			}
		}
	}

	private void renderLabel(PerspectiveCamera camera, long value, Rectangle bounds, boolean top) {
	    projectedPosition.set(bounds.x, top ? bounds.y + bounds.height : bounds.y, 0);
	    camera.project(projectedPosition);

	    projectedRight.set(bounds.x + bounds.width, bounds.y, 0);
	    camera.project(projectedRight);

	    String hullText = StringUtils.formatBigNumber(value, 0).replaceAll(" ", "");
	    if (hullText.length() < 3) {
	        hullText = StringUtils.formatBigNumber(value, 1).replaceAll(" ", "");
	    }

	    float scaleFactor = GraphicsUtil.getScaleFactor();

	    float labelHeight = FontUtil.getFontHeightPx(FontType.MICRO_BOLD);
	    float vx = projectedPosition.x / scaleFactor;
	    float vy;
	    if (top) {
	        vy = projectedPosition.y / scaleFactor + 2;
	    } else {
	        vy = (projectedPosition.y / scaleFactor) - labelHeight - 2;
	    }

	    float labelWidth = (projectedRight.x - projectedPosition.x) / scaleFactor;

	    workingLabel.setText(hullText);
	    workingLabel.setBounds(vx, vy, labelWidth, labelHeight);
	    workingLabel.render();
	}


	private void renderBar(PerspectiveCamera camera, Rectangle bounds, Color color, float current) {
		workingShape.setProjectionMatrix(camera.combined);
		workingCircle.setProjectionMatrix(camera.combined);
		
		workingCircle.getPosition().set(bounds.x, bounds.y + (bounds.height / 2));
		workingCircle.getColor().set(DefenseUIConfiguration.barColor);
		Shapes.draw(workingCircle);
		
		workingCircle.getPosition().set(bounds.x + bounds.width, bounds.y + (bounds.height / 2));
		Shapes.draw(workingCircle);
		
		workingShape.getBounds().set(bounds);
		workingShape.getColor().set(DefenseUIConfiguration.barColor);
		Shapes.draw(workingShape);
		
		workingShape.getBounds().set(workingBounds.set(bounds.x + 1, bounds.y + 2, (bounds.width - 2), bounds.height - 4));
		workingShape.getColor().set(0f, 0f, 0f, 1f);
		Shapes.draw(workingShape);
		
		workingShape.getBounds().set(workingBounds.set(bounds.x + 1, bounds.y + 2, (bounds.width - 2) * current, bounds.height - 4));
		workingShape.getColor().set(color);
		Shapes.draw(workingShape);
	}

}
