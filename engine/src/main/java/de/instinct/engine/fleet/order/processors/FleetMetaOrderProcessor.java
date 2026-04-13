package de.instinct.engine.fleet.order.processors;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.meta.MetaOrderProcessor;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.order.types.SurrenderOrder;

public class FleetMetaOrderProcessor extends MetaOrderProcessor {
	
	public FleetMetaOrderProcessor() {
		super();
	}
	
	public boolean integrateNewFleetOrder(FleetGameState state, GameOrder order) {	
		if (order instanceof SurrenderOrder) {
	    	SurrenderOrder surrenderOrder = (SurrenderOrder)order;
	    	if (isValid(surrenderOrder, state)) {
	    		Player player = playerProcessor.getPlayer(state.playerData.players, surrenderOrder.playerId);
	        	state.resultData.surrendered = player.teamId;
	        	endPause(state, 0L);
	        	return true;
	    	}
	    }
		return super.integrateNewOrder(state, order);
	}
	
	private boolean isValid(SurrenderOrder surrenderOrder, GameState state) {
		if (surrenderOrder.playerId == 0) return false;
		return true;
	}

}
