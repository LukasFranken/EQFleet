
package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.MultitouchInputUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.platform.PlatformUtil;
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
    
    private int activePointer = -1;

    public Button() {
        super();
        enabled = true;
    }

    /** Disabling cancels pointer capture and transient interaction colors immediately. */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) clearInteraction();
    }

    private void clearInteraction() {
        down = false;
        hovered = false;
        activePointer = -1;
    }

    @Override
    public void updateComponent() {
    	boolean blockedByConsole = !consoleBypass && Console.isActive();
        if (enabled && getLayer() >= PopupRenderer.getCurrentLayer() && !blockedByConsole) {
        	if (!PlatformUtil.isMobile()) {
        		Vector2 touchPos = InputUtil.getVirtualMousePosition();
                hovered = getBounds().contains(touchPos.x, touchPos.y);
        	}
            if (activePointer == -1) {
                for (int i = 0; i < 10; i++) {
                    if (MultitouchInputUtil.isPressed(i)) {
                    	Vector2 touchPos = MultitouchInputUtil.getVirtualTouchPosition(i);
                        if (getBounds().contains(touchPos.x, touchPos.y)) {
                            activePointer = i;
                            down = true;
                            if (downAction != null) downAction.execute();
                            break;
                        }
                    }
                }
            }
            if (activePointer != -1) {
                if (!MultitouchInputUtil.isTouched(activePointer)) {
                	if (down) {
                        down = false;
                        Vector2 releasePos = MultitouchInputUtil.getVirtualTouchPosition(activePointer);
                        if (releasePos != null && getBounds().contains(releasePos) && action != null) action.execute();
                    }
                    if (upAction != null) upAction.execute();
                    activePointer = -1;
                }
            }
        } else {
            clearInteraction();
        }
        updateButton();
    }

    protected abstract void updateButton();
}
