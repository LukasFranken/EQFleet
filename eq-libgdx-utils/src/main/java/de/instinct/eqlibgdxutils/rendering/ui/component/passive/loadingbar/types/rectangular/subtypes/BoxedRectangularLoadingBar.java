package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.RectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class BoxedRectangularLoadingBar extends RectangularLoadingBar {

	private ColorScale colorScale;
	private int segments;
	private Color segmentOutlineColor;
	private int segmentOutlineSize;
	private ColorScaleLoader colorScaleLoader;
	private boolean partialSegments;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private EQRectangle workingShape;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Rectangle workingBounds;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Color workingColor;
	
	public BoxedRectangularLoadingBar() {
		super();
		setColorScale(ColorScale.GREEN_TO_RED);
		setSegments(10);
		setSegmentOutlineColor(Color.BLACK);
		setSegmentOutlineSize(2);
		colorScaleLoader = new ColorScaleLoader();
		workingShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.filled(true)
				.build();
		workingBounds = new Rectangle();
		workingColor = new Color();
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
		double valuePerSegment = getMaxValue() / segments;
		int filledSegments = (int) Math.floor(getCurrentValue() / valuePerSegment);
		double valueRemainder = getCurrentValue() - (valuePerSegment * filledSegments);
		double segmentWidthFraction = valueRemainder / valuePerSegment;

		int segmentSpace = calculateSegmentSpace(getSegments());
	    float totalSpacing = (segments - 1) * segmentSpace;
	    float segmentWidth = (getBounds().width - (getBorder().getSize() * 2) - totalSpacing) / (float) segments;
	    float xPos = getBounds().x + getBorder().getSize();
	    float yPos = getBounds().y + getBorder().getSize();
	    float height = getBounds().height - (getBorder().getSize() * 2);

	    for (int i = 0; i < segments; i++) {
	        float segmentXPos = xPos + i * (segmentWidth + segmentSpace);
	        workingColor.set(colorScaleLoader.load(getColorScale(), ((i + 1) / (double) segments)));
	        if (i < filledSegments) {
	            workingBounds.set(segmentXPos, yPos, segmentWidth, height);
	            drawSegment(workingColor, workingBounds);
	        }
	        if (partialSegments) {
	        	if (i == filledSegments) {
	        		workingBounds.set(segmentXPos, yPos, segmentWidth * (float)segmentWidthFraction, height);
	        		drawSegment(workingColor, workingBounds);
	        	}
	        }
	    }
	}
	
	private void drawSegment(Color color, Rectangle bounds) {
		if (getSegmentOutlineColor() != null) {
    		workingShape.getColor().set(getSegmentOutlineColor());
    		workingShape.getBounds().set(bounds.x - getSegmentOutlineSize(), bounds.y - getSegmentOutlineSize(), bounds.width + (getSegmentOutlineSize() * 2), bounds.height + (getSegmentOutlineSize() * 2));
    		Shapes.draw(workingShape);
        }
    	workingShape.getBounds().set(bounds);
        workingShape.getColor().set(color);
        Shapes.draw(workingShape);
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
