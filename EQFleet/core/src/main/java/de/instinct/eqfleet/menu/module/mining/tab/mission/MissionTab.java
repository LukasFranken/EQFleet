package de.instinct.eqfleet.menu.module.mining.tab.mission;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.mining.dto.player.MissionData;
import de.instinct.engine_api.mining.service.model.MiningMissionOverview;
import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.mining.MiningMenu;
import de.instinct.eqfleet.menu.module.mining.MiningMenuModel;
import de.instinct.eqfleet.menu.module.mining.MissionStatus;
import de.instinct.eqfleet.menu.module.mining.message.types.LoadMissionMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.StartMessage;
import de.instinct.eqfleet.menu.module.mining.tab.mission.model.MissionOverview;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class MissionTab extends Component {
	
	private MissionOverview missionOverview;
	private ColorButton startButton;
	private HoloPanel buttonPanel;
	
	private ColorButton leftButton;
	private ColorButton rightButton;
	
	private MissionData selectedMission;
	private MiningMissionOverview selectedMissionOverview;
	
	private String selectedMissionName;
	
	public MissionTab() {
		missionOverview = new MissionOverview();
		buttonPanel = HoloPanel.builder()
				.build();
		
		leftButton = new ColorButton("<--");
		leftButton.getLabel().setType(FontType.MEDIUM_BOLD);
		leftButton.getBorder().setColor(Color.GRAY);
		leftButton.setAction(new Action() {
			
			@Override
			public void execute() {
				previousMission();
			}
		});
		
		rightButton = new ColorButton("-->");
		rightButton.getLabel().setType(FontType.MEDIUM_BOLD);
		rightButton.getBorder().setColor(Color.GRAY);
		rightButton.setAction(new Action() {
			
			@Override
			public void execute() {
				nextMission();
			}
		});
		
		startButton = new ColorButton("Deploy");
		startButton.getLabel().setType(FontType.MEDIUM_BOLD);
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (selectedMissionOverview == null) return;
				MiningMenuModel.messageQueue.add(StartMessage.builder()
						.map(selectedMissionOverview.getName())
						.build());
			}
		});
	}

	@Override
	protected void updateComponent() {
		updateCurrentMission();
		setMissionData();
		
		missionOverview.getBounds().set(MenuModel.moduleBounds.x + 40, MenuModel.moduleBounds.y + 160, MenuModel.moduleBounds.width - 80, MenuModel.moduleBounds.height - 200);
		startButton.setBounds(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2f) - 50, missionOverview.getBounds().y - 45f, 100, 40);
		startButton.getLabel().setColor(missionOverview.getCurrentColor());
		startButton.getBorder().setColor(missionOverview.getCurrentColor());
		startButton.getHoverColor().set(missionOverview.getCurrentColor());
		startButton.getHoverColor().a = 0.2f;
		startButton.getDownColor().set(missionOverview.getCurrentColor());
		startButton.getHoverColor().a = 0.4f;
		buttonPanel.setBounds(startButton.getBounds());
		buttonPanel.setColor(missionOverview.getCurrentColor());
		
		leftButton.setBounds(startButton.getBounds().x - 60, startButton.getBounds().y, 50, 40);
		rightButton.setBounds(startButton.getBounds().x + startButton.getBounds().width + 10, startButton.getBounds().y, 50, 40);
	}
	
	private void nextMission() {
		if (selectedMissionOverview != null && !selectedMissionOverview.getName().equals(MiningMenuModel.missions.getNames().get(MiningMenuModel.missions.getNames().size() - 1))) {
			for (int i = 0; i < MiningMenuModel.missions.getNames().size(); i++) {
				if (MiningMenuModel.missions.getNames().get(i).equals(selectedMissionOverview.getName())) {
					selectedMissionName = MiningMenuModel.missions.getNames().get(i + 1);
					break;
				}
			}
		}
	}
	
	private void previousMission() {
		if (selectedMissionOverview != null && !selectedMissionOverview.getName().equals(MiningMenuModel.missions.getNames().get(0))) {
			for (int i = 0; i < MiningMenuModel.missions.getNames().size(); i++) {
				if (MiningMenuModel.missions.getNames().get(i).equals(selectedMissionOverview.getName())) {
					selectedMissionName = MiningMenuModel.missions.getNames().get(i - 1);
					break;
				}
			}
		}
	}
	
	private void updateCurrentMission() {
		if (MiningMenuModel.missions != null && MiningMenuModel.playerMissionData != null) {
			if (selectedMissionName == null) {
				if (MiningMenuModel.playerMissionData.getMissions().isEmpty()) {
					selectedMissionName = MiningMenuModel.missions.getNames().get(0);
				} else {
					for (MissionData missionData : MiningMenuModel.playerMissionData.getMissions()) {
						MiningMissionOverview missionOverview = getMissionOverview(missionData.getName());
						if (missionOverview == null) continue;
						MissionStatus status = MiningMenu.getMissionStatus(missionOverview, missionData);
						if (status == MissionStatus.AVAILABLE || status == MissionStatus.COMPLETED) {
							selectedMissionName = missionOverview.getName();
						}
					}
				}
			}
			
		}
	}

	private MiningMissionOverview getMissionOverview(String name) {
		for (MiningMissionOverview miningMissionOverview : MiningMenuModel.missionOverviews) {
			if (miningMissionOverview.getName() == null) continue;
			if (miningMissionOverview.getName().equals(name)) {
				return miningMissionOverview;
			}
		}
		if (!MiningMenuModel.requestedMissionData) {
			MiningMenuModel.requestedMissionData = true;
			MiningMenuModel.messageQueue.add(LoadMissionMessage.builder().name(name).build());
		}
		return null;
	}
	
	private void setMissionData() {
		if (selectedMissionName == null) return;
		this.selectedMissionOverview = getMissionOverview(selectedMissionName);
		missionOverview.setSelectedMissionOverview(selectedMissionOverview);
		if (selectedMissionOverview == null) return;
		this.selectedMission = getMissionData();
		missionOverview.setSelectedMission(selectedMission);
	}
	
	private MissionData getMissionData() {
		for (MissionData missionData : MiningMenuModel.playerMissionData.getMissions()) {
			if (missionData.getName().equals(selectedMissionOverview.getName())) {
				return missionData;
			}
		}
		return null;
	}

	@Override
	protected void renderComponent() {
		if (selectedMissionOverview != null && MiningMenuModel.playerMissionData != null) {
			missionOverview.render();
			
			if (!selectedMissionOverview.getName().contentEquals(MiningMenuModel.missions.getNames().get(0))) {
				leftButton.render();
			}
			if (!selectedMissionOverview.getName().contentEquals(MiningMenuModel.missions.getNames().get(MiningMenuModel.missions.getNames().size() - 1))) {
				if (selectedMission != null) {
					rightButton.render();
				}
			}
			
			if (MiningMenu.getMissionStatus(selectedMissionOverview, selectedMission) != MissionStatus.LOCKED) {
				startButton.render();
				HoloRenderer.drawPanel(buttonPanel);
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
		startButton.dispose();
		missionOverview.dispose();
	}

}
