package de.instinct.engine.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.ai.difficulty.AiBehaviorParameters;
import de.instinct.engine.ai.difficulty.AiDifficulty;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.projectile.DirectionalProjectile;
import de.instinct.engine.combat.projectile.HomingProjectile;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.AiPlayer;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.UnitData;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.planet.PlanetData;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.model.ship.components.CoreData;
import de.instinct.engine.model.ship.components.EngineData;
import de.instinct.engine.model.ship.components.HullData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.ship.components.types.CoreType;
import de.instinct.engine.model.ship.components.types.EngineType;
import de.instinct.engine.model.ship.components.types.HullType;
import de.instinct.engine.model.ship.components.types.ShieldType;
import de.instinct.engine.model.ship.components.types.WeaponType;
import de.instinct.engine.model.turret.TurretData;
import de.instinct.engine.net.message.NetworkMessage;
import de.instinct.engine.net.message.types.BuildTurretMessage;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.net.message.types.JoinMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.net.message.types.PlayerAssigned;
import de.instinct.engine.net.message.types.SurrenderMessage;
import de.instinct.engine.order.GameOrder;
import de.instinct.engine.order.OrderType;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.ShipMovementOrder;
import de.instinct.engine.order.types.SurrenderOrder;

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
		kryo.register(CoreType.class);
		kryo.register(WeaponType.class);
		kryo.register(HullType.class);
		kryo.register(ShieldType.class);
		kryo.register(EngineType.class);
		kryo.register(WeaponData.class);
		kryo.register(CoreData.class);
		kryo.register(HullData.class);
		kryo.register(EngineData.class);
		kryo.register(ShieldData.class);
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
		kryo.register(GamePauseMessage.class);
		kryo.register(GamePauseOrder.class);
		kryo.register(SurrenderMessage.class);
		kryo.register(SurrenderOrder.class);
		kryo.register(BuildTurretMessage.class);
		kryo.register(BuildTurretOrder.class);
		kryo.register(UnitData.class);
		kryo.register(TurretData.class);
		kryo.register(Turret.class);
		kryo.register(ConcurrentLinkedQueue.class);
	}

}
