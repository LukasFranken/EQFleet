package de.instinct.eqlibgdxutils.rendering.ui.module.slideshow;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideLifeCycleStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Slide {

	protected final float FADE_IN_DURATION = 0.5f;
	protected final float FADE_OUT_DURATION = 0.5f;

	private float stageElapsed;
	private float alpha;
	private Rectangle bounds;
	private boolean fade;

	private SlideLifeCycleStage stage;
	private List<SlideCondition> conditions;

	public Slide() {
		stage = SlideLifeCycleStage.CREATED;
		conditions = new ArrayList<>();
		bounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fade = true;
	}

	public void render() {
		update();
		renderContent(alpha);
	}

	private void update() {
		stageElapsed += Gdx.graphics.getDeltaTime();
		switch (stage) {
		case CREATED:
			if (fade) {
				setStage(SlideLifeCycleStage.FADE_IN);
			} else {
				setStage(SlideLifeCycleStage.ACTIVE);
			}
			break;
		case FADE_IN:
			if (stageElapsed < FADE_IN_DURATION) {
				alpha = MathUtil.linear(0, 1, stageElapsed / FADE_IN_DURATION);
			} else {
				setStage(SlideLifeCycleStage.ACTIVE);
			}
			break;
		case ACTIVE:
			stageElapsed += Gdx.graphics.getDeltaTime();
			alpha = 1f;
			for (SlideCondition condition : conditions) {
				if (!condition.isMet()) {
					if (fade) {
						setStage(SlideLifeCycleStage.FADE_OUT);
					} else {
						setStage(SlideLifeCycleStage.FINISHED);
					}
				}
			}
			break;
		case FADE_OUT:
			if (stageElapsed < FADE_OUT_DURATION) {
				alpha = MathUtil.linear(1, 0, stageElapsed / FADE_OUT_DURATION);
			} else {
				setStage(SlideLifeCycleStage.FINISHED);
			}
			break;
		case FINISHED:
			break;
		}

	}

	private void setStage(SlideLifeCycleStage stage) {
		stageElapsed = 0;
		this.stage = stage;
	}

	public abstract void renderContent(float slideAlpha);

}
