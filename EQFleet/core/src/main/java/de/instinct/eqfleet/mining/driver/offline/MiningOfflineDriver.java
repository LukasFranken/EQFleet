package de.instinct.eqfleet.mining.driver.offline;

import java.util.ArrayList;

import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.player.MiningPlayer;
import de.instinct.engine_api.mining.model.MiningGameStateInitialization;
import de.instinct.engine_api.mining.model.MiningMap;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.driver.MiningDriver;

public class MiningOfflineDriver extends MiningDriver {

	@Override
	public void initialize() {
		MiningModel.state = engineInterface.initializeMining(getMiningGameStateInitialization());
		MiningModel.playerId = 1;
		MiningModel.state.metaData.started = true;
	}
	
	private MiningGameStateInitialization getMiningGameStateInitialization() {
		MiningGameStateInitialization initialization = new MiningGameStateInitialization();
		initialization.setMap(new MiningMap());
		initialization.setGameUUID("custom");
		initialization.setPauseTimeLimitMS(60);
		initialization.setPauseCountLimit(3);
		
		initialization.setPlayers(new ArrayList<>());
		MiningPlayer player1 = engineInterface.getTestPlayer();
		player1.id = 1;
		initialization.getPlayers().add(player1);
		return initialization;
	}

	@Override
	public void updateDriver() {
		
	}
	
	@Override
	public void integrateOrder(GameOrder order) {
		engineInterface.integrateOrder(MiningModel.state, order);
	}

	@Override
	public void dispose() {
		MiningModel.state = null;
		MiningModel.playerId = 0;
	}

}
