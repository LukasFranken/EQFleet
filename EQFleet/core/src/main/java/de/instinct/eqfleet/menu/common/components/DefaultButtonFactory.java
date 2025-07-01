package de.instinct.eqfleet.menu.common.components;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.core.modules.ModuleUnlockRequirement;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class DefaultButtonFactory {
	
	public static ColorButton moduleButton(MenuModule module) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);

		ColorButton moduleButton = new ColorButton(module.toString().substring(0, 3));
		moduleButton.setBorder(buttonBorder);
		moduleButton.setColor(Color.BLACK);
		moduleButton.setFixedHeight(50);
		moduleButton.setFixedWidth(50);
		moduleButton.setLabelColor(new Color(SkinManager.skinColor));
		moduleButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		moduleButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		moduleButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Menu.openModule(module);
			}
			
		});
		
		return moduleButton;
	}
	
	public static ImageButton moduleButton(ModuleUnlockRequirement moduleUnlockRequirement) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);

		ImageButton moduleButton = new ImageButton();
		moduleButton.setImageTexture(TextureManager.getTexture("ui/image/rank", moduleUnlockRequirement.getRequiredRank().getFileName()));
		moduleButton.setBorder(buttonBorder);
		moduleButton.setMargin(5f);
		moduleButton.setFixedHeight(50);
		moduleButton.setFixedWidth(50);
		moduleButton.setAction(new Action() {
			
			@Override
			public void execute() {
				PopupRenderer.createMessageDialog("Module Locked", "Required Rank: " + moduleUnlockRequirement.getRequiredRank().getLabel());
			}
			
		});
		
		return moduleButton;
	}
	
	public static ColorButton colorButton(String label, Action clickAction) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(SkinManager.skinColor));
		buttonBorder.setSize(2);

		ColorButton colorButton = new ColorButton(label);
		colorButton.setBorder(buttonBorder);
		colorButton.setColor(Color.BLACK);
		colorButton.setLabelColor(new Color(SkinManager.skinColor));
		colorButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		colorButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		colorButton.setAction(clickAction);
		
		return colorButton;
	}

}
