package de.instinct.eqfleet.menu.module.ship;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.shipyard.dto.ShipBuildResponse;
import de.instinct.api.shipyard.dto.UnuseShipResponseCode;
import de.instinct.api.shipyard.dto.UseShipResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.message.types.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.ship.message.ShipyardMessage;
import de.instinct.eqfleet.menu.module.ship.message.types.BuildShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.types.ReloadShipyardMessage;
import de.instinct.eqfleet.menu.module.ship.message.types.UnuseShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.types.UseShipMessage;
import de.instinct.eqfleet.net.WebManager;

public class Shipyard extends BaseModule {

	@Override
	public void init() {
		ShipyardModel.messageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void load() {
		loadData();
	}

	@Override
	public void update() {
		if (!ShipyardModel.messageQueue.isEmpty()) {
			process(ShipyardModel.messageQueue.poll());
		}
	}

	public void process(ShipyardMessage message) {
		if (message instanceof ReloadShipyardMessage) {
			loadData();
		}
		if (message instanceof UseShipMessage) {
			UseShipMessage useShipMessage = (UseShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().use(API.authKey, useShipMessage.getShipUUID()),
					result -> {
						if (result == UseShipResponseCode.SUCCESS) {
							loadData();
						}
					}
			);
		}
		if (message instanceof UnuseShipMessage) {
			UnuseShipMessage unuseShipMessage = (UnuseShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().unuse(API.authKey, unuseShipMessage.getShipUUID()),
					result -> {
						if (result == UnuseShipResponseCode.SUCCESS) {
							loadData();
						}
					}
			);
		}
		if (message instanceof BuildShipMessage) {
			BuildShipMessage buildShipMessage = (BuildShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().build(buildShipMessage.getShipUUID()),
				    result -> {
				    	if (result == ShipBuildResponse.SUCCESS) {
				    		ProfileModel.messageQueue.add(LoadProfileMessage.builder().build());
				    		loadData();
				    	}
				    }
			);
		}
	}
	
	private void loadData() {
		WebManager.enqueue(
				() -> API.shipyard().data(API.authKey),
			    result -> {
			    	ShipyardModel.playerShipyard = result;
			    }
		);
		WebManager.enqueue(
				() -> API.shipyard().shipyard(),
			    result -> {
			    	ShipyardModel.shipyard = result;
			    	ShipyardModel.dataUpdated = true;
			    }
		);
	}

	@Override
	public void close() {
		
	}

}
