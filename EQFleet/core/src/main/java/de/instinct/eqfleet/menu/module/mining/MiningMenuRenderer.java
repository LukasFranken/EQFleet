package de.instinct.eqfleet.menu.module.mining;

import java.util.HashMap;
import java.util.Map;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.tab.TabBar;
import de.instinct.eqfleet.menu.common.components.tab.TabButton;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.mining.tab.MissionTab;
import de.instinct.eqfleet.menu.module.mining.tab.ResourcesTab;
import de.instinct.eqfleet.menu.module.mining.tab.ShipTab;
import de.instinct.eqfleet.menu.module.mining.tab.ShopTab;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class MiningMenuRenderer extends BaseModuleRenderer {
	
	private Map<MiningMenuTab, Component> tabComponents;
	private TabBar tabBar;
	
	public MiningMenuRenderer() {
		MiningMenuModel.currentTab = MiningMenuTab.MISSION;
		tabComponents = new HashMap<>();
		tabComponents.put(MiningMenuTab.MISSION, new MissionTab());
		tabComponents.put(MiningMenuTab.RESOURCES, new ResourcesTab());
		tabComponents.put(MiningMenuTab.SHIP, new ShipTab());
		tabComponents.put(MiningMenuTab.SHOP, new ShopTab());
	}

	@Override
	public void init() {
		tabBar = new TabBar();
		for (MiningMenuTab tab : MiningMenuTab.values()) {
			TabButton miningTabButton = new TabButton(tab.toString());
			miningTabButton.setAction(new Action() {
				
				@Override
				public void execute() {
					MiningMenuModel.currentTab = tab;
				}
				
			});
			tabBar.addTabButton(miningTabButton);
		}
	}

	@Override
	public void update() {
		tabBar.setBounds(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y - 45f, MenuModel.moduleBounds.width, 40f);
		tabBar.setActiveButton(MiningMenuModel.currentTab.toString());
		for (TabButton tabButton : tabBar.getTabButtons()) {
			tabButton.setEnabled(isUnlocked(MiningMenuTab.valueOf(tabButton.getButtonText())));
		}
		tabComponents.get(MiningMenuModel.currentTab).setBounds(MenuModel.moduleBounds);
	}

	@Override
	public void render() {
		tabBar.render();
		tabComponents.get(MiningMenuModel.currentTab).render();
	}

	private boolean isUnlocked(MiningMenuTab tab) {
		switch (tab) {
		case MISSION:
			return true;
		case RESOURCES:
			return true;
		case SHIP:
			return true;
		case SHOP:
			return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		 for (Component component : tabComponents.values()) {
			 component.dispose();
		 }
		 tabBar.dispose();
	}

}
