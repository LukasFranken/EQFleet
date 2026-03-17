package de.instinct.eqfleet.menu.main.header.components.social;

import de.instinct.eqlibgdxutils.generic.Action;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiniWindowConfiguration {
	
	private String title;
	private String content;
	private String acceptButtonText;
	private Action acceptAction;
	private String declineButtonText;
	private Action declineAction;

}
