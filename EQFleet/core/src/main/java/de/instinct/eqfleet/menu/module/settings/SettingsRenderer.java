package de.instinct.eqfleet.menu.module.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.ClipboardUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class SettingsRenderer extends BaseModuleRenderer {
	
	private ColorButton resetTokenButton;
	private ColorButton copyTokenButton;
	private Label authKeyLabel;
	
	public SettingsRenderer() {
		resetTokenButton = DefaultButtonFactory.colorButton("Delete", new Action() {
			
			@Override
			public void execute() {
				PreferenceUtil.save("authkey", "");
				API.authKey = "";
				Logger.log("Settings", "Authkey deleted!", ConsoleColor.YELLOW);
			}
			
		});
		resetTokenButton.setFixedWidth(80);
		resetTokenButton.setFixedHeight(30);
		
		copyTokenButton = DefaultButtonFactory.colorButton("Copy", new Action() {
			
			@Override
			public void execute() {
				ClipboardUtil.setContent(API.authKey);
				Logger.log("Settings", "Authkey copied!", ConsoleColor.YELLOW);
			}
			
		});
		copyTokenButton.setFixedWidth(80);
		copyTokenButton.setFixedHeight(30);
		
		authKeyLabel = new Label("");
	}

	@Override
	public void render() {
		if (API.authKey != null) {
			resetTokenButton.setPosition(Gdx.graphics.getWidth() / 2 - 90, Gdx.graphics.getHeight() / 2);
			resetTokenButton.render();
			
			copyTokenButton.setPosition(Gdx.graphics.getWidth() / 2 + 10, Gdx.graphics.getHeight() / 2);
			copyTokenButton.render();
			
			authKeyLabel.setBounds(new Rectangle(0, Gdx.graphics.getHeight() / 2 - 50, Gdx.graphics.getWidth(), 30));
			authKeyLabel.setText(API.authKey);
			authKeyLabel.render();
		}
	}

	@Override
	public void reload() {
		
	}

	@Override
	public void dispose() {
		
	}

}
