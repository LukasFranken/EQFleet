package de.instinct.eqfleet.menu.module.ship;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.shipyard.dto.ShipBuildResponse;
import de.instinct.api.shipyard.dto.UnuseShipResponseCode;
import de.instinct.api.shipyard.dto.UseShipResponseCode;
import de.instinct.api.shipyard.dto.ship.PlayerShipData;
import de.instinct.api.shipyard.dto.ship.component.ShipComponentType;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqfleet.menu.module.ship.message.BuildShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.ReloadShipyardMessage;
import de.instinct.eqfleet.menu.module.ship.message.UnuseShipMessage;
import de.instinct.eqfleet.menu.module.ship.message.UseShipMessage;
import de.instinct.eqfleet.net.WebManager;

public class Shipyard extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SHIPYARD;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void open() {
		loadData();
	}
	
	private void loadData() {
		//Menu.queue(ReloadShipyardMessage.builder().build());
	}

	@Override
	public void update() {
		
	}

	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof ReloadShipyardMessage) {
			WebManager.enqueue(
					() -> API.shipyard().data(API.authKey),
				    result -> {
				    	ShipyardModel.playerShipyard = result;
				    	super.requireUIReload();
				    }
			);
			WebManager.enqueue(
					() -> API.shipyard().shipyard(),
				    result -> {
				    	ShipyardModel.shipyard = result;
				    	super.requireUIReload();
				    }
			);
			return true;
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
			return true;
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
			return true;
		}
		if (message instanceof BuildShipMessage) {
			BuildShipMessage buildShipMessage = (BuildShipMessage) message;
			WebManager.enqueue(
					() -> API.shipyard().build(buildShipMessage.getShipUUID()),
				    result -> {
				    	if (result == ShipBuildResponse.SUCCESS) {
				    		Inventory.loadData();
				    		loadData();
				    	} else {
				    		super.requireUIReload();
				    	}
				    }
			);
			return true;
		}
		return false;
	}

	public static boolean hasActiveShip() {
		boolean hasActiveShip = false;
		for (PlayerShipData ship : ShipyardModel.playerShipyard.getShips()) {
			if (ship.isInUse()) {
				hasActiveShip = true;
				break;
			}
		}
		return hasActiveShip;
	}
	
	public static Color getPartTypeColor(ShipComponentType type) {
		switch (type) {
			case CORE:
				return Color.PURPLE;
			case ENGINE:
				return Color.YELLOW;
			case HULL:
				return Color.ORANGE;
			case SHIELD:
				return Color.CYAN;
			case WEAPON:
				return Color.RED;
		}
		return Color.GRAY;
	}

	@Override
	public void close() {
		
	}

}
