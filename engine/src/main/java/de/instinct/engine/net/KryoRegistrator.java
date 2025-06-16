package de.instinct.engine.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.ai.AiDifficulty;
import de.instinct.engine.ai.difficulty.AiBehaviorParameters;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.projectile.DirectionalProjectile;
import de.instinct.engine.combat.projectile.HomingProjectile;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.PlanetData;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.Defense;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.ShipType;
import de.instinct.engine.model.ship.Weapon;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderType;
import de.instinct.engine.order.types.ShipMovementOrder;

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
		kryo.register(NetworkMessage.class);
		kryo.register(AiPlayer.class);
		kryo.register(AiDifficulty.class);
		kryo.register(JoinMessage.class);
		kryo.register(LoadedMessage.class);
		kryo.register(PlayerAssigned.class);
		kryo.register(ShipData.class);
		kryo.register(ShipType.class);
		kryo.register(Weapon.class);
		kryo.register(WeaponType.class);
		kryo.register(Defense.class);
		kryo.register(Ship.class);
		kryo.register(Projectile.class);
		kryo.register(DirectionalProjectile.class);
		kryo.register(HomingProjectile.class);
		kryo.register(Vector2.class);
		kryo.register(PlayerConnectionStatus.class);
		kryo.register(PlanetData.class);
		kryo.register(ShipMovementOrder.class);
		kryo.register(OrderType.class);
		kryo.register(GameOrder.class);
		kryo.register(AiBehaviorParameters.class);
	}

}
