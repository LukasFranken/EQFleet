package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.BarFragment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.RectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class FragmentRectangularLoadingBar extends RectangularLoadingBar {
	
	private List<BarFragment> fragments;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private EQRectangle workingShape;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Rectangle workingBounds;
	
	public FragmentRectangularLoadingBar() {
		super();
		workingShape = EQRectangle.builder()
				.filled(true)
				.build();
		workingBounds = new Rectangle();
	}
	
	@Override
	protected void updateComponent() {
		
	}
	
	@Override
	protected void renderContent() {
		if (fragments == null) return;
		float usedUpValue = 0f;
		for (BarFragment fragment : fragments) {
			float fragmentStartX = (float) (getBounds().x + (getBounds().width * (usedUpValue / getMaxValue())));
			float fragmentWidth = (float) (getBounds().width * (fragment.getValue() / getMaxValue()));
			if (fragmentStartX + fragmentWidth > getBounds().x + getBounds().width) {
				fragmentWidth = (getBounds().x + getBounds().width) - fragmentStartX;
			}
			workingBounds.set(fragmentStartX, getBounds().y, fragmentWidth, getBounds().height);
			workingShape.setColor(fragment.getColor());
			workingShape.setBounds(workingBounds);
			Shapes.draw(workingShape);
			usedUpValue += fragment.getValue();
		}
	}

	@Override
	protected void renderLabel() {}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public void dispose() {
		
	}

}
