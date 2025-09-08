package de.instinct.eqfleet.menu.common.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.core.modules.ModuleUnlockRequirement;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class DefaultButtonFactory {
	
	public static LabeledModelButton moduleButton(MenuModule module) {
		ModelInstance model = ModelLoader.instanciate("ship");
        for (Material material : model.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
        
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(model)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(20f)
				.build();
		
		LabeledModelButton menuModelButton = new LabeledModelButton(shipModelPreviewConfig, module.toString(), new Action() {
			
			@Override
			public void execute() {
				Menu.openModule(module);
			}
			
		});
		menuModelButton.setFixedWidth(50f);
		menuModelButton.setFixedHeight(70f);
		return menuModelButton;
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
		colorButton.setFixedHeight(30);
		colorButton.setFixedWidth(FontUtil.getFontTextWidthPx(label.length()) + 20f);
		
		return colorButton;
	}
	
	public static ImageButton backButton(Action backAction) {
		ImageButton backButton = new ImageButton();
		backButton.setImageTexture(TextureManager.getTexture("ui/image", "arrowicon"));
		backButton.setFixedHeight(20);
		backButton.setFixedWidth(20);
		backButton.setAction(backAction);

		return backButton;
	}

}
