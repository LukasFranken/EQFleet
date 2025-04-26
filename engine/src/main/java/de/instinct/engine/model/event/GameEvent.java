package de.instinct.engine.model.event;

public abstract class GameEvent implements Comparable<GameEvent> {
	
    public long startGameTimeMS;
    public long durationMS;

    public long eventFinalizationTimestamp() {
        return startGameTimeMS + durationMS;
    }

    @Override
    public int compareTo(GameEvent other) {
        return Long.compare(this.eventFinalizationTimestamp(), other.eventFinalizationTimestamp());
    }
    
}
