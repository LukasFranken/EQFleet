package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScale;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.ColorScaleLoader;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class FPSStatusComponent extends Component {
	
	private ColorScaleLoader colorScaleLoader;
	private Label fpsLabel;
	
	public FPSStatusComponent() {
		super();
		colorScaleLoader = new ColorScaleLoader();
		fpsLabel = new Label("");
		fpsLabel.setColor(new Color(SkinManager.skinColor));
		fpsLabel.setType(FontType.TINY);
		fpsLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
	}
	
	@Override
	protected void updateComponent() {
		fpsLabel.setBounds(getBounds());
		fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + "FPS");
		fpsLabel.setColor(colorScaleLoader.load(ColorScale.GREEN_TO_RED, MathUtil.clamp(Gdx.graphics.getFramesPerSecond(), 0f, 60f) / 60f));
	}

	@Override
	protected void renderComponent() {
		fpsLabel.render();
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		fpsLabel.dispose();
	}

}
