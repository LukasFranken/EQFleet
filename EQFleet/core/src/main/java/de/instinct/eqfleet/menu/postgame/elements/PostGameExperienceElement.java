package de.instinct.eqfleet.menu.postgame.elements;

import de.instinct.api.commander.dto.CommanderStat;
import de.instinct.api.commander.dto.CommanderUpgrade;
import de.instinct.api.commander.dto.RankUpCommanderUpgrade;
import de.instinct.api.core.API;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
import de.instinct.eqfleet.menu.postgame.PostGameModel;
import de.instinct.eqfleet.menu.postgame.model.AnimationAction;
import de.instinct.eqfleet.menu.postgame.model.PostGameElement;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;

@Data
public class PostGameExperienceElement implements PostGameElement {
	
	private float itemDuration;
	private boolean halted;
	private float elapsed;
	private ExperienceSection uiElement;
	
	public PostGameExperienceElement(float itemDuration) {
		uiElement = new ExperienceSection();
		uiElement.init(50, GraphicsUtil.screenBounds().getHeight() - 150, GraphicsUtil.screenBounds().getWidth() - 100);
		this.itemDuration = itemDuration;
	}

	@Override
	public float getDuration() {
		return itemDuration * (PostGameModel.reward.getExperience() > 10 ? 3 : 1);
	}

	@Override
	public AnimationAction getAnimationAction() {
		return new AnimationAction() {
			
			private long startExperience = ProfileModel.profile.getCurrentExp();
			private long targetExperience = startExperience + PostGameModel.reward.getExperience();
			
			@Override
			public void update(float progression) {
				long currentExperience = (long)MathUtil.easeInOut(startExperience, targetExperience, progression);
				ProfileModel.profile.setCurrentExp(currentExperience);
				while (ProfileModel.profile.getRank().getNextRequiredExp() <= ProfileModel.profile.getCurrentExp()) {
					ProfileModel.profile.setRank(ProfileModel.profile.getRank().getNextRank());
					halted = true;
					createRankUpPopup();
				}
			}

			private void createRankUpPopup() {
				RankUpCommanderUpgrade rankUpCommanderUpgrade = API.commander().upgrade(ProfileModel.profile.getRank());
				ElementList rankUpElementList = new ElementList();
				rankUpElementList.setMargin(20);
				
				Image newRankImage = new Image(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getFileName()));
				newRankImage.setFixedWidth(120);
				newRankImage.setFixedHeight(100);
				rankUpElementList.getElements().add(newRankImage);
				
				Label newRankLabel = new Label(ProfileModel.profile.getRank().getLabel());
				newRankLabel.setFixedHeight(20);
				newRankLabel.setFixedWidth(120);
				newRankLabel.setType(FontType.SMALL);
				rankUpElementList.getElements().add(newRankLabel);
				
				for (CommanderUpgrade upgrade : rankUpCommanderUpgrade.getUpgrades()) {
					ElementStack stack = DefaultLabelFactory.createLabelStack(upgrade.getStat().getLabel(), "+" + (upgrade.getStat() == CommanderStat.CP_PER_SECOND ? StringUtils.format(upgrade.getValue(), 2) : StringUtils.format(upgrade.getValue(), 0)));
					stack.setFixedWidth(120);
					rankUpElementList.getElements().add(stack);
				}
				rankUpElementList.setFixedWidth(120);
				
				ColorButton acceptButton = DefaultButtonFactory.colorButton("Accept", new Action() {
					
					@Override
					public void execute() {
						PopupRenderer.close();
						halted = false;
					}
					
				});
				acceptButton.setFixedWidth(120);
				acceptButton.setFixedHeight(30);
				acceptButton.setLayer(1);
				rankUpElementList.getElements().add(acceptButton);
				
				PopupRenderer.create(Popup.builder()
						.title("Promotion")
						.contentContainer(rankUpElementList)
						.closeOnClickOutside(false)
						.build());
			}
			
		};
	}

	@Override
	public boolean isHalted() {
		return halted;
	}

}
