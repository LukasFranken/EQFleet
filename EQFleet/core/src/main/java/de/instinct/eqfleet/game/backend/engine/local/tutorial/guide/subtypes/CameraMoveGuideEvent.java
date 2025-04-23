package de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes;

import com.badlogic.gdx.math.Vector3;

import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CameraMoveGuideEvent extends GuideEvent {
	
	private Vector3 startCameraPos;
	private Vector3 targetCameraPos;

}
