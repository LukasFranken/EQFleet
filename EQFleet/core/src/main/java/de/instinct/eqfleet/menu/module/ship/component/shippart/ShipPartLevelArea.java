package de.instinct.eqfleet.menu.module.ship.component.shippart;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipPartLevelArea extends Component {
	
	private ColorButton colorButton;
	private PlainRectangularLoadingBar partProgressBar;
	
	public ShipPartLevelArea(int level, Color partColor, float currentValue, float maxValue) {
		super();
		colorButton = new ColorButton("lv " + level + "\n ");
		Border levelButtonBorder = new Border();
		levelButtonBorder.setColor(partColor);
		levelButtonBorder.setSize(1f);
		colorButton.setBorder(levelButtonBorder);
		colorButton.getLabel().setType(FontType.SMALL);
		colorButton.getLabel().setColor(partColor);
		colorButton.setColor(Color.BLACK);
		Color hoverColor = new Color(partColor.r, partColor.g, partColor.b, 0.15f);
		colorButton.setHoverColor(hoverColor);
		Color downColor = new Color(partColor.r, partColor.g, partColor.b, 0.3f);
		colorButton.setDownColor(downColor);
		
		partProgressBar = new PlainRectangularLoadingBar();
		partProgressBar.setBar(TextureManager.createTexture(partColor));
		partProgressBar.setCurrentValue(currentValue);
		partProgressBar.setMaxValue(maxValue);
		Border barBorder = new Border();
		barBorder.setSize(1f);
		barBorder.setColor(partColor);
		partProgressBar.setBorder(barBorder);
		partProgressBar.setCustomDescriptor("");
		
	}
	
	public void setCurrentValue(float value) {
		partProgressBar.setCurrentValue(value);
	}

	@Override
	protected void updateComponent() {
		colorButton.setBounds(getBounds());
		partProgressBar.setBounds(new Rectangle(getBounds().x + 5, getBounds().y + 5, getBounds().width - 10, 4));
	}
	
	@Override
	protected void renderComponent() {
		colorButton.render();
		partProgressBar.render();
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
		colorButton.dispose();
		partProgressBar.dispose();
	}

}
