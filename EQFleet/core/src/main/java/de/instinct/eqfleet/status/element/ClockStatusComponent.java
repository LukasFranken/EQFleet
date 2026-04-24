package de.instinct.eqfleet.status.element;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class ClockStatusComponent extends Component {
	
	private Label timeLabel;
	
	public ClockStatusComponent() {
		super();
		timeLabel = new Label("");
		timeLabel.setColor(new Color(Color.LIGHT_GRAY));
		timeLabel.setType(FontType.SMALL_BOLD);
	}
	
	@Override
	protected void updateComponent() {
		timeLabel.setText(StringUtils.getMinuteTime(System.currentTimeMillis()));
		timeLabel.setBounds(getBounds());
	}

	@Override
	protected void renderComponent() {
		timeLabel.render();
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
		
	}
	
}
