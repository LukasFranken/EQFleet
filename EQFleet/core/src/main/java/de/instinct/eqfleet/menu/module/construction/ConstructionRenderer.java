package de.instinct.eqfleet.menu.module.construction;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.construction.dto.PlanetTurretBlueprint;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.construction.message.UseTurretMessage;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class ConstructionRenderer extends BaseModuleRenderer {

private List<LabeledModelButton> turretButtons;
	
	private final float popupWidth = 200f;
	
	@Override
	public void render() {
		if (turretButtons != null) {
			renderTurrets();
		}
	}

	private void renderTurrets() {
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (LabeledModelButton turretButton : turretButtons) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			turretButton.setPosition(MenuModel.moduleBounds.x + margin + ((50 + margin) * column),
					MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - ((70 + margin) * row));
			turretButton.render();
			i++;
		}
	}

	@Override
	public void reload() {
		turretButtons = new ArrayList<>();
		if (ConstructionModel.infrastructure != null && ConstructionModel.infrastructure.getPlanetTurretBlueprints() != null) {
			for (PlanetTurretBlueprint blueprint : ConstructionModel.infrastructure.getPlanetTurretBlueprints()) {
				turretButtons.add(createTurretButton(blueprint));
			}
		}
	}
	
	private LabeledModelButton createTurretButton(PlanetTurretBlueprint blueprint) {
		ModelInstance model = ModelLoader.instanciate("ship");
        for (Material material : model.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
        
		ModelPreviewConfiguration shipModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(model)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(20f)
				.grid(false)
				.build();
		
		LabeledModelButton turretButton = new LabeledModelButton(shipModelPreviewConfig, blueprint.getName(), new Action() {
			
			@Override
			public void execute() {
				createTurretPopup(blueprint);
			}
			
		});
		turretButton.setFixedWidth(50f);
		turretButton.setFixedHeight(70f);
		turretButton.getModelPreview().getBorder().setColor(blueprint.isInUse() ? Color.GREEN : SkinManager.skinColor);
		return turretButton;
	}
	
	private void createTurretPopup(PlanetTurretBlueprint blueprint) {
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(createLabelStack("WEAPON", blueprint.getPlanetWeapon().getType().toString()));
		popupContent.getElements().add(createLabelStack("DAMAGE", StringUtils.format(blueprint.getPlanetWeapon().getDamage(), 0)));
		popupContent.getElements().add(createLabelStack("RANGE", StringUtils.format(blueprint.getPlanetWeapon().getRange(), 0)));
		popupContent.getElements().add(createLabelStack("COOLDOWN", StringUtils.format(blueprint.getPlanetWeapon().getCooldown()/1000f, 1) + "s"));
		popupContent.getElements().add(createLabelStack("SPEED", StringUtils.format(blueprint.getPlanetWeapon().getSpeed(), 0)));
		popupContent.getElements().add(createLabelStack("--------", "--------"));
		popupContent.getElements().add(createLabelStack("DEFENSE", ""));
		popupContent.getElements().add(createLabelStack("ARMOR", StringUtils.format(blueprint.getPlanetDefense().getArmor(), 0)));
		popupContent.getElements().add(createLabelStack("SHIELD", StringUtils.format(blueprint.getPlanetDefense().getShield(), 0)));
		popupContent.getElements().add(createLabelStack("SHIELD/SEC", StringUtils.format(blueprint.getPlanetDefense().getShieldRegenerationSpeed(), 1)));
		
		ColorButton useButton = DefaultButtonFactory.colorButton("Use", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(UseTurretMessage.builder()
						.turretUUID(blueprint.getUuid())
						.build());
				
				PopupRenderer.close();
			}
			
		});
		useButton.setFixedHeight(30);
		useButton.setFixedWidth(200);
		popupContent.getElements().add(useButton);
		
		PopupRenderer.create(Popup.builder()
				.title(blueprint.getName())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}
	
	private ElementStack createLabelStack(String label, String value) {
		ElementStack uuidLabelStack = new ElementStack();
		Label uuidLabel = new Label(label);
		uuidLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		uuidLabel.setFixedWidth(popupWidth);
		uuidLabelStack.getElements().add(uuidLabel);
		
		Label uuidValueLabel = new Label(value);
		uuidValueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		uuidValueLabel.setFixedWidth(popupWidth);
		uuidValueLabel.setType(FontType.SMALL);
		uuidLabelStack.getElements().add(uuidValueLabel);
		return uuidLabelStack;
	}

	@Override
	public void dispose() {
		if (turretButtons == null) return;
		for (LabeledModelButton turretButton : turretButtons) {
			turretButton.dispose();
		}
	}
	
}
