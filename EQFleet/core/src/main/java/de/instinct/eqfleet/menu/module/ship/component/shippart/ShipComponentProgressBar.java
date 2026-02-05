package de.instinct.eqfleet.menu.module.ship.component.shippart;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.shipyard.dto.ship.component.ShipComponentType;
import de.instinct.eqfleet.menu.module.ship.Shipyard;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipComponentProgressBar extends Component {
	
	private ShipComponentType type;
	private float currentProgress;
	
	private Label partLabel;
	private PlainRectangularLoadingBar progressBar;
	
	public ShipComponentProgressBar(ShipComponentType type, float maxProgress) {
		this.type = type;
		
		partLabel = new Label(type.toString());
		partLabel.setType(FontType.MICRO);
		partLabel.setColor(Shipyard.getPartTypeColor(type));
		partLabel.setFixedHeight(6);
		partLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		progressBar = new PlainRectangularLoadingBar();
		progressBar.setBar(TextureManager.createTexture(Shipyard.getPartTypeColor(type)));
		progressBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		progressBar.setMaxValue(maxProgress);
		progressBar.setCustomDescriptor("");
		progressBar.setFixedHeight(6);
		
		Border border = new Border();
		border.setAlpha(1f);
		border.setColor(Shipyard.getPartTypeColor(type));
		border.setSize(1f);
		progressBar.setBorder(border);
	}
	
	@Override
	protected void updateComponent() {
		progressBar.setCurrentValue(currentProgress);
		
		partLabel.setFixedWidth(30);
		partLabel.setPosition(getBounds().x, getBounds().y);
		
		progressBar.setFixedWidth(getFixedWidth() - partLabel.getFixedWidth() - 2);
		progressBar.setPosition(getBounds().x + partLabel.getFixedWidth() + 2, getBounds().y);
	}
	
	@Override
	protected void renderComponent() {
		partLabel.render();
		progressBar.render();
	}

	@Override
	public float calculateHeight() {
		return 10;
	}

	@Override
	public float calculateWidth() {
		return getFixedWidth();
	}

	@Override
	public void dispose() {
		partLabel.dispose();
		progressBar.dispose();
	}

}
