package de.instinct.eqfleet.menu.module.starmap.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.api.game.dto.MapPreview;
import de.instinct.api.game.dto.PreviewPlanet;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.SimpleShapeRenderer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MapPreviewSection extends Component {
	
	private MapPreview mapPreview;
	
	public MapPreviewSection(MapPreview mapPreview) {
		super();
		this.mapPreview = mapPreview;
		Border border = new Border();
		border.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
		border.setSize(2f);
		super.setBorder(border);
	}
	
	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		
	}
	
	@Override
	protected void updateComponent() {
		
	}

	@Override
	protected void renderComponent() {
		float planetRadius = 3f * mapPreview.getZoomFactor();
		for (PreviewPlanet planet : mapPreview.getPlanets()) {
			Vector2 planetScreenPos = getScreenPosition(planet.getXPos(), planet.getYPos());
			Color planetColor = planet.isAncient() && planet.getOwnerId() == 0 ? GameConfig.ancientColor : getPlayerColor(planet.getOwnerId());
			SimpleShapeRenderer.drawCircle(planetScreenPos, planetRadius, planetColor);
		}
	}

	private Color getPlayerColor(int ownerId) {
		if (ownerId == 0) return GameConfig.neutralColor;
		if (ownerId == 1) return GameConfig.teammate1Color;
		if (ownerId == 2) return GameConfig.enemyColor;
		return null;
	}

	private Vector2 getScreenPosition(float xPos, float yPos) {
		return MathUtil.translate(new Vector2(xPos, yPos), new Rectangle(-1000 / mapPreview.getZoomFactor(), -1000 / mapPreview.getZoomFactor(), 2000 / mapPreview.getZoomFactor(), 2000 / mapPreview.getZoomFactor()), getScreenScaleAdjustedBounds());
	}

}
