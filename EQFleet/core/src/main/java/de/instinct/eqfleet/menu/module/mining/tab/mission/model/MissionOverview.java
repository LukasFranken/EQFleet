package de.instinct.eqfleet.menu.module.mining.tab.mission.model;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.mining.dto.player.MissionData;
import de.instinct.engine_api.mining.service.model.MiningMissionOverview;
import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.menu.module.mining.MiningMenu;
import de.instinct.eqfleet.menu.module.mining.MissionStatus;
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
		currentColor = new Color();
	}
	
	@Override
	protected void updateComponent() {
		missionPanel.setBounds(getBounds());
		if (selectedMissionOverview != null) {
			currentColor.set(getMissionColor(MiningMenu.getMissionStatus(selectedMissionOverview, selectedMission)));
			missionPanel.setColor(currentColor);
			missionNameLabel.setText(selectedMissionOverview.getName().toUpperCase());
			missionNameLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 30, getBounds().width - 20, 20);
			
			asteroidLabel.setText("Asteroids: " + (selectedMission != null ? selectedMission.getMinedAsteroids() : 0) + " / " + selectedMissionOverview.getAsteroids());
			asteroidLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - 60, getBounds().width - 20, 20);
		}
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
