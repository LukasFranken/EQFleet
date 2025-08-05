package de.instinct.eqlibgdxutils.rendering.ui.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;

@Data
public class Border {

	private float size;
	private Color color;
	private Rectangle bounds;
	private float alpha;
	
	public void render() {
		Color finalColor = new Color(color);
		finalColor.a *= alpha;
		Rectangle adjustedBounds = GraphicsUtil.scaleFactorAdjusted(bounds);
		
		TextureManager.draw(TextureManager.createTexture(finalColor), new Rectangle(adjustedBounds.getX(), adjustedBounds.getY(), size, adjustedBounds.getHeight()));
		TextureManager.draw(TextureManager.createTexture(finalColor), new Rectangle(adjustedBounds.getX(), adjustedBounds.getY(), adjustedBounds.getWidth(), size));
		TextureManager.draw(TextureManager.createTexture(finalColor), new Rectangle(adjustedBounds.getX() + adjustedBounds.getWidth() - size, adjustedBounds.getY(), size, adjustedBounds.getHeight()));
		TextureManager.draw(TextureManager.createTexture(finalColor), new Rectangle(adjustedBounds.getX(), adjustedBounds.getY() + adjustedBounds.getHeight() - size, adjustedBounds.getWidth(), size));
	}

}
