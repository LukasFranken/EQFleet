package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
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
	private Direction direction;
	
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
		direction = Direction.EAST;
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
	    switch (direction) {
	        case EAST:
	            renderDirectional(true, false);
	            break;
	        case WEST:
	            renderDirectional(true, true);
	            break;
	        case NORTH:
	            renderDirectional(false, false);
	            break;
	        case SOUTH:
	            renderDirectional(false, true);
	            break;
	    }
	}

	private void renderDirectional(boolean horizontal, boolean reversed) {
	    int segments = getSegments();
	    double valuePerSegment = getMaxValue() / segments;
	    int filledSegments = (int) Math.floor(getCurrentValue() / valuePerSegment);
	    double valueRemainder = getCurrentValue() - (valuePerSegment * filledSegments);
	    double segmentFraction = valueRemainder / valuePerSegment;

	    float border = getBorder().getSize();
	    int segmentSpace = calculateSegmentSpace(segments);
	    float totalSpacing = (segments - 1) * segmentSpace;

	    float primaryTotal  = horizontal ? getBounds().width  : getBounds().height;
	    float secondaryTotal = horizontal ? getBounds().height : getBounds().width;
	    float segmentPrimary   = (primaryTotal - (border * 2) - totalSpacing) / segments;
	    float segmentSecondary = secondaryTotal - (border * 2);

	    float primaryOrigin   = horizontal ? getBounds().x + border : getBounds().y + border;
	    float secondaryOrigin = horizontal ? getBounds().y + border : getBounds().x + border;

	    for (int i = 0; i < segments; i++) {
	        int visualIndex = reversed ? (segments - 1 - i) : i;
	        float segmentPos = primaryOrigin + visualIndex * (segmentPrimary + segmentSpace);

	        workingColor.set(colorScaleLoader.load(getColorScale(), (i + 1.0) / segments));

	        if (i < filledSegments) {
	            setWorkingBounds(horizontal, segmentPos, secondaryOrigin, segmentPrimary, segmentSecondary);
	            drawSegment(workingColor, workingBounds);
	        } else if (partialSegments && i == filledSegments) {
	            float partialSize = segmentPrimary * (float) segmentFraction;
	            float partialPos = reversed ? segmentPos + segmentPrimary - partialSize : segmentPos;
	            setWorkingBounds(horizontal, partialPos, secondaryOrigin, partialSize, segmentSecondary);
	            drawSegment(workingColor, workingBounds);
	        }
	    }
	}

	private void setWorkingBounds(boolean horizontal, float primary, float secondary, float primarySize, float secondarySize) {
	    if (horizontal) {
	        workingBounds.set(primary, secondary, primarySize, secondarySize);
	    } else {
	        workingBounds.set(secondary, primary, secondarySize, primarySize);
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
