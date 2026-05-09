package de.instinct.eqfleet.menu.module.mining.tab.mission.model;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.mining.dto.player.MissionData;
import de.instinct.engine.mining.entity.asteroid.ResourceType;
import de.instinct.engine_api.mining.service.model.MiningMissionOverview;
import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.menu.module.mining.MiningMenu;
import de.instinct.eqfleet.menu.module.mining.MissionStatus;
import de.instinct.eqfleet.mining.frontend.OreManager;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MissionOverview extends Component {
	
	private final Color missionCompletedColor = new Color(0.2f, 0.7f, 0.2f, 1f);
	private final Color missionAvailableColor = new Color(0.7f, 0.7f, 0.2f, 1f);
	private final Color missionLockedColor = new Color(0.2f, 0.2f, 0.2f, 1f);
	
	private HoloPanel missionPanel;
	
	private Label missionNameLabel;
	private Label asteroidLabel;
	private Label resourceLabel;
	private Label resourceTypeLabel;
	
	private MissionData selectedMission;
	private MiningMissionOverview selectedMissionOverview;
	private Color currentColor;
	
	public MissionOverview() {
		super();
		missionPanel = HoloPanel.builder()
				.build();
		missionPanel.getStyle().setReflectionStrength(0f);
		missionNameLabel = new Label("");
		asteroidLabel = new Label("");
		asteroidLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		resourceLabel = new Label("");
		resourceTypeLabel = new Label("");
		resourceTypeLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		currentColor = new Color();
	}
	
	@Override
	protected void updateComponent() {
		missionPanel.setBounds(getBounds());
		if (selectedMissionOverview != null) {
			MissionStatus status = MiningMenu.getMissionStatus(selectedMissionOverview, selectedMission);
			currentColor.set(getMissionColor(status));
			missionPanel.setColor(currentColor);
			missionNameLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 30, getBounds().width - 20, 20);
			asteroidLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 60, getBounds().width - 20, 20);
			resourceLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 120, getBounds().width - 20, 30);
			resourceTypeLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 120, getBounds().width - 20, 20);
			
			switch (status) {
			case COMPLETED:
				updateCompleted();
				break;
			case AVAILABLE:
				updateAvailable();
				break;
			case LOCKED:
				updateLocked();
				break;
			}
		}
	}

	private void updateCompleted() {
		missionNameLabel.setText(selectedMissionOverview.getName().toUpperCase());
		asteroidLabel.setText("Asteroids: " + selectedMissionOverview.getAsteroids());
		resourceLabel.setText("Resources");
	}
	
	private void updateAvailable() {
		missionNameLabel.setText(selectedMissionOverview.getName().toUpperCase());
		asteroidLabel.setText("Asteroids: " + (selectedMission != null ? selectedMission.getMinedAsteroids() : 0) + " / " + selectedMissionOverview.getAsteroids());
		resourceLabel.setText("Resources");
	}
	
	private void updateLocked() {
		missionNameLabel.setText("???");
		asteroidLabel.setText("LOCKED");
		resourceLabel.setText("Clear previous mission\nin one deployment");
	}

	private Color getMissionColor(MissionStatus status) {
		switch (status) {
		case COMPLETED:
			return missionCompletedColor;
		case AVAILABLE:
			return missionAvailableColor;
		case LOCKED:
			return missionLockedColor;
		}
		return currentColor;
	}
	
	@Override
	protected void renderComponent() {
		HoloRenderer.drawPanel(missionPanel);
		missionNameLabel.render();
		asteroidLabel.render();
		resourceLabel.render();
		if (MiningMenu.getMissionStatus(selectedMissionOverview, selectedMission) != MissionStatus.LOCKED) {
			for (ResourceType resourceType : selectedMissionOverview.getAvailableResources()) {
				resourceTypeLabel.getBounds().y -= 20;
				resourceTypeLabel.setText(resourceType.toString());
				resourceTypeLabel.setColor(OreManager.getColorForResourceType(resourceType));
				resourceTypeLabel.render();
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

	public Color getCurrentColor() {
		return currentColor;
	}

}
