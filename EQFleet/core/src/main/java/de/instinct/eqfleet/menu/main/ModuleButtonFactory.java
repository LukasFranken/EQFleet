package de.instinct.eqfleet.menu.main;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.core.modules.ModuleUnlockRequirement;
import de.instinct.eqfleet.menu.main.message.types.OpenModuleMessage;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.LabeledButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.types.LabeledImageButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.types.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class ModuleButtonFactory {
	
	public static LabeledButton moduleButton(MenuModule module) {
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
		
		LabeledModelButton moduleButton = new LabeledModelButton(shipModelPreviewConfig, module.toString(), new Action() {
			
			@Override
			public void execute() {
				MenuModel.messageQueue.add(OpenModuleMessage.builder().module(module).build());
			}
			
		});
		return baseModuleButton(moduleButton);
	}
	
	public static LabeledButton moduleButton(ModuleUnlockRequirement moduleUnlockRequirement) {
		LabeledImageButton moduleButton = new LabeledImageButton(TextureManager.getTexture("ui/image/rank", moduleUnlockRequirement.getRequiredRank().getFileName()), "???");
		moduleButton.setAction(new Action() {
			
			@Override
			public void execute() {
				PopupRenderer.createMessageDialog("Module Locked", "Required Rank\n\n" + moduleUnlockRequirement.getRequiredRank().getLabel() + "\n ");
			}
			
		});
		
		return baseModuleButton(moduleButton);
	}
	
	private static LabeledButton baseModuleButton(LabeledButton moduleButton) {
		moduleButton.setHoverColor(SkinManager.darkerSkinColor);
		moduleButton.setDownColor(SkinManager.darkerSkinColor);
		return moduleButton;
	}

}
