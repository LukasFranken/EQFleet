package de.instinct.eqfleet.game.frontend.input;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.engine.model.GameState;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqfleet.game.frontend.input.handler.ConstructionInputHandler;
import de.instinct.eqfleet.game.frontend.input.handler.GeneralInputHandler;
import de.instinct.eqfleet.game.frontend.input.handler.QLinkInputHandler;
import de.instinct.eqfleet.game.frontend.input.handler.UnitInputHandler;

public class GameInputManager {
    
    private InputHandler generalInputHandler;
    private Map<InteractionMode, InputHandler> modeInputHandlers;
    
    public GameInputManager() {
    	generalInputHandler = new GeneralInputHandler();
    	modeInputHandlers = new HashMap<>();
    	modeInputHandlers.put(InteractionMode.UNIT_CONTROL, new UnitInputHandler());
    	modeInputHandlers.put(InteractionMode.CONSTRUCTION, new ConstructionInputHandler());
    	modeInputHandlers.put(InteractionMode.Q_LINK, new QLinkInputHandler());
    }
    
    public void handleInput(PerspectiveCamera camera, GameState state) {
        if (!GameModel.inputEnabled) return;
        if (state.winner != 0) return;
        if (state.pauseData.resumeCountdownMS > 0) return;
        if (state.pauseData.teamPause != 0) return;
        
        generalInputHandler.handleInput(camera, state);
        modeInputHandlers.get(GameModel.mode).handleInput(camera, state);
    }
    
}