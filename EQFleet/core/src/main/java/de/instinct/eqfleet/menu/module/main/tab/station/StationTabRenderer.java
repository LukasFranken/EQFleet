package de.instinct.eqfleet.menu.module.main.tab.station;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class StationTabRenderer extends BaseModuleRenderer {
	
private ImageButton backButton;
	
	public StationTabRenderer() {
		backButton = new ImageButton();
		backButton.setImageTexture(TextureManager.getTexture("ui/image", "arrowicon"));
		backButton.setBounds(new Rectangle(20, Gdx.graphics.getHeight() - 100, 24, 24));
		backButton.setAction(new Action() {
			
			@Override
			public void execute() {
				
			}
			
		});
	}

	@Override
	public void render() {
		Label.drawUnderConstruction(new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		backButton.render();
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

}
