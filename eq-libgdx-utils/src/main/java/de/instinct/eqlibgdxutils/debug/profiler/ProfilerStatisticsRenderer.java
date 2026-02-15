package de.instinct.eqlibgdxutils.debug.profiler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class ProfilerStatisticsRenderer {
	
	private Rectangle bounds;
	
	private Capture capture;
	private Section selectedSection;
	private String hoveredSection;
	private ProfilerStatisticMode mode;
	
	public ProfilerStatisticsRenderer() {
		mode = ProfilerStatisticMode.MAX;
	}
	
	private void update() {
		
	}
	
	public void render() {
		if (capture != null && bounds != null) {
			update();
			if (capture.getDurationNS() == 0) {
				renderActiveCapture();
			} else {
				renderFinishedCapture();
			}
		}
	}
	
	private void renderActiveCapture() {
		renderHeadline("ACTIVE CAPTURE");
		renderStatistic("Frames", "" + (capture.getSections().isEmpty() ? 0 : capture.getSections().get(0).getFrames().size()), 0);
		renderStatistic("Sections", "" + capture.getSections().size(), 1);
	}
	
	private String formatNSTime(long timeNS) {
		return StringUtils.formatNanoTime(timeNS, 2);
	}
	
	private void renderFinishedCapture() {
		if (selectedSection == null) {
			renderHeadline("SECTIONS");
			int i = 0;
			for (Section section : capture.getSections()) {
				 renderStatistic(section.getTag(), "", i);
				 if (hoveredSection != null && hoveredSection.equals(section.getTag())) {
					 Rectangle hoverRectBounds = getLineBounds(i);
					 hoverRectBounds.x -= 5f;
					 hoverRectBounds.width += 10f;
					 Shapes.draw(EQRectangle.builder()
							.color(new Color(1f, 1f, 1f, 0.1f))
							.bounds(hoverRectBounds)
							.round(true)
							.build());
				 }
				 i++;
			}
		} else {
			if (mode != null) {
				switch (mode) {
				case MAX:
					renderHeadline(selectedSection.getTag() + " - MAX");
					renderFrameStatistic(selectedSection.getMaxFrame());
					break;
				case MIN:
					renderHeadline(selectedSection.getTag() + " - MIN");
					renderFrameStatistic(selectedSection.getMinFrame());
					break;
				case AVG:
					renderHeadline(selectedSection.getTag() + " - AVG");
					renderFrameStatistic(selectedSection.getAvgFrame());
					break;
				}
			}
		}
	}

	private void renderFrameStatistic(Frame frame) {
		for (int i = 0; i < frame.getCheckpoints().size(); i++) {
			Checkpoint checkpoint = frame.getCheckpoints().get(i);
			renderStatistic(checkpoint.getTag(), formatNSTime(checkpoint.getDurationNS()), i);
		}
	}
	
	private void renderHeadline(String text) {
		Label labelHeadline = new Label(text);
		labelHeadline.setFixedWidth(bounds.width);
		labelHeadline.setFixedHeight(20f);
		labelHeadline.setPosition(bounds.x, bounds.y + bounds.height - 20f);
		labelHeadline.setType(FontType.SMALL);
		labelHeadline.render();
	}
	
	private void renderStatistic(String tag, String value, int index) {
		Rectangle lineBounds = getLineBounds(index);
		Label labelFramesLabel = new Label(tag);
		labelFramesLabel.setFixedWidth(lineBounds.width);
		labelFramesLabel.setFixedHeight(lineBounds.height);
		labelFramesLabel.setPosition(lineBounds.x, lineBounds.y);
		labelFramesLabel.setType(FontType.TINY);
		labelFramesLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		labelFramesLabel.setColor(Color.GRAY);
		labelFramesLabel.render();
		Label labelFramesValue = new Label(value);
		labelFramesValue.setFixedWidth(lineBounds.width);
		labelFramesValue.setFixedHeight(lineBounds.height);
		labelFramesValue.setPosition(lineBounds.x, lineBounds.y);
		labelFramesValue.setType(FontType.TINY);
		labelFramesValue.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		labelFramesValue.setColor(Color.GRAY);
		labelFramesValue.render();
	}
	
	private Rectangle getLineBounds(int index) {
		return new Rectangle(bounds.x + 10f, bounds.y + bounds.height - 30f - (index * 10f), bounds.width - 20f, 10f);
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void setCapture(Capture capture) {
		this.capture = capture;
	}
	
	public void setMode(ProfilerStatisticMode mode) {
		this.mode = mode;
	}
	
	public void setSelectedSection(Section selectedSection) {
		this.selectedSection = selectedSection;
	}
	
	public void setHoveredSection(String hoveredSection) {
		this.hoveredSection = hoveredSection;
	}

	public void reset() {
		this.capture = null;
		this.selectedSection = null;
		this.hoveredSection = null;
	}

}
