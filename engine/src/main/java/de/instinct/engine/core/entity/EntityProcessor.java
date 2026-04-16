package de.instinct.engine.core.entity;

import java.util.Iterator;

import de.instinct.engine.core.data.GameState;
import de.instinct.engine.core.entity.data.EntityData;
import de.instinct.engine.core.util.VectorUtil;

public abstract class EntityProcessor {
	
	public void updateEntity(Entity entity, GameState state, long deltaTime) {
		float distanceTraveled = entity.speed * ((float) deltaTime / 1000f);
        if (entity.target != null) {
        	entity.direction = VectorUtil.getDirection(entity.position, entity.target.position);
        }
        entity.position = VectorUtil.getDirectionalTargetPosition(entity.position, entity.direction, (float) distanceTraveled);
	}
	
	public void initializeEntity(Entity newEntity, EntityData entityData) {
		newEntity.id = entityData.entityCounter;
		entityData.entityCounter++;
	}
	
	public void removeDestroyed(Iterator<? extends Entity> entityIterator) {
		while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity.flaggedForDestroy) {
            	entityIterator.remove();
            }
        }
	}
	
	public float entityDistance(Entity origin, Entity target) {
		return VectorUtil.dst(origin.position, target.position) - origin.radius - target.radius;
	}

}
