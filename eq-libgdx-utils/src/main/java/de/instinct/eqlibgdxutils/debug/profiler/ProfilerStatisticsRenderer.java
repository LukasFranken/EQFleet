package de.instinct.eqlibgdxutils.debug.profiler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class ProfilerStatisticsRenderer {
	
	private Rectangle bounds;
	private Capture capture;
	
	private ProfilerStatisticMode mode;
	
	public ProfilerStatisticsRenderer() {
		
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
		Label labelHeadline = new Label("ACTIVE CAPTURE");
		labelHeadline.setFixedWidth(bounds.width);
		labelHeadline.setFixedHeight(15f);
		labelHeadline.setPosition(bounds.x, bounds.y + bounds.height - 20f);
		labelHeadline.setType(FontType.SMALL);
		labelHeadline.render();
		
		renderStatistic("Frames", "" + capture.getFrames().size(), 0);
		renderStatistic("Max Frametime MS", formatNSTime(capture.getMaxFrameTimeNS()), 1);
		renderStatistic("Min Frametime MS", formatNSTime(capture.getMinFrameTimeNS()), 2);
	}
	
	private String formatNSTime(long timeNS) {
		return StringUtils.formatNanoTime(timeNS, 2);
	}
	
	private void renderFinishedCapture() {
		if (mode != null) {
			Label labelHeadline = new Label("");
			labelHeadline.setFixedWidth(bounds.width);
			labelHeadline.setFixedHeight(15f);
			labelHeadline.setPosition(bounds.x, bounds.y + bounds.height - 20f);
			labelHeadline.setType(FontType.SMALL);
			switch (mode) {
			case MAX:
				labelHeadline.setText("MAX");
				renderFrameStatistic(capture.getMaxFrame());
				break;
			case MIN:
				labelHeadline.setText("MIN");
				renderFrameStatistic(capture.getMinFrame());
				break;
			case AVG:
				labelHeadline.setText("AVG");
				renderFrameStatistic(capture.getAvgFrame());
				break;
			}
			labelHeadline.render();
		}
	}

	private void renderFrameStatistic(Frame frame) {
		for (int i = 0; i < frame.getCheckpoints().size(); i++) {
			Checkpoint checkpoint = frame.getCheckpoints().get(i);
			renderStatistic(checkpoint.getTag(), formatNSTime(checkpoint.getDurationNS()), i);
		}
	}
	
	private void renderStatistic(String tag, String value, int index) {
		Label labelFramesLabel = new Label(tag);
		labelFramesLabel.setFixedWidth(bounds.width - 20f);
		labelFramesLabel.setFixedHeight(10f);
		labelFramesLabel.setPosition(bounds.x + 10f, bounds.y + bounds.height - 25f - (index * 10f));
		labelFramesLabel.setType(FontType.TINY);
		labelFramesLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		labelFramesLabel.setColor(Color.GRAY);
		labelFramesLabel.render();
		Label labelFramesValue = new Label(value);
		labelFramesValue.setFixedWidth(bounds.width - 20f);
		labelFramesValue.setFixedHeight(10f);
		labelFramesValue.setPosition(bounds.x + 10f, bounds.y + bounds.height - 25f - (index * 10f));
		labelFramesValue.setType(FontType.TINY);
		labelFramesValue.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		labelFramesValue.setColor(Color.GRAY);
		labelFramesValue.render();
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

}
