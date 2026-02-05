package de.instinct.eqfleet.menu.postgame.elements;

import de.instinct.api.matchmaking.dto.ShipResult;
import de.instinct.eqfleet.menu.module.ship.component.shippart.ShipProgressSection;
import de.instinct.eqfleet.menu.postgame.model.AnimationAction;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import lombok.Data;

@Data
public class PostGameShipProgressOverview implements PostGameElement {

	private float itemDuration;
	private boolean halted;
	private float elapsed;
	private ShipProgressSection uiElement;
	private AnimationAction animationAction;
	
	public PostGameShipProgressOverview(float itemDuration, ShipResult result, int offset) {
		this.itemDuration = itemDuration;
		uiElement = new ShipProgressSection(result);
		uiElement.setFixedWidth(GraphicsUtil.screenBounds().width / 2f);
		uiElement.setPosition(GraphicsUtil.screenBounds().width / 4f, GraphicsUtil.screenBounds().height - 270 - offset - getHeight());
		buildAnimationAction();
	}

	private void buildAnimationAction() {
		animationAction = new AnimationAction() {
			
			@Override
			public void update(float progression) {
				uiElement.setProgress(progression);
			}
		};
	}
	
	public float getHeight() {
		return uiElement.calculateHeight();
	}
	
	@Override
	public float getDuration() {
		return itemDuration * 2f;
	}

}
