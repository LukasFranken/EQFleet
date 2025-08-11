package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.LoadingBar;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CircularLoadingBar extends LoadingBar {
	
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
	protected void renderComponent() {
		
	}

	@Override
	public void dispose() {
		
	}

}
