package de.instinct.eqfleet.menu.module.main.tab.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class SettingsTabRenderer extends Renderer {
	
	private ColorButton resetTokenButton;

	@Override
	public void init() {
		Border buttonBorder = new Border();
		buttonBorder.setSize(2);
		buttonBorder.setColor(Color.GRAY);
		
		resetTokenButton = new ColorButton("Delete Key");
		resetTokenButton.setBorder(buttonBorder);
		resetTokenButton.setColor(Color.BLACK);
		resetTokenButton.setFixedWidth(120);
		resetTokenButton.setFixedHeight(30);
		resetTokenButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		resetTokenButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		resetTokenButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		resetTokenButton.setActive(true);
		resetTokenButton.setAction(new Action() {
			
			@Override
			public void execute() {
				PreferenceUtil.save("authkey", "");
				API.authKey = "";
				System.out.println("Authkey deleted!");
			}
			
		});
		resetTokenButton.setBorder(buttonBorder);
	}

	@Override
	public void render() {
		resetTokenButton.setPosition(Gdx.graphics.getWidth() / 2 - 60, Gdx.graphics.getHeight() / 2);
		resetTokenButton.render();
		
		if (API.authKey != null)
		FontUtil.drawLabel(API.authKey, new Rectangle(0, Gdx.graphics.getHeight() / 2 - 50, Gdx.graphics.getWidth(), 30));
	}

	@Override
	public void dispose() {
		
	}

}
