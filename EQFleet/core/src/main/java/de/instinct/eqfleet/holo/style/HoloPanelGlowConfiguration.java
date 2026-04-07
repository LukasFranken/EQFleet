package de.instinct.eqfleet.holo.style;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

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
	private float glowSize = 13f;

}
