package de.instinct.eqfleet.menu.module.construction;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.construction.dto.PlayerTurretData;
import de.instinct.api.game.engine.EngineInterface;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.construction.message.UseTurretMessage;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
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
		if (ConstructionModel.infrastructure != null && ConstructionModel.playerInfrastructure != null) {
			for (PlayerTurretData playerTurretData : ConstructionModel.playerInfrastructure.getPlayerTurrets()) {
				//TurretData turretData = EngineInterface.getPlayerTurretData(playerTurretData, ConstructionModel.infrastructure);
				//turretButtons.add(createTurretButton(playerTurretData, turretData));
			}
		}
	}
	
	private LabeledModelButton createTurretButton(PlayerTurretData playerTurretData, TurretData turretData) {
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
		
		LabeledModelButton turretButton = new LabeledModelButton(shipModelPreviewConfig, turretData.model, new Action() {
			
			@Override
			public void execute() {
				createTurretPopup(playerTurretData.getUuid(), turretData);
			}
			
		});
		turretButton.setFixedWidth(50f);
		turretButton.setFixedHeight(70f);
		turretButton.getModelPreview().getBorder().setColor(playerTurretData.isInUse() ? Color.GREEN : SkinManager.skinColor);
		return turretButton;
	}
	
	private void createTurretPopup(String uuid, TurretData turretData) {
		ModelInstance turretModel = ModelLoader.instanciate("ship");
        for (Material material : turretModel.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
        }
		ModelPreviewConfiguration turretModelPreviewConfig = ModelPreviewConfiguration.builder()
				.model(turretModel)
				.baseRotationAngle(-90f)
				.baseRotationAxis(new Vector3(1, 0, 0))
				.scale(10f)
				.build();
		ModelPreview turretModelPreview = new ModelPreview(turretModelPreviewConfig);
		Border turretModelPreviewBorder = new Border();
		turretModelPreviewBorder.setSize(2f);
		turretModelPreviewBorder.setColor(Color.GRAY);
		turretModelPreview.setBorder(turretModelPreviewBorder);
		turretModelPreview.setFixedWidth(popupWidth);
		turretModelPreview.setFixedHeight(popupWidth);
		ElementList popupContent = new ElementList();
		popupContent.setMargin(10f);
		popupContent.getElements().add(turretModelPreview);
		/*popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RESOURCE COST", StringUtils.format(turretData.cost, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("CP COST", StringUtils.format(turretData.commandPointsCost, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("ROTATION SPEED", StringUtils.format(turretData.rotationSpeed, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("--------", "--------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("WEAPON", turretData.weapon.type.toString(), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DAMAGE", StringUtils.format(turretData.weapon.damage, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("RANGE", StringUtils.format(turretData.weapon.range, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("COOLDOWN", StringUtils.format(turretData.weapon.cooldown/1000f, 1) + "s", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("PROJECTILE SPEED", StringUtils.format(turretData.weapon.speed, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("--------", "--------", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("DEFENSE", "", popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("ARMOR", StringUtils.format(turretData.defense.armor, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD", StringUtils.format(turretData.defense.shield, 0), popupWidth));
		popupContent.getElements().add(DefaultLabelFactory.createLabelStack("SHIELD/SEC", StringUtils.format(turretData.defense.shieldRegenerationSpeed, 1), popupWidth));
		*/
		ColorButton useButton = DefaultButtonFactory.colorButton("Use", new Action() {
			
			@Override
			public void execute() {
				Menu.queue(UseTurretMessage.builder()
						.turretUUID(uuid)
						.build());
				
				PopupRenderer.close();
			}
			
		});
		useButton.setFixedHeight(30);
		useButton.setFixedWidth(200);
		useButton.setLayer(1);
		popupContent.getElements().add(useButton);
		
		PopupRenderer.create(Popup.builder()
				.title(turretData.model.toUpperCase())
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}

	@Override
	public void dispose() {
		if (turretButtons == null) return;
		for (LabeledModelButton turretButton : turretButtons) {
			turretButton.dispose();
		}
	}
	
}
