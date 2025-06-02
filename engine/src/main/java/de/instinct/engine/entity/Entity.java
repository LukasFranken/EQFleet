package de.instinct.engine.entity;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString
public abstract class Entity implements Cloneable {
	
	public int id;
	public int ownerId;
	public Vector2 position;
	public float radius;
	
	@Override
    public Entity clone() {
        try {
            Entity clone = (Entity) super.clone();
            clone.position = this.position.cpy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
