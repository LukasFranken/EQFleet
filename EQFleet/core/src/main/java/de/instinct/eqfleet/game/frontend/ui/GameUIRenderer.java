package de.instinct.eqfleet.game.frontend.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.API;
import de.instinct.engine.EngineUtility;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Planet;
import de.instinct.engine.model.Player;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;

public class GameUIRenderer {
	
	private boolean initialized;
	
	private GameUILoader uiLoader;
	private List<GameUIElement<?>> elements;
	
	private UIBounds bounds;
	
	public GameUIRenderer() {
		uiLoader = new GameUILoader();
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
	}
	
	public void init() {
		if (!initialized) {
			loadConfig();
			elements = uiLoader.loadElements(bounds);
			initializeElements();
			
			initialized = true;
			LoadedMessage loadedMessage = new LoadedMessage();
			loadedMessage.playerUUID = API.authKey;
			Game.outputMessageQueue.add(loadedMessage);
		}
	}

	private void initializeElements() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.getInitAction() != null) {
				gameUIelement.setCurrentGameState(Game.activeGameState);
				gameUIelement.getInitAction().execute();
			}
		}
	}

	private void loadConfig() {
		float scaleX = Gdx.graphics.getWidth() / 400f;
		float scaleY = Gdx.graphics.getHeight() / 900f;
		
		bounds = UIBounds.builder()
		.time(new Rectangle(330 * scaleX, 870 * scaleY, 65 * scaleX, 25 * scaleY))
		.ownCPBar(new Rectangle(47 * scaleX, 18 * scaleY, 330 * scaleX, 27 * scaleY))
		.ownCPBarLabel(new Rectangle(20 * scaleX, 18 * scaleY, 27 * scaleX, 27 * scaleY))
		.teammate1CPBar(new Rectangle(75 * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
		.teammate1CPBarLabel(new Rectangle(50 * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
		.teammate2CPBar(new Rectangle((75 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 135 * scaleX, 20 * scaleY))
		.teammate2CPBarLabel(new Rectangle((50 + 155 + 10) * scaleX, (18 + 27 + 3) * scaleY, 25 * scaleX, 20 * scaleY))
		.enemy1CPBarLabel(new Rectangle((51 + 10) * scaleX, 831 * scaleY, 27 * scaleX, 27 * scaleY))
		.enemy1CPBar(new Rectangle((51 + 27 + 10) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
		.enemy2CPBar(new Rectangle((51 + 27 + 10 + 82 + 5) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
		.enemy3CPBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5) * scaleX, 831 * scaleY, 82 * scaleX, 27 * scaleY))
		.teamAPBar(new Rectangle(20 * scaleX, 174 * scaleY, 27 * scaleX, 207 * scaleY))
		.teamAPBarLabel(new Rectangle(20 * scaleX, 147 * scaleY, 27 * scaleX, 27 * scaleY))
		.enemyAPBar(new Rectangle(20 * scaleX, 492 * scaleY, 27 * scaleX, 207 * scaleY))
		.enemyAPBarLabel(new Rectangle(20 * scaleX, (492 + 207) * scaleY, 27 * scaleX, 27 * scaleY))
		.build();
	}
	
	public void render(GameState state) {
		if (Game.activeGameState != null) {
			if (initialized) {
				updateUI(state);
				renderUI(state);
			}
		}
	}
	
	private void updateUI(GameState state) {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				gameUIelement.setCurrentGameState(Game.activeGameState);
				if (gameUIelement.getUpdateAction() != null) gameUIelement.getUpdateAction().execute();
			}
		}
	}

	private void renderUI(GameState state) {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				if (gameUIelement.getElement() != null) {
					gameUIelement.getElement().setBounds(gameUIelement.getBounds());
					gameUIelement.getElement().render();
				}
				if (gameUIelement.getPostRenderAction() != null) gameUIelement.getPostRenderAction().execute();
			}
		}
	}

	public void renderParticles(PerspectiveCamera camera) {
		if (initialized) {
			Planet activeAncientPlanet = null;
			for (Planet planet : Game.activeGameState.planets) {
				if (planet.ancient) {
					activeAncientPlanet = planet;
				}
			}
			Player owner = EngineUtility.getPlayer(Game.activeGameState, activeAncientPlanet.ownerId);
			Player self = EngineUtility.getPlayer(Game.activeGameState, Game.playerId);
			if (activeAncientPlanet != null) {
				if (activeAncientPlanet.ownerId != 0) {
					if (!ParticleRenderer.isStarted("ancient")) ParticleRenderer.start("ancient");
		        } else {
		            ParticleRenderer.stop("ancient");
		        }
			    Vector3 projected = camera.project(new Vector3(activeAncientPlanet.xPos, activeAncientPlanet.yPos, 0));
			    Vector2 source = new Vector2(projected.x, projected.y);
			    Vector2 target = (owner.teamId == self.teamId)
			        ? new Vector2(50, 260)
			        : new Vector2(50, 600);

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
	
	public void setElementVisible(String tag, boolean visible) {
		for (GameUIElement<?> element : elements) {
			element.setVisible(visible);
		}
	}

}
