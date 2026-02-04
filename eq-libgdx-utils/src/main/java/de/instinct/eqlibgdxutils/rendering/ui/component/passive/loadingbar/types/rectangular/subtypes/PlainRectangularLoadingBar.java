package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.RectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlainRectangularLoadingBar extends RectangularLoadingBar {

	private Texture bar;
	private String customDescriptor;
	private Direction direction;
	private Label descriptorLabel;

	public PlainRectangularLoadingBar() {
		super();
		descriptorLabel = new Label(customDescriptor);
		descriptorLabel.setType(FontType.BOLD);
		bar = TextureManager.createTexture(Color.BLACK);
		direction = Direction.EAST;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}
	
	@Override
	protected void updateComponent() {
		
	}

	@Override
	protected void renderContent() {
		float barLengthFactor = (float)(getCurrentValue() / getMaxValue());
		float horizontalBarLength = barLengthFactor * (getBounds().width - (getBorder().getSize() * 2));
		float verticalBarLength = barLengthFactor * (getBounds().height - (getBorder().getSize() * 2));
		switch (direction) {
		case NORTH:
			TextureManager.draw(bar, new Rectangle(getBounds().x + getBorder().getSize(),
					getBounds().y + getBorder().getSize(),
					getBounds().width - (getBorder().getSize()) * 2,
					verticalBarLength), getAlpha());
			break;
		case EAST:
			TextureManager.draw(bar, new Rectangle(getBounds().x + getBorder().getSize(),
					getBounds().y + getBorder().getSize(),
					horizontalBarLength,
					getBounds().height - (getBorder().getSize()) * 2), getAlpha());
			break;
		case SOUTH:
			TextureManager.draw(bar, new Rectangle(getBounds().x + getBorder().getSize(),
					getBounds().y + + getBounds().height - getBorder().getSize() - verticalBarLength,
					getBounds().width - (getBorder().getSize()) * 2,
					verticalBarLength), getAlpha());
			break;
		case WEST:
			TextureManager.draw(bar, new Rectangle(getBounds().x + getBounds().width - getBorder().getSize() - horizontalBarLength,
					getBounds().y + getBorder().getSize(),
					horizontalBarLength,
					getBounds().height - (getBorder().getSize()) * 2), getAlpha());
			break;
		}
	}

	@Override
	protected void renderLabel() {
		String descriptor = StringUtils.barLabelFormat(getCurrentValue(), getMaxValue());
		if (customDescriptor != null) {
			descriptor = customDescriptor;
		}
		descriptorLabel.setText(descriptor);
		descriptorLabel.setBounds(getBounds());
		descriptorLabel.render();
	}

	@Override
	public void dispose() {

	}

}
