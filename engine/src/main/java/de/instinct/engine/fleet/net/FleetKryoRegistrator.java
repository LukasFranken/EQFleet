package de.instinct.engine.fleet.net;

import com.esotericsoftware.kryo.Kryo;

import de.instinct.engine.core.entity.data.EntityData;
import de.instinct.engine.core.net.KryoRegistrator;
import de.instinct.engine.core.net.NetworkMessage;
import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.ai.configuration.AiBehaviorParameters;
import de.instinct.engine.fleet.ai.configuration.AiDifficulty;
import de.instinct.engine.fleet.ai.data.AiPlayer;
import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.data.ResultData;
import de.instinct.engine.fleet.data.StaticData;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine.fleet.entity.planet.data.PlanetData;
import de.instinct.engine.fleet.entity.projectile.FleetProjectile;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.engine.fleet.entity.unit.component.Weapon;
import de.instinct.engine.fleet.entity.unit.component.data.ShieldData;
import de.instinct.engine.fleet.entity.unit.component.data.WeaponData;
import de.instinct.engine.fleet.entity.unit.component.data.types.HullType;
import de.instinct.engine.fleet.entity.unit.component.data.types.ShieldType;
import de.instinct.engine.fleet.entity.unit.component.data.types.WeaponType;
import de.instinct.engine.fleet.entity.unit.data.UnitData;
import de.instinct.engine.fleet.entity.unit.ship.Ship;
import de.instinct.engine.fleet.entity.unit.ship.component.types.CoreType;
import de.instinct.engine.fleet.entity.unit.ship.component.types.EngineType;
import de.instinct.engine.fleet.entity.unit.ship.data.ShipData;
import de.instinct.engine.fleet.entity.unit.turret.Turret;
import de.instinct.engine.fleet.entity.unit.turret.data.TurretData;
import de.instinct.engine.fleet.net.data.PlayerConnectionStatus;
import de.instinct.engine.fleet.net.messages.BuildTurretMessage;
import de.instinct.engine.fleet.net.messages.FleetMovementMessage;
import de.instinct.engine.fleet.net.messages.GameFinishUpdate;
import de.instinct.engine.fleet.net.messages.GameOrderUpdate;
import de.instinct.engine.fleet.net.messages.GameStartUpdate;
import de.instinct.engine.fleet.net.messages.JoinMessage;
import de.instinct.engine.fleet.net.messages.LoadedMessage;
import de.instinct.engine.fleet.net.messages.PlayerAssigned;
import de.instinct.engine.fleet.net.messages.SurrenderMessage;
import de.instinct.engine.fleet.order.types.BuildTurretOrder;
import de.instinct.engine.fleet.order.types.ShipMovementOrder;
import de.instinct.engine.fleet.order.types.SurrenderOrder;
import de.instinct.engine.fleet.player.FleetPlayer;

public class FleetKryoRegistrator extends KryoRegistrator {

	@Override
	protected void registerEngineClasses(Kryo kryo) {
		kryo.register(FleetMovementMessage.class);
		kryo.register(Planet.class);
		kryo.register(Player.class);
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
		kryo.register(ShieldData.class);
		kryo.register(Ship.class);
		kryo.register(FleetProjectile.class);
		kryo.register(PlayerConnectionStatus.class);
		kryo.register(PlanetData.class);
		kryo.register(ShipMovementOrder.class);
		kryo.register(AiBehaviorParameters.class);
		kryo.register(SurrenderMessage.class);
		kryo.register(SurrenderOrder.class);
		kryo.register(BuildTurretMessage.class);
		kryo.register(BuildTurretOrder.class);
		kryo.register(UnitData.class);
		kryo.register(TurretData.class);
		kryo.register(Turret.class);
		kryo.register(Shield.class);
		kryo.register(Weapon.class);
		kryo.register(StaticData.class);
		kryo.register(EntityData.class);
		kryo.register(GameOrderUpdate.class);
		kryo.register(GameStartUpdate.class);
		kryo.register(GameFinishUpdate.class);
		kryo.register(ResultData.class);
		kryo.register(FleetGameState.class);
		kryo.register(FleetPlayer.class);
	}

}
