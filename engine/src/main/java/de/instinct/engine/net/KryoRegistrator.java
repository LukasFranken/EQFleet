package de.instinct.engine.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.event.GameEvent;
import de.instinct.engine.model.event.types.FleetMovementEvent;
import de.instinct.engine.model.ship.Ship;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;

public class KryoRegistrator {
	
	public static void registerAll(Kryo kryo) {
		kryo.register(FleetMovementMessage.class);
		kryo.register(String.class);
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(Planet.class);
		kryo.register(Player.class);
		kryo.register(GameState.class);
		kryo.register(PriorityQueue.class);
		kryo.register(GameEvent.class);
		kryo.register(FleetMovementEvent.class);
		kryo.register(NetworkMessage.class);
		kryo.register(AiPlayer.class);
		kryo.register(AiDifficulty.class);
		kryo.register(JoinMessage.class);
		kryo.register(LoadedMessage.class);
		kryo.register(PlayerAssigned.class);
		kryo.register(Ship.class);
		kryo.register(ShipType.class);
	}

}
