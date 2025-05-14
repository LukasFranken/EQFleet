package de.instinct.eqfleet.menu.module.main.tab.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class InventoryTabRenderer extends Renderer {
	
	@Override
	public void init() {
		
	}

	@Override
	public void render() {
		FontUtil.drawLabel("(under construction)", new Rectangle(0, Gdx.graphics.getHeight() / 2 - 50, Gdx.graphics.getWidth(), 30));
	}

	@Override
	public void dispose() {
		
	}

}
