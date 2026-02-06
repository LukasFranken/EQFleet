package de.instinct.eqfleet.menu.postgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.matchmaking.dto.ShipResult;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.postgame.elements.PostGameExperienceElement;
import de.instinct.eqfleet.menu.postgame.elements.PostGameResourceElement;
import de.instinct.eqfleet.menu.postgame.elements.PostGameShipProgressOverview;
import de.instinct.eqfleet.menu.postgame.model.DynamicPostGameElement;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class PostGameRenderer extends BaseModuleRenderer {
	
	private float PER_ITEM_DURATION_MS = 0.8f;
	
	private ColorButton claimButton;
	private ColorButton skipButton;
	
	private List<PostGameElement> elements;
	
	private boolean halted;
	private boolean skipped;
	private boolean loaded;
	
	public PostGameRenderer() {
		skipButton = DefaultButtonFactory.colorButton("Skip", new Action() {
			
			@Override
			public void execute() {
				skipped = true;
			}
			
		});
		claimButton = DefaultButtonFactory.colorButton("Continue", new Action() {
			
			@Override
			public void execute() {
				Menu.load();
				PostGameModel.reward = null;
				loaded = false;
			}
			
		});
	}

	@Override
	public void render() {
		if (loaded) {
			update();
			for (PostGameElement element : elements) {
				if (element.getElapsed() > 0) {
					if (element.getUiElement() != null) {
						element.getUiElement().render();
					}
				}
			}
		}
	}

	private void update() {
		float thisFrameDelta = Gdx.graphics.getDeltaTime();
		boolean anyHalted = false;
		for (PostGameElement element : elements) {
			if (thisFrameDelta <= 0) break;
			if (element.getElapsed() < element.getDuration()) {
				if (!halted) {
					if (skipped) {
						element.setElapsed(element.getDuration());
					} else {
						if (thisFrameDelta > element.getDuration() - element.getElapsed()) {
							float difference = element.getDuration() - element.getElapsed();
							element.setElapsed(element.getElapsed() + difference);
							thisFrameDelta -= difference;
						} else {
							element.setElapsed(element.getElapsed() + thisFrameDelta);
							thisFrameDelta = 0;
						}
					}
					
					if (element.getAnimationAction() != null) {
						float progression = element.getElapsed() / element.getDuration();
						element.getAnimationAction().update(progression);
					}
				}
				if (element.isHalted()) {
					anyHalted = true;
					break;
				}
			}
		}
		halted = anyHalted;
	}

	@Override
	public void reload() {
		if (PostGameModel.reward != null) {
			halted = false;
			skipped = false;
			elements = new ArrayList<>();
			
			elements.add(DynamicPostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.build());
			
			skipButton.setFixedWidth(90);
			skipButton.setFixedHeight(30);
			skipButton.setPosition((GraphicsUtil.screenBounds().width / 2) - (skipButton.getFixedWidth() / 2), 50);
			elements.add(DynamicPostGameElement.builder()
					.duration(1f)
					.uiElement(skipButton)
					.build());
			
			Label header = new Label(PostGameModel.reward.getVictoryType().toString().replace("_", " "));
			header.setType(FontType.LARGE);
			header.setFixedHeight(50);
			header.setFixedWidth(GraphicsUtil.screenBounds().width);
			header.setPosition(0, GraphicsUtil.screenBounds().getHeight() - 110);
			elements.add(DynamicPostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(header)
					.build());
			
			elements.add(new PostGameExperienceElement(PER_ITEM_DURATION_MS));
			elements.add(new PostGameResourceElement(PER_ITEM_DURATION_MS));
			int offset = 0;
			for (ShipResult shipResult : PostGameModel.reward.getShipResults()) {
				PostGameShipProgressOverview postGameShipProgressOverview = new PostGameShipProgressOverview(PER_ITEM_DURATION_MS, shipResult, offset);
				elements.add(postGameShipProgressOverview);
				offset += postGameShipProgressOverview.getHeight() + 20;
			}
			
			claimButton.setFixedWidth(90);
			claimButton.setFixedHeight(30);
			claimButton.setPosition((GraphicsUtil.screenBounds().width / 2) - (claimButton.getFixedWidth() / 2), 50);
			elements.add(DynamicPostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(claimButton)
					.build());
			loaded = true;
		}
	}

	@Override
	public void dispose() {
		
	}

}
