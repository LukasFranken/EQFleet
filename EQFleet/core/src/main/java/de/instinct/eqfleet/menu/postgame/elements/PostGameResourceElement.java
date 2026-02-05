package de.instinct.eqfleet.menu.postgame.elements;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqfleet.menu.module.profile.inventory.element.ResourceChange;
import de.instinct.eqfleet.menu.module.profile.inventory.element.ResourceSection;
import de.instinct.eqfleet.menu.postgame.PostGameModel;
import de.instinct.eqfleet.menu.postgame.model.AnimationAction;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import lombok.Data;

@Data
public class PostGameResourceElement implements PostGameElement {
	
	private float itemDuration;
	private boolean halted;
	private float elapsed;
	private ResourceSection uiElement;
	private AnimationAction animationAction;
	
	private List<ResourceChange> currentResources;
	
	public PostGameResourceElement(float itemDuration) {
		uiElement = new ResourceSection();
		uiElement.setFixedWidth(GraphicsUtil.screenBounds().width / 2);
		this.itemDuration = itemDuration;
		
		buildAnimationAction();
	}
	
	private void buildAnimationAction() {
		animationAction = new AnimationAction() {
			
			@Override
			public void update(float progression) {
				currentResources = new ArrayList<>();
				for (ResourceAmount resource : PostGameModel.reward.getResources()) {
					long currentChange = (long)MathUtil.easeInOut(0, resource.getAmount(), progression);
					ResourceChange currentResource = new ResourceChange();
					currentResource.setCurrentResource(new ResourceAmount());
					currentResource.getCurrentResource().setType(resource.getType());
					currentResource.getCurrentResource().setAmount(Inventory.getResource(resource.getType()) + currentChange);
					currentResource.setChangeAmount(currentChange);
					currentResources.add(currentResource);
				}
				uiElement.setResources(currentResources);
				uiElement.setPosition(GraphicsUtil.screenBounds().width / 4, GraphicsUtil.screenBounds().getHeight() - 180 - uiElement.calculateHeight());
				uiElement.update();
			}
			
		};
	}

	@Override
	public float getDuration() {
		return itemDuration * 3f;
	}

}
