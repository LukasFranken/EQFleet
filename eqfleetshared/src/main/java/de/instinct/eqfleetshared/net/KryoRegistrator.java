package de.instinct.eqfleetshared.net;

import java.util.ArrayList;
import java.util.PriorityQueue;

import com.esotericsoftware.kryo.Kryo;

import de.instinct.eqfleetshared.gamelogic.ai.AiDifficulty;
import de.instinct.eqfleetshared.gamelogic.event.model.GameEvent;
import de.instinct.eqfleetshared.gamelogic.event.model.subtypes.FleetMovementEvent;
import de.instinct.eqfleetshared.gamelogic.model.AiPlayer;
import de.instinct.eqfleetshared.gamelogic.model.GameState;
import de.instinct.eqfleetshared.gamelogic.model.Planet;
import de.instinct.eqfleetshared.gamelogic.model.Player;
import de.instinct.eqfleetshared.net.enums.FactionMode;
import de.instinct.eqfleetshared.net.enums.GameMode;
import de.instinct.eqfleetshared.net.enums.VersusMode;
import de.instinct.eqfleetshared.net.message.NetworkMessage;
import de.instinct.eqfleetshared.net.message.types.FleetMovementMessage;
import de.instinct.eqfleetshared.net.message.types.MatchmakingRequest;
import de.instinct.eqfleetshared.net.message.types.MatchmakingUpdateResponse;
import de.instinct.eqfleetshared.net.message.types.PlayerAssigned;

public class KryoRegistrator {
	
	public static void registerAll(Kryo kryo) {
		kryo.register(MatchmakingRequest.class);
		kryo.register(PlayerAssigned.class);
		kryo.register(FleetMovementMessage.class);
		kryo.register(String.class);
		kryo.register(ArrayList.class);
		kryo.register(Planet.class);
		kryo.register(Player.class);
		kryo.register(GameState.class);
		kryo.register(PriorityQueue.class);
		kryo.register(FactionMode.class);
		kryo.register(GameMode.class);
		kryo.register(VersusMode.class);
		kryo.register(GameEvent.class);
		kryo.register(FleetMovementEvent.class);
		kryo.register(MatchmakingUpdateResponse.class);
		kryo.register(NetworkMessage.class);
		kryo.register(AiPlayer.class);
		kryo.register(AiDifficulty.class);
	}

}
