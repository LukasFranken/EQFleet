package de.instinct.engine.model.turret;

import de.instinct.engine.model.UnitData;
import lombok.ToString;

@ToString(callSuper = true)
public class TurretData extends UnitData {
	
	public PlatformData platform;

}
