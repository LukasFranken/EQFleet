package de.instinct.eqfleet.game.frontend.ui.modes;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.engine.model.GameState;

public abstract class ModeRenderer {
	
	public abstract void render(PerspectiveCamera camera, GameState state);

}
