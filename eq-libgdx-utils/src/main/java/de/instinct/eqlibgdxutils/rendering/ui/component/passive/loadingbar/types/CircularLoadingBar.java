package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.LoadingBar;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CircularLoadingBar extends LoadingBar {
	
	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected void renderElement() {
		
	}

	@Override
	public void dispose() {
		
	}

}
