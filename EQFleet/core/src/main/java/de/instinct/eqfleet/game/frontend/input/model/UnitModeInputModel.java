package de.instinct.eqfleet.game.frontend.input.model;

import com.badlogic.gdx.math.Vector3;

import lombok.ToString;

@ToString
public class UnitModeInputModel {
	
	public Integer selectedOriginPlanetId;
	public Integer hoveredShipId;
	public Integer selectedShipId;
	public boolean isDragging;
	public Vector3 dragStartPosition;

}
