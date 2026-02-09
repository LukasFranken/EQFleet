package de.instinct.eqfleet.game.frontend.ui;

import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.API;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.PlayerConnectionStatus;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.net.message.types.GamePauseMessage;
import de.instinct.engine.net.message.types.LoadedMessage;
import de.instinct.engine.net.message.types.SurrenderMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.ApplicationMode;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqfleet.game.frontend.input.GameInputManager;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQArc;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class GameUIRenderer {
	
	public boolean initialized;
	
	private GameUILoader uiLoader;
	private List<GameUIElement<?>> elements;
	
	private UIBounds bounds;
	
	private ShapeRenderer shapeRenderer;
	private GameInputManager inputManager;
	private DefenseUIRenderer defenseUIRenderer;
	
	private PerspectiveCamera camera;
	private GameState state;
	
	private ColorButton surrenderButton;
    private ColorButton resumeButton;
    
    private ColorButton unitControlButton;
    private ColorButton constructionButton;
    private ColorButton qLinkButton;
    
    public static Color bluroutColor;
	
	public GameUIRenderer() {
		uiLoader = new GameUILoader();
		ParticleRenderer.loadParticles("ancient", "ancient");
		ParticleRenderer.stop("ancient");
		shapeRenderer = new ShapeRenderer();
		inputManager = new GameInputManager();
		defenseUIRenderer = new DefenseUIRenderer();
		bluroutColor = new Color(0f, 0f, 0f, 0.5f);
	}
	
	public void init() {
		loadBounds();
		elements = uiLoader.loadElements(bounds);
		initializeElements();
		
		LoadedMessage loadedMessage = new LoadedMessage();
		loadedMessage.playerUUID = API.authKey;
		GameModel.outputMessageQueue.add(loadedMessage);
		initialized = true;
		
		surrenderButton = DefaultButtonFactory.colorButton("Surrender", () -> {
    		SurrenderMessage order = new SurrenderMessage();
    		order.gameUUID = state.gameUUID;
    		order.userUUID = API.authKey;
    		GameModel.outputMessageQueue.add(order);
		});
    	resumeButton = DefaultButtonFactory.colorButton("Resume", () -> {
    		GamePauseMessage order = new GamePauseMessage();
        	order.gameUUID = state.gameUUID;
        	order.userUUID = API.authKey;
        	order.pause = false;
        	GameModel.outputMessageQueue.add(order);
    	});
    	Rectangle surrenderBounds = new Rectangle((GraphicsUtil.screenBounds().width / 2) - 60, 200, 120, 40);
    	surrenderButton.setBounds(surrenderBounds);
        Rectangle resumeBounds = new Rectangle((GraphicsUtil.screenBounds().width / 2) - 60, 100, 120, 40);
        resumeButton.setBounds(resumeBounds);
        
        unitControlButton = DefaultButtonFactory.colorButton("U", () -> {
        	if (GameModel.mode != InteractionMode.UNIT_CONTROL) {
        		GameModel.mode = InteractionMode.UNIT_CONTROL;
        		AudioManager.playVoice("units");
        	}
		});
        unitControlButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 100, 40, 40));
        
        constructionButton = DefaultButtonFactory.colorButton("C", () -> {
			if (GameModel.mode != InteractionMode.CONSTRUCTION) {
        		GameModel.mode = InteractionMode.CONSTRUCTION;
        		AudioManager.playVoice("construction");
        	}
		});
        constructionButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 160, 40, 40));
        
        qLinkButton = DefaultButtonFactory.colorButton("Q", () -> {
			if (GameModel.mode != InteractionMode.Q_LINK) {
        		GameModel.mode = InteractionMode.Q_LINK;
        		AudioManager.playVoice("qlink");
        	}
		});
        qLinkButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 220, 40, 40));
	}

	private void initializeElements() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.getInitAction() != null) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				gameUIelement.getInitAction().execute();
			}
		}
	}

	private void loadBounds() {
		bounds = UIBounds.builder()
				.time(new Rectangle(330, 830, 65, 25))
				.ownCPBar(new Rectangle(47, 23, 330, 27))
				.ownCPBarLabel(new Rectangle(20, 23, 27, 27))
				.teammate1CPBar(new Rectangle(75, (23 + 27 + 3), 135, 20))
				.teammate1CPBarLabel(new Rectangle(50, (23 + 27 + 3), 25, 20))
				.teammate2CPBar(new Rectangle((75 + 155 + 10), (23 + 27 + 3), 135, 20))
				.teammate2CPBarLabel(new Rectangle((50 + 155 + 10), (23 + 27 + 3), 25, 20))
				.enemy1CPBarLabel(new Rectangle((51 + 10), 800, 27, 27))
				.enemy1CPBar(new Rectangle((51 + 27 + 10), 800, 82, 27))
				.enemy2CPBar(new Rectangle((51 + 27 + 10 + 82 + 5), 800, 82, 27))
				.enemy3CPBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5), 800, 82, 27))
				.teamAPBar(new Rectangle(20, 174, 27, 207))
				.teamAPBarLabel(new Rectangle(20, 147, 27, 27))
				.enemyAPBar(new Rectangle(20, 492, 27, 207))
				.enemyAPBarLabel(new Rectangle(20, (492 + 207), 27, 27))
				.build();
	}
	
	public void render() {
		if (state != null) {
			if (initialized && state.winner == 0) {
				inputManager.handleInput(camera, state);
				updateStaticUI();
				renderResourceCircles();
				defenseUIRenderer.render(state, camera);
				renderStaticUI();
				if (state.teamPause == 0 && state.resumeCountdownMS <= 0) {
					renderModeButtons(state);
				}
				renderShipSelection();
				renderCountdownScreen(state);
				renderPauseScreen(state);
				if (!state.started) {
					renderLoadingScreen(state);
				}
			}
		}
		renderMessageText();
	}
	
	private void renderModeButtons(GameState state) {
		Player self = EngineUtility.getPlayer(state.players, GameModel.playerId);
		if (!self.turrets.isEmpty()) {
			if (GameModel.mode == InteractionMode.UNIT_CONTROL) {
				unitControlButton.getBorder().setColor(Color.GREEN);
			} else {
				unitControlButton.getBorder().setColor(SkinManager.skinColor);
			}
			unitControlButton.render();
			
			if (GameModel.mode == InteractionMode.CONSTRUCTION) {
				constructionButton.getBorder().setColor(Color.GREEN);
			} else {
				constructionButton.getBorder().setColor(SkinManager.skinColor);
			}
			constructionButton.render();
		}
	}

	private void renderCountdownScreen(GameState state) {
		if (state.resumeCountdownMS > 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Label pauseLabel = new Label(StringUtils.format(Math.min((state.resumeCountdownMS / 1000) + 1, 3), 0));
			pauseLabel.setType(FontType.GIANT);
			pauseLabel.setBounds(new Rectangle(100, (GraphicsUtil.screenBounds().getHeight() / 2), GraphicsUtil.screenBounds().getWidth() - 200, 60));
			pauseLabel.render();
		}
	}

	private void renderPauseScreen(GameState state) {
		if (state.teamPause != 0) {
			TextureManager.draw(TextureManager.createTexture(bluroutColor), GraphicsUtil.screenBounds());
			Player self = EngineUtility.getPlayer(state.players, GameModel.playerId);
			String teamName = self.teamId == state.teamPause ? "OWN" : "ENEMY";
			Label pauseLabel = new Label("PAUSED - " + teamName + " TEAM");
			pauseLabel.setType(FontType.LARGE);
			pauseLabel.setBounds(new Rectangle(50, (GraphicsUtil.screenBounds().getHeight() / 2) + 200, GraphicsUtil.screenBounds().getWidth() - 100, 60));
			pauseLabel.setBackgroundColor(Color.BLACK);
			pauseLabel.render();
			
			long teamPauseMS = state.teamPausesMS.get(state.teamPause);
			Label remainingTimeLabel = new Label("Remaining Time: " + StringUtils.format(((float)(state.maxPauseMS - teamPauseMS) / 1000f), 0) + "s");
			remainingTimeLabel.setType(FontType.NORMAL);
			remainingTimeLabel.setBounds(new Rectangle(100, (GraphicsUtil.screenBounds().getHeight() / 2) + 100, GraphicsUtil.screenBounds().getWidth() - 200, 30));
			remainingTimeLabel.setBackgroundColor(Color.BLACK);
			remainingTimeLabel.render();
			
			if (self.teamId == state.teamPause && state.currentPauseElapsedMS > state.minPauseMS) {
				surrenderButton.render();
	        	resumeButton.render();
			}
		}
	}

	private void renderLoadingScreen(GameState state) {
		int i = 1;
		float labelHeight = 30;
		Label header = new Label("NAME - CONNECTED - LOADED");
		header.setBounds(new Rectangle(0, 500, GraphicsUtil.screenBounds().getWidth(), labelHeight));
		header.render();
		for (Player player : state.players) {
			if (player.teamId == 0) continue;
			for (PlayerConnectionStatus status : state.connectionStati) {
				if (status.playerId == player.id) {
					Label row = new Label(player.name + " - " + status.connected + " - " + status.loaded);
					row.setBounds(new Rectangle(0, 500 - (i * labelHeight), GraphicsUtil.screenBounds().getWidth(), labelHeight));
					row.render();
					i++;
				}
			}
		}
	}

	private void renderResourceCircles() {
		for (Planet planet : state.planets) {
			Vector3 screenPos = camera.project(new Vector3(planet.position.x, planet.position.y, 0f));
		    String value = String.valueOf((int) planet.currentResources);
		    float labelWidth = FontUtil.getFontTextWidthPx(value.length());
		    float labelHeight = FontUtil.getFontHeightPx();
		    float labelX = screenPos.x - labelWidth / 2f;
		    float labelY = screenPos.y - labelHeight / 2f;
		    
		    Player owner = EngineUtility.getPlayer(state.players, planet.ownerId);
		    renderResourceCircle(planet.position.x, planet.position.y, GameConfig.getPlayerColor(owner.id), (float)(planet.currentResources / owner.planetData.maxResourceCapacity));

		    if (GlobalStaticData.mode == ApplicationMode.DEV) {
			    Gdx.gl.glEnable(GL20.GL_BLEND);
			    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			    shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
			    shapeRenderer.rect(labelX - 4, labelY - 2, labelWidth + 8, labelHeight + 4);
			    shapeRenderer.end();
			    Gdx.gl.glDisable(GL20.GL_BLEND);
			    
			    Label valueLabel = new Label(value);
			    valueLabel.setColor(Color.WHITE);
			    valueLabel.setBounds(new Rectangle(labelX, labelY, labelWidth, labelHeight));
			    valueLabel.render();
		    }
		}
	}

	private void updateStaticUI() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				if (gameUIelement.getUpdateAction() != null) gameUIelement.getUpdateAction().execute();
			}
		}
	}

	private void renderStaticUI() {
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

	public void renderParticles() {
		if (initialized && EngineUtility.mapHasAncient(GameModel.activeGameState)) {
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
	
	public void setElementVisible(String tag, boolean visible) {
		for (GameUIElement<?> element : elements) {
			if (element.getTag().contentEquals(tag)) {
				element.setVisible(visible);
			}
		}
	}
	
	private void renderShipSelection() {
		renderSelectionShapes();
		renderArrowLabel();
		renderCPCostRect();
		renderRadialSelectionCircles();
	}

	private void renderSelectionShapes() {
		setDensityLineWidth();
		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		renderSelected();
		renderHovered();
		shapeRenderer.end();
		renderArrow();
	}

	private void renderCPCostRect() {
		if (inputManager.getSelectedPlanetId() != null && Gdx.input.isTouched()) {
			float cpCost = 0f;
			Planet selected = EngineUtility.getPlanet(state.planets, inputManager.getSelectedPlanetId());
			Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
			
			switch (GameModel.mode) {
			case UNIT_CONTROL:
				if (inputManager.getSelectedShipId() != null || inputManager.getHoveredShipId() != null) {
					cpCost = owner.ships.get(inputManager.getSelectedShipId() == null ? inputManager.getHoveredShipId() : inputManager.getSelectedShipId()).cpCost;
				}
				break;
			case CONSTRUCTION:
				if (inputManager.getHoveredBuildingId() != null) {
					cpCost = owner.turrets.get(inputManager.getHoveredBuildingId()).cpCost;
				}
				break;
			case Q_LINK:
				break;
			}
			
			if (cpCost > 0f) {
				double maxCP = owner.maxCommandPoints;
				double currentCP = owner.currentCommandPoints;
				float thickness = 2f;
				Rectangle cpCostRectBounds = new Rectangle(bounds.getOwnCPBar().x, bounds.getOwnCPBar().y, bounds.getOwnCPBar().width * (float)(cpCost / maxCP), bounds.getOwnCPBar().height);
				Color rectColor = cpCost > currentCP ? new Color(0.5f, 0.5f, 0.5f, 1f) : new Color(0f, 1f, 0f, 1f);
				Shapes.draw(EQRectangle.builder()
						.bounds(cpCostRectBounds)
						.color(rectColor)
						.thickness(thickness)
						.round(true)
						.build());
			}
		}
	}

	private void renderRadialSelectionCircles() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		if (selected != null) {
			Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
			switch (GameModel.mode) {
			case UNIT_CONTROL:
				if (owner.ships.size() > 1 && inputManager.getSelectedShipId() == null) {
					renderShipSelectionCircle(selected, owner);
				}
				break;
			case CONSTRUCTION:
				renderBuildingSelectionCircle(selected, owner);
				break;
			case Q_LINK:
				break;
			}
		}
	}

	private void renderSelected() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		
		if (selected != null) {
			shapeRenderer.circle(selected.position.x, selected.position.y, EngineUtility.PLANET_RADIUS);
			renderResourceCost(selected);
		}
	}

	private void renderResourceCost(Planet selected) {
		float resourceCost = 0;
		Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
		Integer shipIndex = inputManager.getSelectedShipId();
		if (shipIndex == null) shipIndex = inputManager.getHoveredShipId();
		if (owner.ships.size() == 1) shipIndex = 0;
		if (shipIndex != null && GameModel.mode == InteractionMode.UNIT_CONTROL) {
			resourceCost = owner.ships.get(shipIndex).resourceCost;
		}
		if (inputManager.getHoveredBuildingId() != null) {
			resourceCost = owner.turrets.get(inputManager.getHoveredBuildingId()).resourceCost;
		}
		if (resourceCost > 0 && owner.planetData.maxResourceCapacity > 0) {
			renderResourceCircle(selected.position.x, selected.position.y, resourceCost <= selected.currentResources ? Color.GREEN : Color.RED, (float)(resourceCost / owner.planetData.maxResourceCapacity));
		}
	}

	private void setDensityLineWidth() {
		float baseLineWidth = 2f;
		float density = Gdx.graphics.getDensity();
		Gdx.gl.glLineWidth(baseLineWidth * (density > 1f ? density : 1f));
	}

	private void renderHovered() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		Planet hovered = inputManager.getHoveredPlanet(camera, state);
		
		if (hovered != null) {
			boolean isSelectingOrigin = (selected == null && hovered.ownerId == GameModel.playerId);
			boolean isTargeting = (selected != null && hovered.id != selected.id);
			if (isSelectingOrigin || isTargeting) {
				shapeRenderer.circle(hovered.position.x, hovered.position.y, EngineUtility.PLANET_RADIUS);
			}
			if (isTargeting) {
				Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
				Player target = EngineUtility.getPlayer(state.players, hovered.ownerId);
				if (owner != null && owner.ships != null) {
					float fleetCost = owner.ships.get(inputManager.getSelectedShipId()).resourceCost;
					if (owner.teamId == target.teamId) {
						renderResourceCircle(hovered.position.x, hovered.position.y, Color.GREEN, (float)(fleetCost / target.planetData.maxResourceCapacity));
					}
				}
			}
			Turret turret = EngineUtility.getPlanetTurret(state.turrets, hovered.id);
			if (turret != null && turret.data.weapons.size() > 0) {
				shapeRenderer.end();
				setDensityLineWidth();
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
				shapeRenderer.setColor(GameConfig.getPlayerColor(hovered.ownerId));
				shapeRenderer.circle(hovered.position.x, hovered.position.y, turret.data.weapons.get(0).range + EngineUtility.PLANET_RADIUS);
			}
		}
	}

	private void renderArrow() {
		Integer selectedId = inputManager.getSelectedPlanetId();
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
		if (selected != null && Gdx.input.isTouched()) {
			Vector3 cursorWorld = inputManager.getCurrentTouchWorldPosition(camera);
			Gdx.gl.glEnable(GL20.GL_BLEND);
	    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			shapeRenderer.line(selected.position.x, selected.position.y, cursorWorld.x, cursorWorld.y);
			shapeRenderer.end();
			
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			drawArrow(cursorWorld, selected.position.x, selected.position.y);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
	}
	
	private void renderArrowLabel() {
		if (inputManager.getSelectedPlanetId() != null && inputManager.getSelectedShipId() != null && Gdx.input.isTouched()) {
			float arrowLabelYOffset = Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS ? 50f : 30f;
			Integer selectedId = inputManager.getSelectedPlanetId();
			Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.planets, selectedId) : null;
			Player owner = EngineUtility.getPlayer(state.players, selected.ownerId);
			ShipData ship = owner.ships.get(inputManager.getSelectedShipId());
			String shipName = ship.model;
	        float labelWidth = FontUtil.getFontTextWidthPx(shipName.length(), FontType.SMALL);
	        Color labelColor = new Color(selected.currentResources >= ship.resourceCost && owner.currentCommandPoints >= ship.cpCost ? Color.GREEN : Color.RED);
	        Label shipLabel = new Label(shipName);
	        shipLabel.setColor(labelColor);
	        shipLabel.setBounds(new Rectangle(InputUtil.getNormalizedMousePosition().x - (labelWidth / 2), InputUtil.getNormalizedMousePosition().y - 10f + arrowLabelYOffset, labelWidth, 20f));
	        shipLabel.setType(FontType.SMALL);
	        shipLabel.render();
		}
	}

	private void renderShipSelectionCircle(Planet planet, Player owner) {
		float x = planet.position.x;
		float y = planet.position.y;
		Color unselectedColor = new Color(0.5f, 0.5f, 0.5f, 0.2f);
		Color unselectedColorLabel = new Color(0.7f, 0.7f, 0.7f, 0.5f);
		Color selectedColor = new Color(1f, 0f, 0f, 0.5f);
		Color selectedAffordableColor = new Color(0f, 1f, 0f, 0.5f);
		
	    int shipCount = owner.ships.size();
	    float outerRadius = EngineUtility.PLANET_RADIUS + 80f;
	    float innerRadius = EngineUtility.PLANET_RADIUS + 20f;
	    float sectionAngle = 360f / shipCount;
	    float marginAngle = 30f / shipCount;
	    
	    shapeRenderer.setProjectionMatrix(camera.combined);
	    for (int i = 0; i < shipCount; i++) {
	    	Gdx.gl.glEnable(GL20.GL_BLEND);
	    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	        boolean isSelected = (inputManager.getHoveredShipId() != null && inputManager.getHoveredShipId() == i);
	        boolean isAffordable = owner.ships.get(i).resourceCost <= planet.currentResources && owner.ships.get(i).cpCost <= owner.currentCommandPoints;
	        if (isSelected) {
	        	shapeRenderer.setColor(selectedColor);
	        	if (isAffordable) {
	        		shapeRenderer.setColor(selectedAffordableColor);
	        	}
	        } else {
	            shapeRenderer.setColor(unselectedColor);
	        }
	        
	        float angleOffset = - 90;
	        if (shipCount == 4) {
	        	angleOffset = - 45;
	        }
	        
	        float startAngle = i * sectionAngle + angleOffset;
	        float endAngle = (i + 1) * sectionAngle + angleOffset;
	        
	        for (float angle = startAngle + marginAngle; angle < endAngle - marginAngle; angle += 1f) {
	            float rad1 = (float) Math.toRadians(angle);
	            float rad2 = (float) Math.toRadians(angle + 1f);
	            
	            float x1 = x + innerRadius * (float) Math.cos(rad1);
	            float y1 = y + innerRadius * (float) Math.sin(rad1);
	            float x2 = x + outerRadius * (float) Math.cos(rad1);
	            float y2 = y + outerRadius * (float) Math.sin(rad1);
	            float x3 = x + outerRadius * (float) Math.cos(rad2);
	            float y3 = y + outerRadius * (float) Math.sin(rad2);
	            float x4 = x + innerRadius * (float) Math.cos(rad2);
	            float y4 = y + innerRadius * (float) Math.sin(rad2);
	            
	            shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
	            shapeRenderer.triangle(x1, y1, x3, y3, x4, y4);
	        }
	        shapeRenderer.end();
	        Gdx.gl.glDisable(GL20.GL_BLEND);
	        
	        float midAngle = (float) Math.toRadians((startAngle + endAngle) / 2);
	        float labelX = x + (50f + outerRadius) * (float) Math.cos(midAngle);
	        float labelY = y + (50f + outerRadius) * (float) Math.sin(midAngle);
	        Vector3 labelPos = camera.project(new Vector3(labelX, labelY, 0f));
	        
	        String shipName = owner.ships.get(i).model;
	        float labelWidth = FontUtil.getFontTextWidthPx(shipName.length(), FontType.SMALL);
	        Label shipLabel = new Label(shipName);
	        shipLabel.setColor(isSelected ? (isAffordable ? selectedAffordableColor : selectedColor) : unselectedColorLabel);
	        shipLabel.setBounds(GraphicsUtil.scaleFactorDeducted(new Rectangle(labelPos.x - (labelWidth / 2), labelPos.y - 10f, labelWidth, 20f)));
	        shipLabel.setType(FontType.SMALL);
	        shipLabel.render();
	    }
	}
	
	private void renderBuildingSelectionCircle(Planet planet, Player owner) {
		float x = planet.position.x;
		float y = planet.position.y;
		Color unselectedColor = new Color(0.5f, 0.5f, 0.5f, 0.2f);
		Color unselectedColorLabel = new Color(0.7f, 0.7f, 0.7f, 0.5f);
		Color selectedColor = new Color(SkinManager.skinColor.r, SkinManager.skinColor.g, SkinManager.skinColor.b, 0.5f);
		Color selectedAffordableColor = new Color(0f, 1f, 0f, 0.5f);
		
	    int buildingCount = 1;
	    float outerRadius = EngineUtility.PLANET_RADIUS + 80f;
	    float innerRadius = EngineUtility.PLANET_RADIUS + 20f;
	    float sectionAngle = 360f / buildingCount;
	    float marginAngle = 30f / buildingCount;
	    
	    shapeRenderer.setProjectionMatrix(camera.combined);
	    for (int i = 0; i < buildingCount; i++) {
	    	Gdx.gl.glEnable(GL20.GL_BLEND);
	    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	        boolean isSelected = (inputManager.getHoveredBuildingId() != null && inputManager.getHoveredBuildingId() == i);
	        boolean isAffordable = owner.turrets.get(i).resourceCost <= planet.currentResources && owner.turrets.get(i).cpCost <= owner.currentCommandPoints;
	        if (isSelected) {
	        	shapeRenderer.setColor(selectedColor);
	        	if (isAffordable) {
	        		shapeRenderer.setColor(selectedAffordableColor);
	        	}
	        } else {
	            shapeRenderer.setColor(unselectedColor);
	        }
	        
	        float angleOffset = - 90;
	        if (buildingCount == 4) {
	        	angleOffset = - 45;
	        }
	        if (buildingCount == 1) {
	        	marginAngle = 0f;
	        }
	        
	        float startAngle = i * sectionAngle + angleOffset;
	        float endAngle = (i + 1) * sectionAngle + angleOffset;
	        
	        for (float angle = startAngle + marginAngle; angle < endAngle - marginAngle; angle += 1f) {
	            float rad1 = (float) Math.toRadians(angle);
	            float rad2 = (float) Math.toRadians(angle + 1f);
	            
	            float x1 = x + innerRadius * (float) Math.cos(rad1);
	            float y1 = y + innerRadius * (float) Math.sin(rad1);
	            float x2 = x + outerRadius * (float) Math.cos(rad1);
	            float y2 = y + outerRadius * (float) Math.sin(rad1);
	            float x3 = x + outerRadius * (float) Math.cos(rad2);
	            float y3 = y + outerRadius * (float) Math.sin(rad2);
	            float x4 = x + innerRadius * (float) Math.cos(rad2);
	            float y4 = y + innerRadius * (float) Math.sin(rad2);
	            
	            shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
	            shapeRenderer.triangle(x1, y1, x3, y3, x4, y4);
	        }
	        shapeRenderer.end();
	        Gdx.gl.glDisable(GL20.GL_BLEND);
	        
	        float midAngle = (float) Math.toRadians((startAngle + endAngle) / 2);
	        float labelX = x + (50f + outerRadius) * (float) Math.cos(midAngle);
	        float labelY = y + (50f + outerRadius) * (float) Math.sin(midAngle);
	        Vector3 labelPos = camera.project(new Vector3(labelX, labelY, 0f));
	        
	        String buildingName = owner.turrets.get(i).model + " Turret";
	        float labelWidth = FontUtil.getFontTextWidthPx(buildingName.length(), FontType.SMALL);
	        Label buildingLabel = new Label(buildingName);
	        buildingLabel.setColor(isSelected ? (isAffordable ? selectedAffordableColor : selectedColor) : unselectedColorLabel);
	        buildingLabel.setBounds(GraphicsUtil.scaleFactorDeducted(new Rectangle(labelPos.x - (labelWidth / 2), labelPos.y - 10f, labelWidth, 20f)));
	        buildingLabel.setType(FontType.SMALL);
	        buildingLabel.render();
	    }
	}

	private void drawArrow(Vector3 to, float fromX, float fromY) {
		float dx = fromX - to.x;
		float dy = fromY - to.y;
		float angle = (float) Math.atan2(dy, dx);

		float arrowLength = 20f;
		float arrowOffset = 20f;

		float baseX = to.x + (float) Math.cos(angle) * arrowOffset;
		float baseY = to.y + (float) Math.sin(angle) * arrowOffset;

		float leftX = baseX + (float) Math.cos(angle + Math.PI / 6) * arrowLength;
		float leftY = baseY + (float) Math.sin(angle + Math.PI / 6) * arrowLength;

		float rightX = baseX + (float) Math.cos(angle - Math.PI / 6) * arrowLength;
		float rightY = baseY + (float) Math.sin(angle - Math.PI / 6) * arrowLength;

		shapeRenderer.triangle(to.x, to.y, leftX, leftY, rightX, rightY);
	}
	
	private void renderResourceCircle(float x, float y, Color color, float value) {
		float radius = EngineUtility.PLANET_RADIUS + 5f;
		float thickness = 8f;
		Shapes.draw(EQArc.builder()
				.position(new Vector2(x, y))
				.innerRadius(radius)
				.outerRadius(radius + thickness)
				.color(color)
				.startAngle(90 + (GameRenderer.isFlipped ? 180 : 0))
				.degrees(value * 360f)
				.projectionMatrix(camera.combined)
				.build());
	}

	private void renderMessageText() {
		String message = "Connecting...";
		if (GameModel.activeGameState != null) {
			int winner = GameModel.activeGameState.winner;
			if (winner != 0 && !GameModel.activeGameState.gameUUID.equals("tutorial")) {
				if (winner == EngineUtility.getPlayer(GameModel.activeGameState.players, GameModel.playerId).teamId) {
					if (EngineUtility.winIsWiped(GameModel.activeGameState)) {
						message = "DOMINATION";
					} else {
						message = "VICTORY";
					}
				} else if (winner != 0) {
					if (GameModel.activeGameState.surrendered != 0) {
						message = "SURRENDERED";
					} else {
						if (EngineUtility.winIsWiped(GameModel.activeGameState)) {
							message = "WIPED OUT";
						} else {
							message = "DEFEATED";
						}
					}
				}
			} else {
				message = "";
			}
		}
		Label messageLabel = new Label(message);
		messageLabel.setColor(Color.WHITE);
		messageLabel.setType(FontType.LARGE);
		messageLabel.setBounds(GraphicsUtil.screenBounds());
		messageLabel.render();
	}

	public void setCamera(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public void setState(GameState state) {
		this.state = state;
	}
	
}
