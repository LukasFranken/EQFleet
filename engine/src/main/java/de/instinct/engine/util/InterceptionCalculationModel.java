package de.instinct.engine.util;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString
public class InterceptionCalculationModel {

	public Vector2 originPosition;
	public float originSpeed;
	public float originRadius;
	
	public Vector2 targetPosition;
	public Vector2 targetDirection;
	public float targetSpeed;
	public float targetRadius;
	
}
