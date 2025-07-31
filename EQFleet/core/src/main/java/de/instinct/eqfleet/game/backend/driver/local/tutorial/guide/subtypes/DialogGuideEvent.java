package de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes;

import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ActionBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ConditionalBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.MessageBehavior;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DialogGuideEvent extends GuideEvent {
	
	private ActionBehavior action;
	private ConditionalBehavior condition;
	private MessageBehavior message;

}
