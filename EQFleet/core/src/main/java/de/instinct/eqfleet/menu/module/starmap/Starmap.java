package de.instinct.eqfleet.menu.module.starmap;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.ModuleMessage;
import de.instinct.eqfleet.menu.module.play.Play;
import de.instinct.eqfleet.menu.module.play.PlayModel;
import de.instinct.eqfleet.menu.module.starmap.message.ReloadStarmapMessage;
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
	}

	@Override
	public void open() {
		Menu.queue(ReloadStarmapMessage.builder().build());
	}

	@Override
	public void update() {

	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadStarmapMessage) {
			WebManager.enqueue(
					() -> API.starmap().data(),
				    result -> {
				    	StarmapModel.starmapData = result;
				    	super.requireUIReload();
				    }
			);
			return true;
		}
		return false;
	}

	@Override
	public void close() {
		
	}

	public static void createLobby(int galaxyId, int starsystemId) {
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
