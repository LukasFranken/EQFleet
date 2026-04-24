package de.instinct.eqfleet.holo.style;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class HoloPanelGlowConfiguration {
	
	@Default
	private float glowPad = 24f;
	
	@Default
	private float glowKAlpha = 0.4f;
	
	@Default
	private float glowKRgb = 0.3f;
	
	@Default
	private float glowSize = 25f;
	
	@Default
	private float glowAnimationSpeed = 60f;
	
	@Default
	private float glowAnimationStrength = 0.15f;

}
