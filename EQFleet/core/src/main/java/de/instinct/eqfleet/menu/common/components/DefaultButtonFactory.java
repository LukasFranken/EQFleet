package de.instinct.eqfleet.menu.common.components;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.core.modules.ModuleUnlockRequirement;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class DefaultButtonFactory {
	
	public static ColorButton moduleButton(MenuModule module) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton moduleButton = new ColorButton(module.toString().substring(0, 3));
		moduleButton.setBorder(buttonBorder);
		moduleButton.setColor(Color.BLACK);
		moduleButton.setFixedHeight(50);
		moduleButton.setFixedWidth(50);
		moduleButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		moduleButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		moduleButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
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
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
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
				Label lockedMessage = new Label("Required Rank: " + moduleUnlockRequirement.getRequiredRank().getLabel());
				ElementList popupContent = new ElementList();
				popupContent.getElements().add(lockedMessage);
				PopupRenderer.create(Popup.builder()
						.title("Module Locked")
						.contentContainer(popupContent)
						.build());
			}
			
		});
		
		return moduleButton;
	}
	
	public static ColorButton colorButton(String label, Action clickAction) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton colorButton = new ColorButton(label);
		colorButton.setBorder(buttonBorder);
		colorButton.setColor(Color.BLACK);
		colorButton.setFixedHeight(50);
		colorButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		colorButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		colorButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		colorButton.setAction(clickAction);
		
		return colorButton;
	}

}
