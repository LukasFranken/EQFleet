package de.instinct.eqlibgdxutils.rendering.ui.popup;

import de.instinct.eqlibgdxutils.rendering.ui.container.ElementContainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Popup {
	
	private String title;
	private ElementContainer contentContainer;
	private boolean closeOnClickOutside;

}
