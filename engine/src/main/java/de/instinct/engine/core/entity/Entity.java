package de.instinct.engine.core.entity;

import com.badlogic.gdx.math.Vector2;

import lombok.ToString;

@ToString(callSuper = true)
public abstract class Entity implements Cloneable {
	
	public int id;
	public Vector2 position;
	public Vector2 direction;
	public Entity target;
	public double speed;
	public double radius;
	public boolean flaggedForDestroy;
	
	public Entity() {
		this.position = new Vector2();
		this.direction = new Vector2();
	}
	
	@Override
    public Entity clone() {
        try {
            Entity clone = (Entity) super.clone();
            clone.position = this.position.cpy();
            clone.direction = this.direction.cpy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
