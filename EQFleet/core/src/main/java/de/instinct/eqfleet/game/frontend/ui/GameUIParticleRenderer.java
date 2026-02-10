package de.instinct.eqfleet.game.frontend.ui;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;

public class GameUIParticleRenderer {
	
	public void render(PerspectiveCamera camera) {
		if (EngineUtility.mapHasAncient(GameModel.activeGameState)) {
			Planet activeAncientPlanet = null;
			for (Planet planet : GameModel.activeGameState.planets) {
				if (planet.ancient) {
					activeAncientPlanet = planet;
				}
			}
			Player owner = EngineUtility.getPlayer(GameModel.activeGameState.players, activeAncientPlanet.ownerId);
			Player self = EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId);
			if (activeAncientPlanet != null) {
				if (activeAncientPlanet.ownerId != 0) {
					if (!ParticleRenderer.isStarted("ancient")) ParticleRenderer.start("ancient");
		        } else {
		            ParticleRenderer.stop("ancient");
		        }
			    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.position.x, activeAncientPlanet.position.y, 0));
			    Vector2 source = new Vector2(projected.x, projected.y);
			    Vector2 target = (owner.teamId == self.teamId)
			        ? GraphicsUtil.scaleFactorAdjusted(new Vector2(50, 260))
			        : GraphicsUtil.scaleFactorAdjusted(new Vector2(50, 600));

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
