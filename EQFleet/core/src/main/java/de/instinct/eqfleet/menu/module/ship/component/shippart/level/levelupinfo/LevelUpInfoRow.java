package de.instinct.eqfleet.menu.module.ship.component.shippart.level.levelupinfo;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelUpInfoRow {
	
	private Label tagLabel;
	private ElementStack currentAndNextValueLabelStack;
	private Label changeValueLabel;
	private boolean header;

}
