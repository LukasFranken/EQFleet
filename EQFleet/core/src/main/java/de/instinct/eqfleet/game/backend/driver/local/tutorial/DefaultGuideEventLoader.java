package de.instinct.eqfleet.game.backend.driver.local.tutorial;

import com.badlogic.gdx.math.Vector3;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ActionBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.Condition;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.ConditionalBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.behavior.MessageBehavior;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.guide.subtypes.PauseGuideEvent;
import de.instinct.eqfleet.language.LanguageManager;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.VerticalAlignment;

public class DefaultGuideEventLoader {
	
	private static final float POST_DIALOG_DELAY = 0.5f;
	
	public DialogGuideEvent dialog(int id, VerticalAlignment alignment) {
		DialogGuideEvent dialog = new DialogGuideEvent();
		dialog.setDuration(getVoiceLineDuration(id) + POST_DIALOG_DELAY);
		dialog.setMessage(new MessageBehavior() {
			
			@Override
			public VerticalAlignment getVerticalAlignment() {
				return alignment;
			}
			
			@Override
			public String getText() {
				return getTranslation(id);
			}
			
		});
		dialog.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				playVoiceLine(id);
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		return dialog;
	}
	
	public DialogGuideEvent action(Action action) {
		DialogGuideEvent dialog = new DialogGuideEvent();
		dialog.setDuration(0f);
		dialog.setAction(new ActionBehavior() {
			
			@Override
			public void executeAtStart() {
				action.execute();
			}
			
			@Override
			public void executeAtEnd() {}
			
		});
		return dialog;
	}
	
	public DialogGuideEvent condition(Condition condition) {
		DialogGuideEvent dialog = new DialogGuideEvent();
		dialog.setCondition(new ConditionalBehavior() {

			@Override
			public boolean isStartConditionMet() {
				return true;
			}

			@Override
			public boolean isEndConditionMet() {
				return condition.isMet();
			}
			
		});
		return dialog;
	}
	
	public CameraMoveGuideEvent pan(float duration, Vector3 targetCameraPos) {
		CameraMoveGuideEvent pan = new CameraMoveGuideEvent();
		pan.setDuration(duration);
		pan.setTargetCameraPos(targetCameraPos);
		return pan;
	}
	
	public PauseGuideEvent pause(float duration) {
		PauseGuideEvent pause = new PauseGuideEvent();
		pause.setDuration(duration);
		return pause;
	}
	
	private String getTranslation(int id) {
		return LanguageManager.get("tutorial_voiceline_" + id);
	}
	
	private void playVoiceLine(int id) {
		AudioManager.playVoice("tutorial/" + LanguageManager.getCurrentLanguage().getCode(), "voiceline_" + id);
	}
	
	private float getVoiceLineDuration(int id) {
		return AudioManager.getVoiceDuration("tutorial/" + LanguageManager.getCurrentLanguage().getCode(), "voiceline_" + id);
	}

}
