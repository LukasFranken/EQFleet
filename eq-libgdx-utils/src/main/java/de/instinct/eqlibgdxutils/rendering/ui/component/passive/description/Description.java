package de.instinct.eqlibgdxutils.rendering.ui.component.passive.description;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.description.model.ColorModifier;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.description.model.DescriptionLine;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.description.model.Segment;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Description extends Component {

	private List<DescriptionLine> lines;
	private float lineSpacing;

	public Description() {
		super();
		lines = new ArrayList<>();
		lineSpacing = 10f;
	}

	@Override
	public float calculateWidth() {
		float width = 0f;
		for (DescriptionLine line : lines) {
			width = Math.max(width, getLineWidth(line));
		}
		return width;
	}

	private float getLineWidth(DescriptionLine line) {
		float bookmarkWidthOffset = 0;
		for (ColorModifier modifier : line.getModifiers()) {
			bookmarkWidthOffset += FontUtil.getFontTextWidthPx(modifier.getReplacement()) - FontUtil.getFontTextWidthPx("[" + modifier.getBookmark() + "]");
		}
		return FontUtil.getFontTextWidthPx(line.getContent()) + bookmarkWidthOffset;
	}

	@Override
	public float calculateHeight() {
		return (lines.size() * FontUtil.getFontHeightPx()) + ((lines.size() - 1) * lineSpacing);
	}
	
	@Override
	protected void updateComponent() {
		
	}

	@Override
	public void renderComponent() {
		int lineId = 0;
	    for (DescriptionLine line : lines) {
	        if (line.getModifiers().size() > 0) {
	            drawLineWithModifiers(line, lineId);
	        } else {
	            drawLine(line, lineId);
	        }
	        lineId++;
	    }
	}

	private void drawLine(DescriptionLine hoverInfoLine, int lineId) {
	    FontUtil.draw(hoverInfoLine.getTextColor(), hoverInfoLine.getContent(),
	            super.getScreenScaleAdjustedBounds().x, calculateLineYPosition(lineId), hoverInfoLine.isBold() ? FontType.BOLD : FontType.NORMAL);
	}

	private void drawLineWithModifiers(DescriptionLine hoverInfoLine, int lineId) {
	    String content = hoverInfoLine.getContent();
	    int currentIndex = 0;

	    while (currentIndex < content.length()) {
	        Segment nextSegment = findNextSegment(content, currentIndex, hoverInfoLine.getModifiers());

	        if (nextSegment == null) {
	            String remainingText = content.substring(currentIndex);
	            super.getScreenScaleAdjustedBounds().x = drawSegment(remainingText, hoverInfoLine.getTextColor(), hoverInfoLine.isBold(), lineId, super.getScreenScaleAdjustedBounds().x);
	            break;
	        }

	        if (nextSegment.getStartIndex() > currentIndex) {
	            String segment = content.substring(currentIndex, nextSegment.getStartIndex());
	            super.getScreenScaleAdjustedBounds().x = drawSegment(segment, hoverInfoLine.getTextColor(), hoverInfoLine.isBold(), lineId, super.getScreenScaleAdjustedBounds().x);
	        }

	        super.getScreenScaleAdjustedBounds().x = drawSegment(nextSegment.getReplacement(), nextSegment.getColor(), hoverInfoLine.isBold(), lineId, super.getScreenScaleAdjustedBounds().x);
	        currentIndex = nextSegment.getEndIndex();
	    }
	}

	private Segment findNextSegment(String content, int currentIndex, List<ColorModifier> modifiers) {
	    int nextBookmarkIndex = -1;
	    ColorModifier matchedModifier = null;

	    for (ColorModifier modifier : modifiers) {
	        int bookmarkIndex = content.indexOf(modifier.getBookmark(), currentIndex);
	        if (bookmarkIndex != -1 && (nextBookmarkIndex == -1 || bookmarkIndex < nextBookmarkIndex)) {
	            nextBookmarkIndex = bookmarkIndex;
	            matchedModifier = modifier;
	        }
	    }

	    if (matchedModifier == null) {
	        return null;
	    }

	    return new Segment(matchedModifier.getReplacement(), matchedModifier.getSegmentTextColor(), nextBookmarkIndex, nextBookmarkIndex + matchedModifier.getBookmark().length());
	}

	private float drawSegment(String segment, Color color, boolean isBold, int lineId, float currentX) {
	    FontUtil.draw(color, segment, currentX, calculateLineYPosition(lineId), isBold ? FontType.BOLD : FontType.NORMAL);
	    return currentX + FontUtil.getFontTextWidthPx(segment);
	}

	private float calculateLineYPosition(int lineId) {
		float yPos = super.getScreenScaleAdjustedBounds().y + super.getScreenScaleAdjustedBounds().height - (FontUtil.getFontHeightPx() * lineId) - (lineId * lineSpacing);
	    return yPos;
	}

	@Override
	public void dispose() {

	}

}
