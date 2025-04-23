package de.instinct.eqfleet.game.frontend;

public class PlanetPair {
	public final int fromId;
	public final int toId;

	public PlanetPair(int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlanetPair))
			return false;
		PlanetPair other = (PlanetPair) obj;
		return this.fromId == other.fromId && this.toId == other.toId;
	}

	@Override
	public int hashCode() {
		return 31 * fromId + toId;
	}
}
