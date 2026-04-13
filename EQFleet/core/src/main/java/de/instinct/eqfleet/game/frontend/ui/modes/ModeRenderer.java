package de.instinct.eqfleet.game.frontend.ui.modes;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.engine.fleet.data.FleetGameState;

public abstract class ModeRenderer {
	
	public abstract void render(PerspectiveCamera camera, FleetGameState state);

}
