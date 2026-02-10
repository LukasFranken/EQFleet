package de.instinct.eqfleet.game.frontend.input.handler;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.engine.model.GameState;
import de.instinct.eqfleet.game.frontend.input.InputHandler;

public class ConstructionInputHandler extends InputHandler {

	@Override
	public void handleInput(PerspectiveCamera camera, GameState state) {
		
	}
	
	/*private void updateBuildingSelectionFromDrag(Vector3 currentPosition, GameState state) {
        Planet sourcePlanet = EngineUtility.getPlanet(state.planets, GameInputModel.selectedPlanetId);
        if (sourcePlanet == null) return;
        
        float dx = currentPosition.x - sourcePlanet.position.x;
        float dy = currentPosition.y - sourcePlanet.position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;
        
        //Player player = getPlayerForSelectedPlanet(state);
        int buildingCount = 1;
        if (buildingCount == 3) {
        	angle = angle + 30f; 
        }
        
        if (buildingCount == 5) {
        	angle = angle + 55f; 
        }
        
        float sectorSize = 360f / buildingCount;
        int selectedBuildingIndex = (int)((angle + sectorSize/2) % 360 / sectorSize);
        if (distance < EngineUtility.PLANET_RADIUS + GameInputModel.radialSelectionThreshold) {
        	GameInputModel.hoveredBuildingId = null;
            if (distance > EngineUtility.PLANET_RADIUS + GameInputModel.radialHoverThreshold) {
            	GameInputModel.hoveredBuildingId = selectedBuildingIndex;
            } else {
            	GameInputModel.hoveredBuildingId = null;
            }
        } else {
        	if (GameInputModel.hoveredBuildingId != null && GameInputModel.hoveredBuildingId == 0) {
        		BuildTurretMessage order = new BuildTurretMessage();
    			order.gameUUID = state.gameUUID;
    			order.userUUID = API.authKey;
    			order.planetId = GameInputModel.selectedPlanetId;
    			GameModel.outputMessageQueue.add(order);
        	}
        	GameInputModel.hoveredBuildingId = null;
        	GameInputModel.selectedPlanetId = null;
        }
    }*/

}
