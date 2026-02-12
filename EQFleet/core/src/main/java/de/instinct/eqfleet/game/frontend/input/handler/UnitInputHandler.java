package de.instinct.eqfleet.game.frontend.input.handler;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.API;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.net.message.types.FleetMovementMessage;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.input.InputHandler;
import de.instinct.eqfleet.game.frontend.input.model.GameInputModel;
import de.instinct.eqfleet.game.frontend.input.model.UnitModeInputModel;
import de.instinct.eqlibgdxutils.InputUtil;

public class UnitInputHandler extends InputHandler {
	
	private UnitModeInputModel model;
	
	public UnitInputHandler() {
		model = new UnitModeInputModel();
		model.dragStartPosition = new Vector3();
		GameInputModel.unitModeInputModel = model;
	}

	@Override
	public void handleInput(PerspectiveCamera camera, GameState state) {
		if (InputUtil.isPressed()) {
			if (GameInputModel.targetedPlanet != null && model.selectedOriginPlanetId == null) {
				if (GameInputModel.targetedPlanet.ownerId == GameModel.playerId) {
                	model.selectedOriginPlanetId = GameInputModel.targetedPlanet.id;
                	model.dragStartPosition.set(GameInputModel.targetedPlanet.position.x, GameInputModel.targetedPlanet.position.y, 0f);
                	model.isDragging = true;
                	model.selectedShipId = null;
                }
			}
        }
        
        if (InputUtil.isPressed() && model.selectedOriginPlanetId != null && model.isDragging) {
        	Planet originPlanet = EngineUtility.getPlanet(state.entityData.planets, model.selectedOriginPlanetId);
        	if (originPlanet.ownerId != GameModel.playerId) {
        		resetSelection();
				return;
        	}
            Player player = getPlayerForSelectedPlanet(state);
            if (model.selectedShipId == null) {
        		if (player.ships.size() > 1) {
            		updateShipSelectionFromDrag(state);
            	} else {
            		model.selectedShipId = 0;
            	}
        	}
        }

        if (!InputUtil.isPressed() && model.selectedOriginPlanetId != null) {
        	if (GameInputModel.targetedPlanet != null) {
        		if (GameInputModel.targetedPlanet.id != model.selectedOriginPlanetId) {
            		FleetMovementMessage order = new FleetMovementMessage();
                    order.gameUUID = state.gameUUID;
                    order.userUUID = API.authKey;
                    order.fromPlanetId = model.selectedOriginPlanetId;
                    order.toPlanetId = GameInputModel.targetedPlanet.id;
                    order.shipId = model.selectedShipId == null ? 0 : model.selectedShipId;
                    GameModel.outputMessageQueue.add(order);
            	}
        	}
        	resetSelection();
        }
        
        GameInputModel.unitModeInputModel = model;
	}
	
	private void resetSelection() {
		model.selectedOriginPlanetId = null;
		model.selectedShipId = null;
		model.isDragging = false;
		model.dragStartPosition.set(0f, 0f, 0f);
	}

	private void updateShipSelectionFromDrag(GameState state) {
		Vector3 currentPosition = GameInputModel.mouseWorldPos;
        Planet sourcePlanet = EngineUtility.getPlanet(state.entityData.planets, model.selectedOriginPlanetId);
        if (sourcePlanet == null) return;
        
        float dx = currentPosition.x - sourcePlanet.position.x;
        float dy = currentPosition.y - sourcePlanet.position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;
        
        Player player = getPlayerForSelectedPlanet(state);
        int shipCount = player.ships.size();
        if (shipCount == 3) {
        	angle = angle + 30f; 
        }
        
        if (shipCount == 5) {
        	angle = angle + 55f; 
        }
        
        float sectorSize = 360f / shipCount;
        int selectedShipIndex = (int)((angle + sectorSize/2) % 360 / sectorSize);
        
        if (distance < EngineUtility.PLANET_RADIUS + GameInputModel.radialSelectionThreshold) {
        	model.selectedShipId = null;
            if (distance > EngineUtility.PLANET_RADIUS + GameInputModel.radialHoverThreshold) {
            	model.hoveredShipId = selectedShipIndex;
            } else {
            	model.hoveredShipId = null;
            }
        } else {
        	model.selectedShipId = selectedShipIndex;
        	model.hoveredShipId = null;
        }
    }
	
	private Player getPlayerForSelectedPlanet(GameState state) {
        if (model.selectedOriginPlanetId == null) return null;
        Planet planet = EngineUtility.getPlanet(state.entityData.planets, model.selectedOriginPlanetId);
        if (planet == null) return null;
        return EngineUtility.getPlayer(state.staticData.playerData.players, planet.ownerId);
    }
	
}
