package de.instinct.eqfleet.menu.postgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class PostGameRenderer extends BaseModuleRenderer {
	
	private final float PER_ITEM_DURATION_MS = 1f;
	
	private ColorButton claimButton;
	
	private List<PostGameElement> elements;
	
	public PostGameRenderer() {
		claimButton = DefaultButtonFactory.colorButton("Claim", new Action() {
			
			@Override
			public void execute() {
				PostGameModel.reward = null;
			}
			
		});
	}

	@Override
	public void render() {
		update();
		for (PostGameElement element : elements) {
			if (element.getElapsed() > 0) {
				if (element.getUiElement() != null) {
					element.getUiElement().render();
				}
			}
		}
	}

	private void update() {
		float thisFrameDelta = Gdx.graphics.getDeltaTime();
		for (PostGameElement element : elements) {
			if (element.getElapsed() < element.getDuration()) {
				if (thisFrameDelta > element.getDuration() - element.getElapsed()) {
					float difference = element.getDuration() - element.getElapsed();
					element.setElapsed(element.getElapsed() + difference);
					thisFrameDelta -= difference;
				} else {
					element.setElapsed(element.getElapsed() + thisFrameDelta);
					thisFrameDelta = 0;
				}
			}
			if (thisFrameDelta <= 0) {
				break;
			}
		}
	}

	@Override
	public void reload() {
		if (PostGameModel.reward != null) {
			elements = new ArrayList<>();
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS / 2)
					.build());
			
			Label header = new Label("Results");
			header.setType(FontType.LARGE);
			header.setFixedHeight(50);
			header.setFixedWidth(GraphicsUtil.screenBounds().width);
			header.setPosition(0, Gdx.graphics.getHeight() - 100);
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS / 2)
					.uiElement(header)
					.build());
			
			ExperienceSection experienceSection = new ExperienceSection();
			experienceSection.init(50, Gdx.graphics.getHeight() / 2 + 50, Gdx.graphics.getWidth() - 100);
			
			ElementStack experienceLabels = DefaultLabelFactory.createLabelStack(
					"EXP",
					StringUtils.formatBigNumber(PostGameModel.reward.getExperience()),
					GraphicsUtil.screenBounds().width / 2);
			experienceLabels.setPosition(GraphicsUtil.screenBounds().width / 4, experienceSection.getBounds().y + experienceSection.getActualHeight() + 20);
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(experienceLabels)
					.build());
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(experienceSection)
					.build());
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS * 3)
					.uiElement(experienceSection)
					.build());
			
			int i = 0;
			for (ResourceAmount resource : PostGameModel.reward.getResources()) {
				ElementStack resourceLabels = DefaultLabelFactory.createLabelStack(
						resource.getType().toString(),
						StringUtils.formatBigNumber(resource.getAmount()),
						GraphicsUtil.screenBounds().width / 2);
				resourceLabels.setPosition(GraphicsUtil.screenBounds().width / 4, (Gdx.graphics.getHeight() / 2) - 50 - (30 * i));
				elements.add(PostGameElement.builder()
						.duration(PER_ITEM_DURATION_MS)
						.uiElement(resourceLabels)
						.build());
				i++;
			}
			
			claimButton.setPosition((GraphicsUtil.screenBounds().width / 2) - (claimButton.getFixedWidth() / 2), 50);
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(claimButton)
					.build());
		}
	}

	@Override
	public void dispose() {
		
	}

}
