package de.instinct.eqfleet.menu.module.starmap;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqfleet.menu.module.starmap.message.StarmapMessage;
import de.instinct.eqfleet.menu.module.starmap.message.types.ReloadStarmapMessage;
import de.instinct.eqfleet.menu.module.starmap.message.types.StartConquestMessage;
import de.instinct.eqfleet.net.WebManager;

public class Starmap extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.STARMAP;
	}

	@Override
	public void init() {
		StarmapModel.selectedGalaxyId = -1;
		StarmapModel.selectedStarsystemId = -1;
		StarmapModel.messageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void load() {
		loadData();
	}

	@Override
	public void update() {
		if (!StarmapModel.messageQueue.isEmpty()) {
			process(StarmapModel.messageQueue.poll());
		}
	}

	public void process(StarmapMessage message) {
		if (message instanceof ReloadStarmapMessage) {
			loadData();
		}
		if (message instanceof StartConquestMessage) {
			StartConquestMessage startConquestMessage = (StartConquestMessage) message;
			startConquest(startConquestMessage.getGalaxyId(), startConquestMessage.getStarsystemId());
		}
	}
	
	private void loadData() {
		WebManager.enqueue(
				() -> API.starmap().data(),
			    result -> {
			    	StarmapModel.starmapData = result;
			    	StarmapModel.dataUpdated = true;
			    }
		);
	}

	@Override
	public void close() {
		
	}

	private void startConquest(int galaxyId, int starsystemId) {
		WebManager.enqueue(
			    () -> API.matchmaking().create(),
			    resultCreateLobby -> {
			    	PlayModel.lobbyUUID = resultCreateLobby.getLobbyUUID();
			    	WebManager.enqueue(
						    () -> API.matchmaking().settype(PlayModel.lobbyUUID, 
						    		GameType.builder()
						    		.map(galaxyId + "_" + starsystemId)
						    		.factionMode(FactionMode.ONE_VS_ONE)
						    		.gameMode(GameMode.CONQUEST)
						    		.versusMode(VersusMode.AI)
						    		.build()),
						    result -> {
						    	Play.startMatchmaking();
						    }
					);
			    }
		);
	}

}
