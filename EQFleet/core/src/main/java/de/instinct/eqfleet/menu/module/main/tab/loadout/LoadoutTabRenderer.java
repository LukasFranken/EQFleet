package de.instinct.eqfleet.menu.module.main.tab.loadout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class LoadoutTabRenderer extends BaseModuleRenderer {

	public LoadoutTabRenderer() {
		
	}

	@Override
	public void render() {
		Label.drawUnderConstruction(new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

}
