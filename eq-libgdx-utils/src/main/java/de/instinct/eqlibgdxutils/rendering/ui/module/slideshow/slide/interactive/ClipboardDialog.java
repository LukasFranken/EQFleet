package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.ClipboardUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClipboardDialog extends Slide {

	private final float CONTENT_FADE_IN_DURATION = 0.5f;
	private final float LABEL_MAX_OFFSET = 20f;
	private final float DIALOG_FADE_IN_DELAY = 2f;
	private final float CLIPBOARD_LOAD_FREQUENCY = 0.25f;

	private String message;
	private String response;

	private ColorButton useButton;
	private String authKey;
	private float contentElapsed;
	private long lastClipboardLoad;
	private boolean active;

	public ClipboardDialog() {
		super();
		response = "";
		active = true;
		getConditions().add(new SlideCondition() {

			@Override
			public boolean isMet() {
				return active;
			}

		});
		build();
	}

	public void build() {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		useButton = new ColorButton("Use");
		useButton.setBorder(buttonBorder);
		useButton.setColor(Color.BLACK);
		useButton.setFixedWidth(50);
		useButton.setFixedHeight(30);
		useButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		useButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		useButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
	}

	@Override
	public void renderContent(float slideAlpha) {
		loadClipboardContent();

		float labelYOffset = 0f;
		float clipboardLabelAlpha = 0f;
		if (getStage() == SlideLifeCycleStage.FADE_OUT) {
			labelYOffset = LABEL_MAX_OFFSET;
		}
		if (getStage() == SlideLifeCycleStage.ACTIVE) {
			if (getStageElapsed() >= DIALOG_FADE_IN_DELAY) {
				contentElapsed = MathUtils.clamp(contentElapsed + (authKey == null ? -Gdx.graphics.getDeltaTime() : Gdx.graphics.getDeltaTime()), 0, CONTENT_FADE_IN_DURATION);
				labelYOffset = MathUtil.easeInOut(0, LABEL_MAX_OFFSET, contentElapsed / CONTENT_FADE_IN_DURATION);
				clipboardLabelAlpha = MathUtil.easeInOut(0, 1, contentElapsed / CONTENT_FADE_IN_DURATION);
			}
		}

		FontUtil.drawLabel(message, new Rectangle(0, labelYOffset, getBounds().width, getBounds().height), slideAlpha);
		if (authKey != null) {
			useButton.setPosition((getBounds().width / 2) - (useButton.getBounds().width / 2), (getBounds().height / 2) - useButton.getBounds().height - FontUtil.getFontHeightPx() - 25);
			useButton.setAlpha(Math.min(clipboardLabelAlpha, slideAlpha));
			useButton.update();
			useButton.render();
			Color fontColor = new Color(DefaultUIValues.lighterSkinColor);
			fontColor.a = Math.min(clipboardLabelAlpha, slideAlpha);
			String authKeyLabel = authKey.substring(0, 6) + " ... " + authKey.substring(authKey.length() - 6, authKey.length());
			FontUtil.draw(fontColor, authKeyLabel, (getBounds().width / 2) - (FontUtil.getFontTextWidthPx(authKeyLabel) / 2), (getBounds().height / 2) + (FontUtil.getFontHeightPx() / 2) - 20);
		}
		FontUtil.drawLabel(response, new Rectangle(0, 50, getBounds().width, 30), slideAlpha);
	}

	private void loadClipboardContent() {
		if (lastClipboardLoad < System.currentTimeMillis() - (long)(1000 * CLIPBOARD_LOAD_FREQUENCY)) {
			authKey = ClipboardUtil.getUUID();
			lastClipboardLoad = System.currentTimeMillis();
		}
	}

}
