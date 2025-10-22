package de.instinct.eqfleet.menu.module.ship.component.shippart.level.config;

import com.badlogic.gdx.graphics.Color;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipPartLevelOverviewAreaConfig {
	
	private String tag;
	private String componentType;
	private String componentDescription;
	private Color partColor;
	private float currentValue;
	private float minValue;
	private String minValueLabel;
	private float maxValue;
	private String maxValueLabel;

}
