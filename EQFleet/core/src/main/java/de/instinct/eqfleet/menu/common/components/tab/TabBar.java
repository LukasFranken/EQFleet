package de.instinct.eqfleet.menu.common.components.tab;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;

public class TabBar extends Component {
	
	private List<TabButton> tabButtons;
	private String activeButton;
	private float buttonSpacing;
	
	public TabBar() {
		super();
		tabButtons = new ArrayList<>();
		activeButton = "";
		buttonSpacing = 10f;
	}
	
	public void addTabButton(TabButton tabButton) {
		tabButtons.add(tabButton);
	}
	
	public void clearTabButtons() {
		tabButtons.clear();
	}
	
	public List<TabButton> getTabButtons() {
		return tabButtons;
	}
	
	public void setAlpha(float alpha) {
		for (TabButton tabButton : tabButtons) {
			tabButton.setAlpha(alpha);
		}
	}
	
	public void setActiveButton(String buttonText) {
		this.activeButton = buttonText;
		for (TabButton tabButton : tabButtons) {
			tabButton.setActive(tabButton.getButtonText().equals(activeButton));
		}
	}
	
	@Override
	protected void updateComponent() {
		float buttonWidth = (getBounds().width - (tabButtons.size() - 1) * buttonSpacing) / tabButtons.size();
		for (int i = 0; i < tabButtons.size(); i++) {
			TabButton tabButton = tabButtons.get(i);
			tabButton.setBounds(getBounds().x + (i * buttonWidth) + (i * buttonSpacing),
					getBounds().y,
					buttonWidth,
					getBounds().height);
			tabButton.setAlpha(getAlpha());
		}
	}

	@Override
	protected void renderComponent() {
		for (TabButton tabButton : tabButtons) {
			tabButton.render();
		}
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		for (TabButton tabButton : tabButtons) {
			tabButton.dispose();
		}
	}
	
}
