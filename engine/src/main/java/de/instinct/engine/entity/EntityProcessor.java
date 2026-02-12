package de.instinct.engine.entity;

import java.util.Iterator;

import de.instinct.engine.model.GameState;

public abstract class EntityProcessor {
	
	public void updateEntity(Entity entity, GameState state, long deltaTime) {
		
	}
	
	public void initializeEntity(Entity newEntity, GameState state) {
		newEntity.id = state.entityData.entityCounter;
		state.entityData.entityCounter++;
	}
	
	public void removeDestroyed(Iterator<? extends Entity> entityIterator) {
		while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity.flaggedForDestroy) {
            	entityIterator.remove();
            }
        }
	}

}
