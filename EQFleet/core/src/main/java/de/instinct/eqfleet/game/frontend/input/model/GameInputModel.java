package de.instinct.eqfleet.game.frontend.input.model;

import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.planet.Planet;
import lombok.ToString;

@ToString
public class GameInputModel {
	
	public static volatile float HITBOX_INCREASE = 50f;
	public static volatile float radialSelectionThreshold = 80f;
	public static volatile float radialHoverThreshold = 20f;
	
	public static volatile UnitModeInputModel unitModeInputModel;
	
	public static volatile Vector3 mouseWorldPos = new Vector3();
	public static volatile Planet targetedPlanet;

}
