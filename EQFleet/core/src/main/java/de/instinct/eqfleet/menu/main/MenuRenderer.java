package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.core.modules.ModuleUnlockRequirement;
import de.instinct.eqfleet.ApplicationMode;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.tab.TabBar;
import de.instinct.eqfleet.menu.common.components.tab.TabButton;
import de.instinct.eqfleet.menu.main.header.MenuHeader;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.Button;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.LabeledButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.types.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class MenuRenderer extends BaseModuleRenderer {
	
	private final float OPEN_ANIMATION_DURATION = 1.5f;
	private float elapsed;

	private MenuWindow window;
	private MenuHeader header;
	private Map<MenuModule, Button> moduleButtons;
	private Map<MenuModule, MenuTab> moduleTabAssociations;
	private TabBar tabBar;

	public MenuRenderer() {
		MenuModel.moduleBounds = new Rectangle();
		
		moduleButtons = new LinkedHashMap<>();
		window = new MenuWindow();
		header = new MenuHeader();
		
		createTabBar();
		MenuModel.currentTab = MenuTab.FLAGSHIP;
		moduleButtons = new LinkedHashMap<>();
	}
	
	private void createTabBar() {
		tabBar = new TabBar();
		
		moduleTabAssociations = new LinkedHashMap<>();
		moduleTabAssociations.put(MenuModule.MINING, MenuTab.FLAGSHIP);
		moduleTabAssociations.put(MenuModule.CONSTRUCTION, MenuTab.FLAGSHIP);
		moduleTabAssociations.put(MenuModule.SHIPYARD, MenuTab.FLAGSHIP);
		moduleTabAssociations.put(MenuModule.STARMAP, MenuTab.FLAGSHIP);
		moduleTabAssociations.put(MenuModule.PLAY, MenuTab.FLAGSHIP);
		
		moduleTabAssociations.put(MenuModule.FORGE, MenuTab.STATION);
		moduleTabAssociations.put(MenuModule.MARKET, MenuTab.STATION);
		moduleTabAssociations.put(MenuModule.SHOP, MenuTab.STATION);
		
		moduleTabAssociations.put(MenuModule.SETTINGS, MenuTab.COMMANDER);
		moduleTabAssociations.put(MenuModule.PROFILE, MenuTab.COMMANDER);
		moduleTabAssociations.put(MenuModule.SOCIAL, MenuTab.COMMANDER);
		
		for (MenuTab tab : MenuTab.values()) {
			TabButton tabButton = new TabButton(tab.getColor(), tab.toString());
			tabButton.setAction(new Action() {
				
				@Override
				public void execute() {
					MenuModel.currentTab = tab;
					SkinManager.setSkinColor(tab.getColor());
					Breadcrumbs.change(tab.toString());
				}
				
			});
			tabBar.addTabButton(tabButton);
		}
	}

	private void createModuleButtons() {
		if (MenuModel.unlockedModules != null && MenuModel.lockedModules != null) {
			moduleButtons.clear();
			for (MenuModule module : MenuModule.values()) {
				createModuleButton(module);
			}
		}
	}

	@Override
	public void init() {
		resetWindowAnimation();
	}
	
	public void resetWindowAnimation() {
		MenuModel.alpha = 0f;
		elapsed = 0f;
	}
	
	private void createModuleButton(MenuModule module) {
		if (MenuModel.unlockedModules.getEnabledModules().contains(module)) moduleButtons.put(module, ModuleButtonFactory.moduleButton(module));
		ModuleUnlockRequirement requirement = getRequirement(module);
		if (requirement != null && requirement.getRequiredRank() != null) moduleButtons.put(module, ModuleButtonFactory.moduleButton(requirement));
	}
	
	private ModuleUnlockRequirement getRequirement(MenuModule module) {
		for (ModuleUnlockRequirement requirement : MenuModel.lockedModules.getUnlockRequirements()) {
			if (requirement.getModule() == module) {
				return requirement;
			}
		}
		return null;
	}

	@Override
	public void update() {
		if (MenuModel.modulesUpdated) {
			createModuleButtons();
			MenuModel.modulesUpdated = false;
		}
		float margin = 20f;
		MenuModel.moduleBounds.set(margin, margin + 60, GraphicsUtil.screenBounds().width - (margin * 2), GraphicsUtil.screenBounds().height - 150f - 80f);
		updateWindowAnimation();
		header.getBounds().set(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height + 50, MenuModel.moduleBounds.width, 45);
		window.getBounds().set(MenuModel.moduleBounds);
		header.setAlpha(MenuModel.alpha);
		
		tabBar.setBounds(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y - 45f, MenuModel.moduleBounds.width, 40f);
		tabBar.setActiveButton(MenuModel.currentTab.toString());
		tabBar.setAlpha(MenuModel.alpha);
	}

	@Override
	public void render() {
		if (MenuModel.moduleBounds != null) {
			Profiler.startFrame("MENU_RNDR");
			header.render();
			Profiler.checkpoint("MENU_RNDR", "header");
			window.render();
			Profiler.checkpoint("MENU_RNDR", "window");
			if (MenuModel.activeModule == null) {
				tabBar.render();
				Profiler.checkpoint("MENU_RNDR", "tab bar");
				renderModuleButtons();
				Profiler.checkpoint("MENU_RNDR", "mod btns");
			}
			elapsed += Gdx.graphics.getDeltaTime();
			Profiler.endFrame("MENU_RNDR");
		}
	}

	private void updateWindowAnimation() {
		if (elapsed < OPEN_ANIMATION_DURATION / 2) {
			MenuModel.openAnimationElapsed = elapsed / (OPEN_ANIMATION_DURATION / 2);
		} else {
			MenuModel.openAnimationElapsed = 1f;
			if (elapsed < OPEN_ANIMATION_DURATION) {
				MenuModel.alpha = (elapsed - (OPEN_ANIMATION_DURATION / 2)) / (OPEN_ANIMATION_DURATION / 2);
			} else {
				MenuModel.alpha = 1f;
			}
		}
	}
	
	private void renderModuleButtons() {
		if (moduleButtons != null) {
			int i = 0;
			for (MenuModule module : moduleButtons.keySet()) {
				if (module == MenuModule.PLAY && GlobalStaticData.mode != ApplicationMode.DEV) continue;
				if (moduleTabAssociations.get(module) != MenuModel.currentTab) continue;
				if (MenuModel.unlockedModules.getEnabledModules().contains(module)) {
					renderModuleButton(module, i);
					i++;
				}
			}
			if (MenuModel.lockedModules != null) {
				List<ModuleUnlockRequirement> lockedModuleRequirementsInTab = new ArrayList<>();
				for (ModuleUnlockRequirement unlockRequirement : MenuModel.lockedModules.getUnlockRequirements()) {
					if (moduleTabAssociations.get(unlockRequirement.getModule()) == MenuModel.currentTab) {
						lockedModuleRequirementsInTab.add(unlockRequirement);
					}
				}
				ModuleUnlockRequirement nextUnlockRequirement = null;
				for (ModuleUnlockRequirement unlockRequirement : lockedModuleRequirementsInTab) {
					if (unlockRequirement.getRequiredRank() == null) continue;
					if (nextUnlockRequirement == null) {
						nextUnlockRequirement = unlockRequirement;
					} else {
						if (nextUnlockRequirement.getRequiredRank().isLowerThan(unlockRequirement.getRequiredRank())) {
							nextUnlockRequirement = unlockRequirement;
						}
					}
				}
				if (nextUnlockRequirement != null) {
					renderModuleButton(nextUnlockRequirement.getModule(), i);
				}
			}
		}
	}
	
	private void renderModuleButton(MenuModule module, int index) {
		LabeledButton moduleButton = (LabeledButton) moduleButtons.get(module);
		if (moduleButton == null) return;
		float labeledModelButtonHeight = 70f;
		float buttonWidth = 50f;
		int elementsPerRow = 5;
		float margin = (((float)MenuModel.moduleBounds.width) - (buttonWidth * elementsPerRow)) / ((float)(elementsPerRow + 1));
		int column = index % elementsPerRow;
		int row = 1 + ((int)index / elementsPerRow);
		
		moduleButton.getContentBorder().getColor().set(SkinManager.skinColor);
		moduleButton.getLabel().getColor().set(SkinManager.skinColor);
		moduleButton.getHoverColor().set(SkinManager.skinColor);
		moduleButton.getHoverColor().a = 0.5f;
		moduleButton.getDownColor().set(SkinManager.skinColor);
		moduleButton.getDownColor().a = 0.7f;
		if (moduleButton instanceof LabeledModelButton) {
			LabeledModelButton modelButton = (LabeledModelButton) moduleButton;
			for (Material material : modelButton.getModelPreview().getConfig().getModel().materials) {
	            material.set(ColorAttribute.createDiffuse(SkinManager.darkestSkinColor));
	        }
		}
		moduleButton.setBounds(MenuModel.moduleBounds.x + margin + ((buttonWidth + margin) * column), MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - ((labeledModelButtonHeight + margin) * row), buttonWidth, labeledModelButtonHeight);
		moduleButton.setAlpha(MenuModel.alpha);
		moduleButton.render();
	}

	@Override
	public void dispose() {
		window.dispose();
		header.dispose();
		for (Button button : moduleButtons.values()) {
			button.dispose();
		}
	}

}
