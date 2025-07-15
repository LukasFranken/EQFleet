package de.instinct.eqlibgdxutils.rendering.ui.font;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class FontConfiguration {
	
	@Default
	private String name = "source";
	
	@Default
	private int largeSize = 32;
	@Default
	private int normalSize = 20;
	@Default
	private int boldSize = 20;
	@Default
	private int smallSize = 18;
	@Default
	private int tinySize = 14;

}
