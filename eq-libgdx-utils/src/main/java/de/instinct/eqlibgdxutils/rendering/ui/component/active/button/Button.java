package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.module.hover.HoverInfo;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Button extends Component {

	private Action action;
	private boolean active;
	private boolean enabled;
	private boolean down;
	private boolean hovered;
	private HoverInfo hoverInfo;
	private boolean consoleBypass;
	
	public Button() {
		super();
		enabled = true;
	}

	@Override
	public void updateComponent() {
		if (enabled && getLayer() >= PopupRenderer.getCurrentLayer()) {
	        if (InputUtil.mouseIsOver(getBounds())) {
	        	if (consoleBypass ? InputUtil.isPressedConsole() : InputUtil.isPressed()) {
	    			if (!down) {
	    				down = true;
	            	}
	    			hovered = false;
	    		} else {
	    			if (down) {
	    				if (action != null) {
	    					action.execute();
	    				}
	    				down = false;
	    			} else {
	    				if (Gdx.app.getType() != Application.ApplicationType.Android && Gdx.app.getType() != Application.ApplicationType.iOS) hovered = true;
	    			}
	    		}
	        } else {
	        	down = false;
	        	hovered = false;
	        }
		}
		updateButton();
	}
	
	protected abstract void updateButton();

}
