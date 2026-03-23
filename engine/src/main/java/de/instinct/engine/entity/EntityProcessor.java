package de.instinct.engine.entity;

import java.util.Iterator;

import de.instinct.engine.model.GameState;

public abstract class EntityProcessor {
	
	public static void updateEntity(Entity entity, GameState state, long deltaTime) {
		
	}
	
	public static void initializeEntity(Entity newEntity, GameState state) {
		newEntity.id = state.entityData.entityCounter;
		state.entityData.entityCounter++;
	}
	
	public static void removeDestroyed(Iterator<? extends Entity> entityIterator) {
		while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity.flaggedForDestroy) {
            	entityIterator.remove();
            }
        }
	}

}
