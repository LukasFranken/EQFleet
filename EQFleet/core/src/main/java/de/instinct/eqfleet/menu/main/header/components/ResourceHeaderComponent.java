package de.instinct.eqfleet.menu.main.header.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.Resource;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

public class ResourceHeaderComponent extends Component {
	
	private Image creditsImage;
	private Label creditsLabel;
	private Rectangle creditsBounds;
	
	private EQRectangle creditsBorder;
	
	public void init() {
		creditsBounds = new Rectangle();
		
		creditsImage = new Image(TextureManager.getTexture("ui/image", "credits"));

        creditsLabel = new Label("");
		creditsLabel.setColor(Color.GREEN);
		creditsLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		
        creditsBorder = EQRectangle.builder()
				.bounds(creditsBounds)
				.color(Color.GREEN)
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(1f)
				.build();
	}

	@Override
	protected void updateComponent() {
		creditsBounds.set(getBounds().x + getBounds().width - 103, getBounds().y + 10, 85, 20);
		
		creditsLabel.setBounds(creditsBounds.x + 8, creditsBounds.y, creditsBounds.width - 16, creditsBounds.height);
		creditsLabel.setText(StringUtils.formatBigNumber(Inventory.getResource(Resource.CREDITS)));
		creditsLabel.setAlpha(getAlpha());
		
		creditsImage.setBounds(creditsBounds.x + creditsBounds.width + 2, creditsBounds.y + 2, 16, 16);
		creditsImage.setAlpha(getAlpha());
		
		creditsBorder.getColor().a = getAlpha();
	}
	
	@Override
	protected void renderComponent() {
        creditsLabel.render();
        creditsImage.render();
        Shapes.draw(creditsBorder);
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
		creditsImage.dispose();
		creditsLabel.dispose();
	}
	
}
