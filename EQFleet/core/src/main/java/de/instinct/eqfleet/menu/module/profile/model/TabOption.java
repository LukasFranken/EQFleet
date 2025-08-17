package de.instinct.eqfleet.menu.module.profile.model;

import de.instinct.eqlibgdxutils.generic.Action;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TabOption {
	
	private String label;
	private Action switchAction;

}
