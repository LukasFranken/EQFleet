package de.instinct.eqfleet.menu.module.mining;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;

import de.instinct.api.core.API;
import de.instinct.api.mining.dto.CreateSessionRequest;
import de.instinct.api.mining.dto.CreateSessionResponse;
import de.instinct.api.mining.dto.player.MissionData;
import de.instinct.engine_api.core.EngineAPI;
import de.instinct.engine_api.mining.service.model.MiningMissionOverview;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.mining.message.MiningMenuMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.LoadInventoryMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.LoadMiningMenuMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.LoadMissionMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.LoadMissionsMessage;
import de.instinct.eqfleet.menu.module.mining.message.types.StartMessage;
import de.instinct.eqfleet.mining.MiningMode;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.model.ConnectionStatus;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;

public class MiningMenu extends BaseModule {

	@Override
	public void init() {
		MiningMenuModel.messageQueue = new ConcurrentLinkedQueue<>();
		MiningMenuModel.missionOverviews = new ArrayList<>();
	}

	@Override
	public void load() {
		MiningMenuModel.missions = null;
		MiningMenuModel.playerMissionData = null;
		MiningMenuModel.inventory = null;
		MiningMenuModel.missionOverviews.clear();
		loadData();
	}
	
	private void loadData() {
		loadMissions();
		loadInventory();
	}

	@Override
	public void update() {
		if (!MiningMenuModel.messageQueue.isEmpty()) {
			process(MiningMenuModel.messageQueue.poll());
		}
	}
	
	private void process(MiningMenuMessage message) {
		if (message instanceof LoadMiningMenuMessage) {
			loadData();
		}
		if (message instanceof LoadInventoryMessage) {
			loadInventory();
		}
		if (message instanceof LoadMissionsMessage) {
			loadMissions();
		}
		if (message instanceof LoadMissionMessage) {
			LoadMissionMessage loadMissionMessage = (LoadMissionMessage) message;
			loadMissionData(loadMissionMessage.getName());
		}
		if (message instanceof StartMessage) {
			StartMessage startMessage = (StartMessage) message;
			if (WebManager.status == ConnectionStatus.ONLINE) {
				MiningModel.mode = MiningMode.ONLINE;
				CreateSessionRequest request = new CreateSessionRequest();
				request.setPlayerUUIDs(new ArrayList<>());
				request.getPlayerUUIDs().add(API.authKey);
				request.setMap(startMessage.getMap());
				WebManager.enqueue(
						() -> API.mining().createSession(request),
						result -> {
							if (result == CreateSessionResponse.SUCCESS || result == CreateSessionResponse.ALREADY_IN_SESSION) {
								Gdx.app.postRunnable(() -> {
									SceneManager.changeTo(SceneType.MINING);
								});
							}
						}
				);
			} else {
				MiningModel.mode = MiningMode.OFFLINE;
				SceneManager.changeTo(SceneType.MINING);
			}
		}
	}

	private void loadInventory() {
		WebManager.enqueue(
			    () -> EngineAPI.mining().inventory(API.authKey),
			    result -> {
			    	MiningMenuModel.inventory = result;
			    }
		);
	}
	
	private void loadMissions() {
		WebManager.enqueue(
			    () -> EngineAPI.mining().maps(),
			    missions -> {
			    	MiningMenuModel.missions = missions;
			    }
		);
		WebManager.enqueue(
			    () -> EngineAPI.mining().missiondata(API.authKey),
			    result -> {
			    	MiningMenuModel.playerMissionData = result;
			    }
		);
	}
	
	private void loadMissionData(String name) {
		WebManager.enqueue(
			    () -> EngineAPI.mining().mission(name),
			    miningMissionOverview -> {
			    	MiningMenuModel.missionOverviews.add(miningMissionOverview);
			    	MiningMenuModel.requestedMissionData = false;
			    }
		);
	}
	
	public static MissionStatus getMissionStatus(MiningMissionOverview selectedMissionOverview, MissionData selectedMission) {
		if (selectedMissionOverview == null) return MissionStatus.LOCKED;
		if (selectedMission == null) {
			int missionIndex = MiningMenuModel.missionOverviews.indexOf(selectedMissionOverview);
			if (missionIndex <= 0) {
				return MissionStatus.AVAILABLE;
			} else {
				MiningMissionOverview previousMission = MiningMenuModel.missionOverviews.get(missionIndex - 1);
				MissionData previousMissionData = null;
				for (MissionData missionData : MiningMenuModel.playerMissionData.getMissions()) {
					if (missionData.getName().equals(previousMission.getName())) {
						previousMissionData = missionData;
						break;
					}
				}
				if (previousMissionData != null && previousMissionData.isCompleted()) {
					return MissionStatus.AVAILABLE;
				} else {
					return MissionStatus.LOCKED;
				}
			}
		} else {
			if (selectedMission.isCompleted()) {
				return MissionStatus.COMPLETED;
			} else {
				return MissionStatus.AVAILABLE;
				
			}
		}
	}

	@Override
	public void close() {
		
	}

}
