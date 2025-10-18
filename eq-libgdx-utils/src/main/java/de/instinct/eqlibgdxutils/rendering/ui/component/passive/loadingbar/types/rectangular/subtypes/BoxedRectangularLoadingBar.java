package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.RectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoxedRectangularLoadingBar extends RectangularLoadingBar {

	private ColorScale colorScale;
	private int segments;
	private Color segmentOutlineColor;
	private int segmentOutlineSize;
	private ColorScaleLoader colorScaleLoader;
	
	public BoxedRectangularLoadingBar() {
		super();
		setColorScale(ColorScale.GREEN_TO_RED);
		setSegments(10);
		setSegmentOutlineColor(Color.BLACK);
		setSegmentOutlineSize(2);
		colorScaleLoader = new ColorScaleLoader();
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}
	
	@Override
	protected void updateComponent() {
		
	}

	@Override
	protected void renderContent() {
		int segments = getSegments();
		int filledSegments = (int) Math.floor(getCurrentValue());
	    int segmentSpace = calculateSegmentSpace(getSegments());

	    float totalSpacing = (segments - 1) * segmentSpace;
	    float segmentWidth = (getBounds().width - (getBorder().getSize() * 2) - totalSpacing) / (float) segments;
	    float xPos = getBounds().x + getBorder().getSize();
	    float yPos = getBounds().y + getBorder().getSize();
	    float height = getBounds().height - (getBorder().getSize() * 2);

	    for (int i = 0; i < segments; i++) {
	        float segmentXPos = xPos + i * (segmentWidth + segmentSpace);
	        Texture segmentTexture;
	        if (i < filledSegments) {
	        	if (getSegmentOutlineColor() != null) {
	            	TextureManager.draw(TextureManager.createTexture(getSegmentOutlineColor()), new Rectangle(segmentXPos - getSegmentOutlineSize(), 
	            			yPos - getSegmentOutlineSize(), segmentWidth + (getSegmentOutlineSize() * 2), height + (getSegmentOutlineSize() * 2)));
	            }
	            segmentTexture = getLoadingBarTexture((i + 1) / (double) segments);
	            TextureManager.draw(segmentTexture, new Rectangle(segmentXPos, yPos, segmentWidth, height));
	        }
	    }
	}
	
	private Texture getLoadingBarTexture(double segmentValue) {
	    return colorScaleLoader.load(getColorScale(), segmentValue);
	}
	
	private int calculateSegmentSpace(int segments) {
		if (segments < 10) {
	    	return 3;
	    }
		if (segments < 100) {
	    	return 2;
	    }
	    if (segments < 200) {
	    	return 1;
	    }
	    return 0;
	}
	
	@Override
	protected void renderLabel() {
		
	}

	@Override
	public void dispose() {
		
	}

}
