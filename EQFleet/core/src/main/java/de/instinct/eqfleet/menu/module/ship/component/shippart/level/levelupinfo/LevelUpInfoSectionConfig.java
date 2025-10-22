package de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo;

import java.util.List;

import com.badlogic.gdx.graphics.Color;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelUpInfoSectionConfig {
	
	private int currentLevel;
	private int nextLevel;
	private List<LevelUpInfo> levelUpInfos;
	private Color color;

}
