package de.instinct.engine.mining.net;

import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.core.net.KryoRegistrator;
import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.engine.mining.data.map.MiningMap;
import de.instinct.engine.mining.data.map.node.MiningMapNode;
import de.instinct.engine.mining.data.map.node.types.AsteroidMapNode;
import de.instinct.engine.mining.data.map.node.types.RecallAreaNode;
import de.instinct.engine.mining.entity.asteroid.Asteroid;
import de.instinct.engine.mining.entity.asteroid.ResourceType;
import de.instinct.engine.mining.entity.data.MiningEntityData;
import de.instinct.engine.mining.entity.projectile.MiningProjectile;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.cargo.CargoItem;
import de.instinct.engine.mining.entity.ship.cargo.MiningCargo;
import de.instinct.engine.mining.entity.ship.core.MiningCore;
import de.instinct.engine.mining.entity.ship.data.MiningShipData;
import de.instinct.engine.mining.entity.ship.thruster.MiningThruster;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeapon;
import de.instinct.engine.mining.net.message.ConnectMessage;
import de.instinct.engine.mining.net.message.OnboardMessage;
import de.instinct.engine.mining.net.message.StartMessage;
import de.instinct.engine.mining.order.InputChangedOrder;
import de.instinct.engine.mining.order.RecallOrder;
import de.instinct.engine.mining.player.MiningPlayer;

public class MiningKryoRegistrator extends KryoRegistrator {

	@Override
	protected void registerEngineClasses(Kryo kryo) {
		kryo.register(MiningGameState.class);
		kryo.register(MiningPlayer.class);
		kryo.register(MiningEntityData.class);
		kryo.register(MiningPlayerShip.class);
		kryo.register(MiningProjectile.class);
		kryo.register(Asteroid.class);
		kryo.register(ResourceType.class);
		kryo.register(MiningCore.class);
		kryo.register(MiningWeapon.class);
		kryo.register(MiningThruster.class);
		kryo.register(MiningCargo.class);
		kryo.register(InputChangedOrder.class);
		kryo.register(RecallOrder.class);
		kryo.register(ConnectMessage.class);
		kryo.register(OnboardMessage.class);
		kryo.register(StartMessage.class);
		kryo.register(MiningShipData.class);
		kryo.register(CargoItem.class);
		kryo.register(MiningMap.class);
		kryo.register(MiningMapNode.class);
		kryo.register(AsteroidMapNode.class);
		kryo.register(RecallAreaNode.class);
	}

}
