package de.instinct.eqfleet.mining.driver.online;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.engine.core.net.GameOrderMessage;
import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.mining.net.message.ConnectMessage;
import de.instinct.engine.mining.net.message.OnboardMessage;
import de.instinct.engine.mining.net.message.StartMessage;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.driver.MiningDriver;

public class MiningOnlineDriver extends MiningDriver {
	
	private MiningClient client;
	private long STATE_TIME_BUFFER_MS = 200;

	@Override
	public void initialize() {
		MiningOnlineModel.networkMessages = new ConcurrentLinkedQueue<>();
		client = new MiningClient();
		client.start();
		ConnectMessage connectMessage = new ConnectMessage();
		connectMessage.senderUUID = API.authKey;
		client.sendMessage(connectMessage);
	}

	@Override
	public void updateDriver() {
		while (!MiningOnlineModel.networkMessages.isEmpty()) {
			NetworkMessage message = MiningOnlineModel.networkMessages.poll();
			if (message instanceof OnboardMessage) {
				OnboardMessage onboardMessage = (OnboardMessage) message;
				MiningModel.state = onboardMessage.initialGameState;
				MiningModel.playerId = onboardMessage.assignedPlayerId;
			}
			if (message instanceof StartMessage) {
				MiningModel.state.metaData.started = true;
				MiningModel.state.metaData.pauseData.resumeCountdownMS += STATE_TIME_BUFFER_MS;
			}
			if (message instanceof GameOrderMessage) {
				GameOrderMessage orderMessage = (GameOrderMessage) message;
				stateManager.integrateOrder(MiningModel.state, orderMessage.order);
			}
		}
	}

	@Override
	public void integrateOrder(GameOrder order) {
		GameOrderMessage orderMessage = new GameOrderMessage();
		orderMessage.order = order;
		orderMessage.gameUUID = MiningModel.state.metaData.gameUUID;
		orderMessage.senderUUID = API.authKey;
		client.sendMessage(orderMessage);
	}

	@Override
	public void dispose() {
		MiningModel.state = null;
		MiningModel.playerId = 0;
		client.dispose();
	}

}
