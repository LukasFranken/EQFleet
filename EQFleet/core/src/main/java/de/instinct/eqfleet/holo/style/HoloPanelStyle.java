package de.instinct.eqfleet.holo.style;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class HoloPanelStyle {
	
	@Default
	private float bevelSize = 8f;
	
	@Default
	private float softness = 1f;
	
	@Default
	private float fillAlpha = 0.2f;
	
	@Default
	private float reflectionStrength = 0.15f;
	
	@Default
	private float borderSize = 0.5f;
	
	@Default
	private HoloPanelGlowConfiguration glowConfiguration = HoloPanelGlowConfiguration.builder().build();
    
}