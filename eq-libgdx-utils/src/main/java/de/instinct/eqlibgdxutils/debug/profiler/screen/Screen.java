package de.instinct.eqlibgdxutils.debug.profiler.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.debug.profiler.ProfilerModel;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public abstract class Screen {
	
	public abstract void init();
	
	public void render() {
		renderButtons();
		update();
		renderContent();
	}
	
	protected abstract void update();
	protected abstract void renderButtons();
	protected abstract void renderContent();
	
	protected void renderHeadline(String text) {
		Label labelHeadline = new Label(text);
		labelHeadline.setFixedWidth(ProfilerModel.screenBounds.width);
		labelHeadline.setFixedHeight(20f);
		labelHeadline.setPosition(ProfilerModel.screenBounds.x, ProfilerModel.screenBounds.y + ProfilerModel.screenBounds.height - 20f);
		labelHeadline.setType(FontType.SMALL);
		labelHeadline.render();
	}
	
	protected void renderStatistic(String tag, String value, int index) {
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
	
	protected void renderExtendedStatistic(String tag, String value1, String value2, String value3, int index) {
		Rectangle lineBounds = getLineBounds(index);
		Label labelFramesLabel = new Label(tag);
		labelFramesLabel.setFixedWidth(lineBounds.width);
		labelFramesLabel.setFixedHeight(lineBounds.height);
		labelFramesLabel.setPosition(lineBounds.x, lineBounds.y);
		labelFramesLabel.setType(FontType.TINY);
		labelFramesLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		labelFramesLabel.setColor(Color.GRAY);
		labelFramesLabel.render();
		
		Label labelFramesValue1 = new Label(value1);
		labelFramesValue1.setFixedWidth(lineBounds.width - 100f);
		labelFramesValue1.setFixedHeight(lineBounds.height);
		labelFramesValue1.setPosition(lineBounds.x, lineBounds.y);
		labelFramesValue1.setType(FontType.TINY);
		labelFramesValue1.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		labelFramesValue1.setColor(Color.GRAY);
		labelFramesValue1.render();
		Label labelFramesValue2 = new Label(value2);
		labelFramesValue2.setFixedWidth(lineBounds.width - 50f);
		labelFramesValue2.setFixedHeight(lineBounds.height);
		labelFramesValue2.setPosition(lineBounds.x, lineBounds.y);
		labelFramesValue2.setType(FontType.TINY);
		labelFramesValue2.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		labelFramesValue2.setColor(Color.GRAY);
		labelFramesValue2.render();
		Label labelFramesValue3 = new Label(value3);
		labelFramesValue3.setFixedWidth(lineBounds.width);
		labelFramesValue3.setFixedHeight(lineBounds.height);
		labelFramesValue3.setPosition(lineBounds.x, lineBounds.y);
		labelFramesValue3.setType(FontType.TINY);
		labelFramesValue3.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		labelFramesValue3.setColor(Color.GRAY);
		labelFramesValue3.render();
	}
	
	protected Rectangle getLineBounds(int index) {
		return new Rectangle(ProfilerModel.screenBounds.x + 10f, ProfilerModel.screenBounds.y + ProfilerModel.screenBounds.height - 30f - (index * 10f), ProfilerModel.screenBounds.width - 20f, 10f);
	}
	
	protected void drawLineHover(int index) {
		Rectangle hoverRectBounds = getLineBounds(index);
		 hoverRectBounds.x -= 5f;
		 hoverRectBounds.width += 10f;
		 Shapes.draw(EQRectangle.builder()
				.color(new Color(1f, 1f, 1f, 0.1f))
				.bounds(hoverRectBounds)
				.round(true)
				.build());
	}

}
