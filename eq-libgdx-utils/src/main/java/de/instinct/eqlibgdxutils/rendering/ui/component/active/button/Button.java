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
	private Action downAction;
	private Action upAction;	
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
			boolean pressed = consoleBypass ? InputUtil.isPressedConsole() : InputUtil.isPressed();
			boolean mouseOver = InputUtil.mouseIsOver(getBounds());
			boolean onMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
	        if (mouseOver) {
	        	if (pressed) {
	    			if (!down && InputUtil.isJustPressed()) {
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
	    				if (!onMobile) hovered = true;
	    			}
	    		}
	        } else {
	        	down = false;
	        	hovered = false;
	        }
	        if (down && downAction != null) downAction.execute();
	        if (!down && upAction != null) upAction.execute();
		}
		updateButton();
	}
	
	protected abstract void updateButton();

}
