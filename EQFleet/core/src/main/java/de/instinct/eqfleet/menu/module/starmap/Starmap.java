package de.instinct.eqfleet.menu.module.starmap;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.starmap.dto.StartConquestRequest;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.social.SocialModel;
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
				() -> API.starmap().data(API.authKey),
			    result -> {
			    	StarmapModel.starmapData = result;
			    }
		);
		WebManager.enqueue(
				() -> API.starmap().sector(getFactionMode()),
			    result -> {
			    	StarmapModel.sectorData = result;
			    	StarmapModel.dataUpdated = true;
			    }
		);
	}
	
	private FactionMode getFactionMode() {
		if (SocialModel.groupData != null) {
			if (SocialModel.groupData.getMembers().size() == 2) return FactionMode.TWO_VS_TWO;
			if (SocialModel.groupData.getMembers().size() == 3) return FactionMode.THREE_VS_THREE;
		}
		return FactionMode.ONE_VS_ONE;
	}

	@Override
	public void close() {
		
	}

	private void startConquest(int galaxyId, int starsystemId) {
		StartConquestRequest request = new StartConquestRequest();
		request.setGroupToken(SocialModel.playerData.getGroupToken());
		request.setUserToken(API.authKey);
		request.setGalaxyId(galaxyId);
		request.setSystemId(starsystemId);
		WebManager.enqueue(
			    () -> API.starmap().start(request),
			    resultStart -> {
			    	
			    }
		);
	}

}
