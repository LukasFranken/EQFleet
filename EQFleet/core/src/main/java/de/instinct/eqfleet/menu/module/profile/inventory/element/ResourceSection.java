package de.instinct.eqfleet.menu.module.profile.inventory.element;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqfleet.menu.postgame.PostGameModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.core.UIElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResourceSection extends Component {
	
	private float itemHeight = 15f;
	
	private List<ResourceChange> resources;
	private List<UIElement> uiElements;
	
	public ResourceSection() {
		super();
		resources = new ArrayList<>();
	}
	
	@Override
	protected void updateComponent() {
		uiElements = new ArrayList<>();
		if (PostGameModel.reward.getResources().size() > 0) {
			int i = 0;
			for (ResourceChange resourceChange : resources) {
				ElementStack resourceLabels = DefaultLabelFactory.createResourceStack(resourceChange.getCurrentResource());
				resourceLabels.setFixedWidth(getBounds().getWidth());
				resourceLabels.setPosition(getBounds().x, getBounds().y + getBounds().height - (itemHeight * i));
				i++;
				uiElements.add(resourceLabels);
				
				if (resourceChange.getChangeAmount() > 0) {
					Label changeLabel = new Label("+" + StringUtils.formatBigNumber(resourceChange.getChangeAmount(), 1));
					changeLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
					changeLabel.setColor(Inventory.getColorForResource(resourceChange.getCurrentResource().getType()));
					changeLabel.setFixedWidth(GraphicsUtil.screenBounds().width - (resourceLabels.getBounds().x + resourceLabels.getFixedWidth() + 5));
					changeLabel.setPosition(resourceLabels.getBounds().x + resourceLabels.getFixedWidth() + 5, resourceLabels.getBounds().y);
					uiElements.add(changeLabel);
				}
			}
		} else {
			Label noResourcesLabel = new Label("No resources gained");
			noResourcesLabel.setFixedWidth(getBounds().getWidth());
			noResourcesLabel.setPosition(getBounds().x, getBounds().y);
			uiElements.add(noResourcesLabel);
		}
	}
	
	@Override
	protected void renderComponent() {
		for (UIElement element : uiElements) {
			element.render();
		}
	}

	@Override
	public float calculateHeight() {
		return Math.max(resources.size() * itemHeight, itemHeight);
	}

	@Override
	public float calculateWidth() {
		return getFixedWidth();
	}

	@Override
	public void dispose() {
		if (uiElements != null) {
			for (UIElement element : uiElements) {
				element.dispose();
			}
		}
	}

}
