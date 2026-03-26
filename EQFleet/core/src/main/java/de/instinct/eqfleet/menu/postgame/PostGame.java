package de.instinct.eqfleet.menu.postgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.core.API;
import de.instinct.api.matchmaking.dto.ShipResult;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.postgame.elements.PostGameExperienceElement;
import de.instinct.eqfleet.menu.postgame.elements.PostGameResourceElement;
import de.instinct.eqfleet.menu.postgame.elements.PostGameShipProgressOverview;
import de.instinct.eqfleet.menu.postgame.model.DynamicPostGameElement;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class PostGame extends Scene {
	
	private float PER_ITEM_DURATION_MS = 0.8f;
	
	private ColorButton claimButton;
	private ColorButton skipButton;
	
	private List<PostGameElement> elements;
	
	private boolean halted;
	private boolean skipped;
	
	public PostGame() {
		skipButton = DefaultButtonFactory.colorButton("Skip", new Action() {
			
			@Override
			public void execute() {
				skipped = true;
			}
			
		});
		claimButton = DefaultButtonFactory.colorButton("Continue", new Action() {
			
			@Override
			public void execute() {
				PostGameModel.reward = null;
				SceneManager.changeTo(SceneType.MENU);
			}
			
		});
		elements = new ArrayList<>();
	}

	@Override
	public void init() {
		if (PostGameModel.dataUpdated) {
			halted = false;
			skipped = false;
			
			elements.add(DynamicPostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.build());
			
			if (PostGameModel.reward != null) {
				skipButton.setFixedWidth(100);
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
				
				int offset = 200;
				PostGameExperienceElement postGameExperienceElement = new PostGameExperienceElement(PER_ITEM_DURATION_MS, offset);
				elements.add(postGameExperienceElement);
				offset += postGameExperienceElement.getUiElement().calculateHeight();
				
				PostGameResourceElement postGameResourceElement = new PostGameResourceElement(PER_ITEM_DURATION_MS, offset);
				elements.add(postGameResourceElement);
				offset += postGameResourceElement.getHeight() + 20;
				
				for (ShipResult shipResult : PostGameModel.reward.getShipResults()) {
					PostGameShipProgressOverview postGameShipProgressOverview = new PostGameShipProgressOverview(PER_ITEM_DURATION_MS, shipResult, offset);
					elements.add(postGameShipProgressOverview);
					offset += postGameShipProgressOverview.getHeight() + 20;
				}
				
			} else {
				Label loadingLabel = new Label("Failed loading post game data\nGame results are saved\nYou can skip this screen");
				loadingLabel.setType(FontType.NORMAL);
				loadingLabel.setFixedHeight(50);
				loadingLabel.setFixedWidth(GraphicsUtil.screenBounds().width);
				loadingLabel.setPosition(0, GraphicsUtil.screenBounds().getHeight() / 2);
				elements.add(DynamicPostGameElement.builder()
						.duration(PER_ITEM_DURATION_MS)
						.uiElement(loadingLabel)
						.build());
			}
			claimButton.setFixedWidth(100);
			claimButton.setFixedHeight(30);
			claimButton.setPosition((GraphicsUtil.screenBounds().width / 2) - (claimButton.getFixedWidth() / 2), 50);
			elements.add(DynamicPostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(claimButton)
					.build());
			PostGameModel.dataUpdated = false;
		}
	}

	@Override
	public void open() {
		elements.clear();
		WebManager.enqueue(
			    () -> API.matchmaking().result(GameModel.lastGameUUID),
			    result -> {
			    	if (result == null) {
			    		Logger.log("Menu", "Failed to load post game data for UUID " + GameModel.lastGameUUID, ConsoleColor.RED);
			    	} else {
			    		PostGameModel.reward = result;
			    	}
			    	PostGameModel.dataUpdated = true;
			    },
			    error -> {
			    	Logger.log("Menu", "Failed to load post game data for UUID " + GameModel.lastGameUUID + " with error: " + error.getMessage(), ConsoleColor.RED);
			    	PostGameModel.dataUpdated = true;
			    }
		);
	}

	@Override
	public void update() {
		init();
		if (elements != null) {
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
	}
	
	@Override
	public void render() {
		if (elements != null) {
			Profiler.startFrame("POSTGAME_RNDR");
			for (PostGameElement element : elements) {
				if (element.getElapsed() > 0) {
					if (element.getUiElement() != null) {
						element.getUiElement().render();
					}
				}
			}
			Profiler.checkpoint("POSTGAME_RNDR", "render");
			Profiler.endFrame("POSTGAME_RNDR");
		}
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public void dispose() {
		
	}

}
