package de.instinct.eqfleet.menu.module.ship.component.shippart;

public class ShipSubtypeDescription {
	
	public static String get(String subtype) {
		switch (subtype.toLowerCase()) {
			case "fighter":
				return "While lacking in firepower\nand durability, fighters\nare cheap and fast.";
			case "cruiser":
				return "(unimplemented)";
			case "destroyer":
				return "(unimplemented)";
			case "titan":
				return "(unimplemented)";
				
			case "ion":
				return "Ion engines are lightweight\nand provide almost instant\nvelocity for small ships.";
			case "antimatter":
				return "Antimatter engines take time\nto build up, but can move\nlarge ships";
				
			case "carbon":
				return "Designed for speed and\nmaneuverability, carbon hulls\nare light but fragile.";
			case "nanobot":
				return "Unlike other hulls,\nnanobot hulls can self-repair\ndamage over time.";
			case "alloy":
				return "Alloy hulls are heavy and\ndurable, but sacrifice\nspeed and agility.";
				
			case "plasma":
				return "Constant generation of high-\ntemperature plasma allows for\nregenerative matter and energy\nabsorption.";
			case "nullpoint":
				return "Nullpoint shields can fully\nabsorb incoming damage by\ncreating a short-lived\nblack hole.";
			case "graviton":
				return "Through short-burst gravitational\nwave emission, graviton shields\ncan reflect incoming projectiles.";
				
			case "projectile":
				return "Slower moving than other\nweapon types, projectile weapons\ntypically hit harder.";
			case "laser":
				return "Quick to recharge and fire,\nlaser weapons are designed\nfor quantity over quality.";
			case "missile":
				return "Missiles take a long time to\nreload, but can track targets\nand explode on impact.";
			case "beam":
				return "Beam weapons sacrifice\npunching power for instant,\nsustained damage over time.";
				
			default:
				return "Unknown Subtype:\nNo description available.";
		}
	}

}
