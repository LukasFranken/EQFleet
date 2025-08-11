package de.instinct.eqlibgdxutils.rendering.ui.module.hover;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.module.BaseModule;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class HoverInfo extends BaseModule {

	private Vector2 pointerOffset;
	private ElementList elementList;

	public HoverInfo() {
		super();
		pointerOffset = new Vector2(0f, 0f);
		elementList = new ElementList();
	}

	@Override
	public void updatePosition() {
		getBounds().x = InputUtil.getMouseX();
		getBounds().y = InputUtil.getMouseY();
	}

	@Override
	public void updateContent() {
		elementList.update();
	}

	@Override
	public void updateContentPosition() {
		elementList.getBounds().x = getBounds().x + getElementMargin();
		elementList.getBounds().y = getBounds().y + getElementMargin();
		elementList.update();
	}

	@Override
	public float calculateWidth() {
		return (getElementMargin() * 2) + elementList.getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return (getElementMargin() * 2) + elementList.getBounds().height;
	}
	
	@Override
	protected void renderContent() {
		elementList.render();
	}

	@Override
	public void dispose() {
		elementList.dispose();
	}

}
