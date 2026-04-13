package de.instinct.engine.core.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.entity.projectile.Projectile;
import de.instinct.engine.core.meta.data.MetaData;
import de.instinct.engine.core.meta.data.PauseData;
import de.instinct.engine.core.order.GameOrder;
import de.instinct.engine.core.order.types.GamePauseOrder;
import de.instinct.engine.core.player.data.PlayerData;
import de.instinct.engine.fleet.net.messages.GamePauseMessage;
import de.instinct.engine.fleet.order.data.OrderData;

public abstract class KryoRegistrator {
	
	public void registerClasses(Kryo kryo) {
		registerCoreClasses(kryo);
		registerEngineClasses(kryo);
	}

	private void registerCoreClasses(Kryo kryo) {
		kryo.register(String.class);
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(GameState.class);
		kryo.register(PriorityQueue.class);
		kryo.register(Projectile.class);
		kryo.register(Vector2.class);
		kryo.register(GameOrder.class);
		kryo.register(GamePauseMessage.class);
		kryo.register(GamePauseOrder.class);
		kryo.register(ConcurrentLinkedQueue.class);
		kryo.register(MetaData.class);
		kryo.register(PauseData.class);
		kryo.register(PlayerData.class);
		kryo.register(OrderData.class);
	}

	protected abstract void registerEngineClasses(Kryo kryo);

}
