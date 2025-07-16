package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class FontConfiguration {
	
	@Default
	private List<FontTypeConfiguration> fontTypes = new ArrayList<>();

}
