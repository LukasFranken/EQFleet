package de.instinct.eqlibgdxutils.rendering.ui.font;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class FontTypeConfiguration {
	
	@Default
	private FontType type = FontType.NORMAL;
	@Default
	private String name = "source";
	@Default
	private int size = 20;

}
