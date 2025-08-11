package de.instinct.eqfleet.menu.postgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.commander.dto.CommanderStat;
import de.instinct.api.commander.dto.CommanderUpgrade;
import de.instinct.api.commander.dto.RankUpCommanderUpgrade;
import de.instinct.api.core.API;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.inventory.message.LoadResourcesMessage;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.message.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
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

public class PostGameRenderer extends BaseModuleRenderer {
	
	private final float PER_ITEM_DURATION_MS = 1f;
	
	private ColorButton claimButton;
	
	private List<PostGameElement> elements;
	
	private boolean halted;
	
	public PostGameRenderer() {
		claimButton = DefaultButtonFactory.colorButton("Claim", new Action() {
			
			@Override
			public void execute() {
				PostGameModel.reward = null;
				if (PlayModel.lobbyStatus.getType().getGameMode() == GameMode.CONQUEST) Play.leaveLobby();
				Menu.queue(LoadProfileMessage.builder().build());
				Menu.queue(LoadResourcesMessage.builder().build());
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
		if (!halted) {
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
				if (element.getAnimationAction() != null) {
					float progression = element.getElapsed() / element.getDuration();
					element.getAnimationAction().update(progression);
				}
				if (thisFrameDelta <= 0) {
					break;
				}
			}
		}
	}

	@Override
	public void reload() {
		if (PostGameModel.reward != null) {
			halted = false;
			elements = new ArrayList<>();
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS / 2)
					.build());
			
			Label header = new Label("Results");
			header.setType(FontType.LARGE);
			header.setFixedHeight(50);
			header.setFixedWidth(GraphicsUtil.baseScreenBounds().width);
			header.setPosition(0, GraphicsUtil.baseScreenBounds().getHeight() - 100);
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS / 2)
					.uiElement(header)
					.build());
			
			ExperienceSection experienceSection = new ExperienceSection();
			experienceSection.setRankImagesEnabled(false);
			experienceSection.init(50, GraphicsUtil.baseScreenBounds().getHeight() / 2 + 50, GraphicsUtil.baseScreenBounds().getWidth() - 100);
			
			ElementStack experienceLabels = DefaultLabelFactory.createLabelStack(
					"EXP",
					StringUtils.formatBigNumber(PostGameModel.reward.getExperience()),
					GraphicsUtil.baseScreenBounds().width / 2);
			experienceLabels.setPosition(GraphicsUtil.baseScreenBounds().width / 4, experienceSection.getBounds().y + experienceSection.getActualHeight() + 20);
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS)
					.uiElement(experienceLabels)
					.build());
			
			elements.add(PostGameElement.builder()
					.duration(PER_ITEM_DURATION_MS * (PostGameModel.reward.getExperience() > 10 ? 3 : 1))
					.uiElement(experienceSection)
					.animationAction(new AnimationAction() {
						
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
							newRankLabel.setFixedHeight(30);
							newRankLabel.setFixedWidth(120);
							newRankLabel.setType(FontType.SMALL);
							rankUpElementList.getElements().add(newRankLabel);
							
							for (CommanderUpgrade upgrade : rankUpCommanderUpgrade.getUpgrades()) {
								rankUpElementList.getElements().add(DefaultLabelFactory.createLabelStack(upgrade.getStat().getLabel(), "+" + (upgrade.getStat() == CommanderStat.CP_PER_SECOND ? StringUtils.format(upgrade.getValue(), 2) : StringUtils.format(upgrade.getValue(), 0)), 120));
							}
							
							ColorButton acceptButton = DefaultButtonFactory.colorButton("Accept", new Action() {
								
								@Override
								public void execute() {
									PopupRenderer.close();
									halted = false;
								}
								
							});
							acceptButton.setFixedWidth(100);
							acceptButton.setFixedHeight(30);
							rankUpElementList.getElements().add(acceptButton);
							
							PopupRenderer.create(Popup.builder()
									.title("Promotion")
									.contentContainer(rankUpElementList)
									.closeOnClickOutside(false)
									.build());
						}
						
					})
					.build());
			
			if (PostGameModel.reward.getResources().size() > 0) {
				int i = 0;
				for (ResourceAmount resource : PostGameModel.reward.getResources()) {
					ElementStack resourceLabels = DefaultLabelFactory.createLabelStack(
							resource.getType().toString(),
							StringUtils.formatBigNumber(resource.getAmount()),
							GraphicsUtil.baseScreenBounds().width / 2);
					resourceLabels.setPosition(GraphicsUtil.baseScreenBounds().width / 4, (GraphicsUtil.baseScreenBounds().getHeight() / 2) - 50 - (30 * i));
					elements.add(PostGameElement.builder()
							.duration(PER_ITEM_DURATION_MS)
							.uiElement(resourceLabels)
							.build());
					i++;
				}
			} else {
				Label noResourcesLabel = new Label("No resources gained");
				noResourcesLabel.setPosition(GraphicsUtil.baseScreenBounds().width / 4, (GraphicsUtil.baseScreenBounds().getHeight() / 2) - 50);
				elements.add(PostGameElement.builder()
						.duration(PER_ITEM_DURATION_MS)
						.uiElement(noResourcesLabel)
						.build());
			}
			
			claimButton.setPosition((GraphicsUtil.baseScreenBounds().width / 2) - (claimButton.getFixedWidth() / 2), 70);
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
