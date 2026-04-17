package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.Direction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.RectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlainRectangularLoadingBar extends RectangularLoadingBar {

	private String customDescriptor;
	private Direction direction;
	private Label descriptorLabel;
	private EQRectangle bar;
	private Color barColor;

	public PlainRectangularLoadingBar() {
		super();
		descriptorLabel = new Label(customDescriptor);
		descriptorLabel.setType(FontType.BOLD);
		bar = EQRectangle.builder()
				.filled(true)
				.build();
		barColor = new Color(1f, 1f, 1f, 1f);
		direction = Direction.EAST;
	}
	
	public void setColor(Color color) {
		this.barColor.set(color);
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
			bar.setBounds(getBounds().x + getBorder().getSize(),
					getBounds().y + getBorder().getSize(),
					getBounds().width - (getBorder().getSize()) * 2,
					verticalBarLength);
			break;
		case EAST:
			bar.setBounds(getBounds().x + getBorder().getSize(),
					getBounds().y + getBorder().getSize(),
					horizontalBarLength,
					getBounds().height - (getBorder().getSize()) * 2);
			break;
		case SOUTH:
			bar.setBounds(getBounds().x + getBorder().getSize(),
					getBounds().y + + getBounds().height - getBorder().getSize() - verticalBarLength,
					getBounds().width - (getBorder().getSize()) * 2,
					verticalBarLength);
			break;
		case WEST:
			bar.setBounds(getBounds().x + getBounds().width - getBorder().getSize() - horizontalBarLength,
					getBounds().y + getBorder().getSize(),
					horizontalBarLength,
					getBounds().height - (getBorder().getSize()) * 2);
			break;
		}
		bar.setColor(barColor);
		bar.getColor().a = bar.getColor().a * getAlpha();
		Shapes.draw(bar);
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
