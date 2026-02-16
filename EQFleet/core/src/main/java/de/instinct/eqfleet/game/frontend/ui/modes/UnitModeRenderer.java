package de.instinct.eqfleet.game.frontend.ui.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Turret;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.GameRenderer;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqfleet.game.frontend.input.model.GameInputModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQArc;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class UnitModeRenderer extends ModeRenderer {
	
	private ShapeRenderer shapeRenderer;
	
	private PerspectiveCamera camera;
	private GameState state;
	
	public UnitModeRenderer() {
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render(PerspectiveCamera camera, GameState state) {
		this.camera = camera;
		this.state = state;
		renderSelectionShapes(camera);
		renderArrowLabel();
		renderCPCostRect(state);
		renderRadialSelectionCircle();
	}

	private void renderSelectionShapes(PerspectiveCamera camera) {
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
	
	private void renderRadialSelectionCircle() {
		if (GameInputModel.unitModeInputModel.selectedOriginPlanetId != null) {
			Planet selectedPlanet = EngineUtility.getPlanet(state.entityData.planets, GameInputModel.unitModeInputModel.selectedOriginPlanetId);
			Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, selectedPlanet.ownerId);
			if (owner.ships.size() > 1 && GameInputModel.unitModeInputModel.selectedShipId == null && InputUtil.isPressed()) {
				renderShipSelectionCircle(selectedPlanet, owner);
			}
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
	
	private void renderArrow() {
		Integer selectedId = GameInputModel.unitModeInputModel.selectedOriginPlanetId;
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.entityData.planets, selectedId) : null;
		if (selected != null && Gdx.input.isTouched()) {
			Vector3 cursorWorld = GameInputModel.mouseWorldPos;
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

	private void renderSelected() {
		Integer selectedId = GameInputModel.unitModeInputModel.selectedOriginPlanetId;
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.entityData.planets, selectedId) : null;
		if (selected != null) {
			shapeRenderer.circle(selected.position.x, selected.position.y, EngineUtility.PLANET_RADIUS);
			renderResourceCost(selected);
		}
	}
	
	private void renderCPCostRect(GameState state) {
		if (GameInputModel.unitModeInputModel.selectedShipId != null && Gdx.input.isTouched()) {
			float cpCost = 0f;
			Planet selected = EngineUtility.getPlanet(state.entityData.planets, GameInputModel.unitModeInputModel.selectedOriginPlanetId);
			Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, selected.ownerId);
			if (GameInputModel.unitModeInputModel.selectedShipId != null || GameInputModel.unitModeInputModel.hoveredShipId != null) {
				cpCost = owner.ships.get(GameInputModel.unitModeInputModel.selectedShipId == null ? GameInputModel.unitModeInputModel.hoveredShipId : GameInputModel.unitModeInputModel.selectedShipId).cpCost;
			}
			
			if (cpCost > 0f) {
				double maxCP = owner.maxCommandPoints;
				double currentCP = owner.currentCommandPoints;
				float thickness = 2f;
				Rectangle cpCostRectBounds = new Rectangle(GameModel.uiBounds.getOwnCPBar().x, GameModel.uiBounds.getOwnCPBar().y, GameModel.uiBounds.getOwnCPBar().width * (float)(cpCost / maxCP), GameModel.uiBounds.getOwnCPBar().height);
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
	
	private void renderArrowLabel() {
		if (GameInputModel.unitModeInputModel.selectedOriginPlanetId != null && GameInputModel.unitModeInputModel.selectedShipId != null && Gdx.input.isTouched()) {
			float arrowLabelYOffset = Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS ? 50f : 30f;
			Integer selectedId = GameInputModel.unitModeInputModel.selectedOriginPlanetId;
			Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.entityData.planets, selectedId) : null;
			Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, selected.ownerId);
			ShipData ship = owner.ships.get(GameInputModel.unitModeInputModel.selectedShipId);
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
	    float outerRadius = EngineUtility.PLANET_RADIUS + GameInputModel.radialSelectionThreshold;
	    float innerRadius = EngineUtility.PLANET_RADIUS + GameInputModel.radialHoverThreshold;
	    float sectionAngle = 360f / shipCount;
	    float marginAngle = 30f / shipCount;
	    
	    shapeRenderer.setProjectionMatrix(camera.combined);
	    for (int i = 0; i < shipCount; i++) {
	    	Gdx.gl.glEnable(GL20.GL_BLEND);
	    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	        boolean isSelected = (GameInputModel.unitModeInputModel.hoveredShipId != null && GameInputModel.unitModeInputModel.hoveredShipId == i);
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
	
	private void renderResourceCost(Planet selected) {
		float resourceCost = 0;
		Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, selected.ownerId);
		Integer shipIndex = GameInputModel.unitModeInputModel.selectedShipId;
		if (shipIndex == null) shipIndex = GameInputModel.unitModeInputModel.hoveredShipId;
		if (owner.ships.size() == 1) shipIndex = 0;
		if (shipIndex != null && GameModel.mode == InteractionMode.UNIT_CONTROL) {
			resourceCost = owner.ships.get(shipIndex).resourceCost;
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
		Integer selectedId = GameInputModel.unitModeInputModel.selectedOriginPlanetId;
		Planet selected = (selectedId != null) ? EngineUtility.getPlanet(state.entityData.planets, selectedId) : null;
		Planet targeted = GameInputModel.targetedPlanet;
		
		if (targeted != null) {
			boolean isSelectingOrigin = (selected == null && targeted.ownerId == GameModel.playerId);
			boolean isTargeting = (selected != null && targeted.id != selected.id);
			if (isSelectingOrigin || isTargeting) {
				shapeRenderer.circle(targeted.position.x, targeted.position.y, EngineUtility.PLANET_RADIUS);
			}
			if (isTargeting && GameInputModel.unitModeInputModel.selectedShipId != null) {
				Player owner = EngineUtility.getPlayer(state.staticData.playerData.players, selected.ownerId);
				Player target = EngineUtility.getPlayer(state.staticData.playerData.players, targeted.ownerId);
				if (owner != null && owner.ships != null) {
					float fleetCost = owner.ships.get(GameInputModel.unitModeInputModel.selectedShipId).resourceCost;
					if (owner.teamId == target.teamId) {
						renderResourceCircle(targeted.position.x, targeted.position.y, Color.GREEN, (float)(fleetCost / target.planetData.maxResourceCapacity));
					}
				}
			}
			Turret turret = EngineUtility.getPlanetTurret(state.entityData.turrets, targeted.id);
			if (turret != null && turret.data.weapons.size() > 0) {
				shapeRenderer.end();
				setDensityLineWidth();
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
				shapeRenderer.setColor(GameConfig.getPlayerColor(targeted.ownerId));
				shapeRenderer.circle(targeted.position.x, targeted.position.y, turret.data.weapons.get(0).range + EngineUtility.PLANET_RADIUS);
			}
		}
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

}
