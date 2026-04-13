package de.instinct.eqfleet.mining.input;

public class MiningInput {
	
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean shoot;
	
	public boolean isIdenticalTo(MiningInput other) {
		return this.up == other.up 
			&& this.down == other.down
			&& this.left == other.left
			&& this.right == other.right
			&& this.shoot == other.shoot;
	}

}
