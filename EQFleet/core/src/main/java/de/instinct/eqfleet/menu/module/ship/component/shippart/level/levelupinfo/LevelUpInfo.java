package de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelUpInfo {
	
	private String tagValue;
	private String currentValue;
	private String changeValue;
	private String nextValue;

}
