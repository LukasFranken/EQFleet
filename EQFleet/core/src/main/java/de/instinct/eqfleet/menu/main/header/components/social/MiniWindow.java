package de.instinct.eqfleet.menu.main.header.components.social;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MiniWindow extends Component {
	
	private Label titleLabel;
	private Label contentLabel;
	private ColorButton acceptButton;
	private ColorButton declineButton;
	
	private MiniWindowConfiguration config;
	
	public MiniWindow(MiniWindowConfiguration config) {
		this.config = config;
		Border border = new Border();
		border.setSize(1f);
		border.setColor(new Color(SkinManager.skinColor));
		setBorder(border);
		titleLabel = new Label(config.getTitle());
		titleLabel.setBorder(border);
		titleLabel.setType(FontType.TINY);
		
		contentLabel = new Label(config.getContent());
		contentLabel.setType(FontType.TINY);
		acceptButton = new ColorButton(config.getAcceptButtonText());
		acceptButton.getLabel().setType(FontType.TINY);
		acceptButton.setAction(config.getAcceptAction());
		declineButton = new ColorButton(config.getDeclineButtonText());
		declineButton.getLabel().setType(FontType.TINY);
		declineButton.setAction(config.getDeclineAction());
	}
	
	@Override
	protected void updateComponent() {
		titleLabel.setBounds(getBounds().x, getBounds().y + getBounds().height - 13, getBounds().width, 13);
		contentLabel.setBounds(getBounds().x, getBounds().y + getBounds().height - 27, getBounds().width, 14);
		acceptButton.setBounds(getBounds().x, getBounds().y, (getBounds().width / 2) - 2, 15);
		declineButton.setBounds(getBounds().x + (getBounds().width / 2) + 2, getBounds().y, (getBounds().width / 2) - 2, 15);
	}
	
	@Override
	protected void renderComponent() {
		titleLabel.render();
		contentLabel.render();
		acceptButton.render();
		declineButton.render();
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
		titleLabel.dispose();
		contentLabel.dispose();
		acceptButton.dispose();
		declineButton.dispose();
	}

}
