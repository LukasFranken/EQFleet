package de.instinct.eqfleet.menu.module.mining.tab;

import java.util.ArrayList;

import de.instinct.api.core.API;
import de.instinct.api.mining.dto.CreateSessionRequest;
import de.instinct.api.mining.dto.CreateSessionResponse;
import de.instinct.eqfleet.mining.MiningMode;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class MissionTab extends Component {
	
	private ColorButton startOnlineButton;
	private ColorButton startButton;
	
	public MissionTab() {
		startOnlineButton = new ColorButton("Online");
		startOnlineButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mode = MiningMode.ONLINE;
				CreateSessionRequest request = new CreateSessionRequest();
				request.setPlayerUUIDs(new ArrayList<>());
				request.getPlayerUUIDs().add(API.authKey);
				request.setMap("intro");
				WebManager.enqueue(
						() -> API.mining().createSession(request),
						result -> {
							if (result == CreateSessionResponse.SUCCESS || result == CreateSessionResponse.ALREADY_IN_SESSION) {
								SceneManager.changeTo(SceneType.MINING);
							}
						}
				);
			}
		});
		
		startButton = new ColorButton("Offline");
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mode = MiningMode.OFFLINE;
				SceneManager.changeTo(SceneType.MINING);
			}
		});
	}

	@Override
	protected void updateComponent() {
		startOnlineButton.setBounds(150, 370, 100, 40);
		startButton.setBounds(150, 430, 100, 40);
	}
	
	@Override
	protected void renderComponent() {
		startOnlineButton.render();
		startButton.render();
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
		startOnlineButton.dispose();
		startButton.dispose();
	}

}
