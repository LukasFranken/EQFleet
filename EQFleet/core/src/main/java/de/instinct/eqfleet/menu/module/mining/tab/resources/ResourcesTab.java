package de.instinct.eqfleet.menu.module.mining.tab.resources;

import java.util.Map.Entry;

import de.instinct.engine.mining.entity.asteroid.ResourceType;
import de.instinct.eqfleet.menu.module.mining.MiningMenuModel;
import de.instinct.eqfleet.mining.frontend.OreManager;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class ResourcesTab extends Component {
	
	private Label workingLabel;
	
	public ResourcesTab() {
		workingLabel = new Label("");
		workingLabel.setType(FontType.MEDIUM_BOLD);
	}

	@Override
	protected void updateComponent() {
		
	}
	
	@Override
	protected void renderComponent() {
		float margin = 10f;
		float elementHeight = 20f;
		int index = 1;
		if (MiningMenuModel.inventory != null) {
			for (Entry<ResourceType, Float> entry : MiningMenuModel.inventory.getResources().entrySet()) {
				workingLabel.setColor(OreManager.getColorForResourceType(entry.getKey()));
				workingLabel.setBounds(getBounds().x + margin, getBounds().y + getBounds().height - 10f - (index * elementHeight), getBounds().width - (margin * 2f), elementHeight);
				workingLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
				workingLabel.setText(entry.getKey().toString());
				workingLabel.render();
				workingLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
				workingLabel.setText(StringUtils.format(entry.getValue(), 0));
				workingLabel.render();
				index++;
			}
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
		
	}

}
