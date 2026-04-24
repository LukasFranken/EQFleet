package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class FPSStatusComponent extends Component {
	
	private Label fpsLabel;
	
	public FPSStatusComponent() {
		super();
		fpsLabel = new Label("");
		fpsLabel.setColor(new Color(SkinManager.skinColor));
		fpsLabel.setType(FontType.TINY);
		fpsLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
	}
	
	@Override
	protected void updateComponent() {
		fpsLabel.setBounds(getBounds());
		fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + "FPS");
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
