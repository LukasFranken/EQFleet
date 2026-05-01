package de.instinct.eqfleet.menu.common.components.tab;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinColor;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class TabButton extends Component {
	
	private HoloPanel holoPanel;
	private ColorButton button;
	private SkinColor skinColor;
	
	@Setter
	private boolean enabled;
	@Setter
	private boolean active;
	
	public TabButton(SkinColor skinColor, String label) {
		super();
		init(skinColor, label);
	}

	public TabButton(String label) {
		super();
		init(SkinManager.skin, label);
	}
	
	private void init(SkinColor skinColor, String label) {
		this.skinColor = skinColor;
		enabled = true;
		holoPanel = HoloPanel.builder()
				.color(new Color(skinColor.getColor()))
				.build();
		button = new ColorButton(label);
		button.getLabel().setType(FontType.SMALL_BOLD);
	}
	
	public void setAction(Action action) {
		button.setAction(action);
	}
	
	public String getButtonText() {
		return button.getLabel().getText();
	}

	@Override
	protected void updateComponent() {
		button.setBounds(getBounds());
		holoPanel.setBounds(getBounds());
		
		if (active) {
			button.getBorder().getColor().set(skinColor.getColor());
			button.getLabel().getColor().set(skinColor.getColor());
			button.getHoverColor().set(skinColor.getColor());
			button.getHoverColor().a = 0.5f;
			button.getDownColor().set(skinColor.getColor());
			button.getDownColor().a = 0.7f;
		} else {
			button.getBorder().getColor().set(Color.GRAY);
			button.getLabel().getColor().set(Color.GRAY);
			button.getHoverColor().set(Color.GRAY);
			button.getHoverColor().a = 0.5f;
			button.getDownColor().set(Color.GRAY);
			button.getDownColor().a = 0.7f;
		}
	}

	@Override
	protected void renderComponent() {
		if (enabled) {
			button.render();
			if (active) {
				HoloRenderer.drawPanel(holoPanel);
			}
		}
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
		button.dispose();
	}

}
