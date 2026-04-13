package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.core.player.Player;
import de.instinct.engine.fleet.entity.planet.Planet;
import de.instinct.engine_api.core.service.EngineDataInterface;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;

public class GameUIParticleRenderer {
	
	public void render(PerspectiveCamera camera) {
		if (EngineDataInterface.mapHasAncient(GameModel.activeGameState)) {
			Planet activeAncientPlanet = null;
			for (Planet planet : GameModel.activeGameState.entityData.planets) {
				if (planet.ancient) {
					activeAncientPlanet = planet;
				}
			}
			Player owner = EngineDataInterface.getPlayer(GameModel.activeGameState.playerData.players, activeAncientPlanet.ownerId);
			Player self = EngineDataInterface.getPlayer(GameModel.activeGameState.playerData.players, GameModel.playerId);
			if (activeAncientPlanet != null) {
				if (activeAncientPlanet.ownerId != 0) {
					if (!ParticleRenderer.isStarted("ancient")) ParticleRenderer.start("ancient");
		        } else {
		            ParticleRenderer.stop("ancient");
		        }
			    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.position.x, activeAncientPlanet.position.y, 0));
			    Vector2 source = new Vector2(projected.x, projected.y);
			    Vector2 target = (owner.teamId == self.teamId)
			        ? GraphicsUtil.translateToPhysical(new Vector2(50, 260))
			        : GraphicsUtil.translateToPhysical(new Vector2(50, 600));

			    Vector2 dir = new Vector2(target).sub(source);
			    float angle = dir.angleDeg();
			    ParticleRenderer.setEmitterAngle("ancient", angle);

			    float distance = dir.len();
			    float baseVelocity = 200f;
			    float targetVelocity = baseVelocity * (distance / 600f);
			    ParticleRenderer.setEmitterVelocity("ancient", targetVelocity);
			    ParticleRenderer.renderParticles("ancient", source);
			}
		}
	}

}
