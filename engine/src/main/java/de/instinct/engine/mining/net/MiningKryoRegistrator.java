package de.instinct.engine.mining.net;

import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.core.net.KryoRegistrator;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningKryoRegistrator extends KryoRegistrator {

	@Override
	protected void registerEngineClasses(Kryo kryo) {
		kryo.register(MiningGameState.class);
		kryo.register(MiningPlayer.class);
	}

}
