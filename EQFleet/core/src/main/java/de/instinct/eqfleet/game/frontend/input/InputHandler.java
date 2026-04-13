package de.instinct.eqfleet.game.frontend.input;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.engine.fleet.data.FleetGameState;

public abstract class InputHandler {
	
	public abstract void handleInput(PerspectiveCamera camera, FleetGameState state);

}
