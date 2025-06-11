package de.instinct.eqfleet.game.frontend.guide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.GuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.CameraMoveGuideEvent;
import de.instinct.eqfleet.game.backend.engine.local.tutorial.guide.subtypes.DialogGuideEvent;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class GuideRenderer {
	
	private GuideEvent currentGuideEvent;
	
	public GuideRenderer() {
		currentGuideEvent = null;
	}

	public void renderEvents(PerspectiveCamera camera) {
		processEventPolling();
		if (currentGuideEvent != null) {
			renderEvent(camera);
			processEventTermination();
		}
	}

	private void processEventPolling() {
		if (currentGuideEvent == null && GameModel.guidedEvents != null) {
			GuideEvent peekedGuideEvent = GameModel.guidedEvents.peek();
			if (peekedGuideEvent != null) {
				if (peekedGuideEvent instanceof DialogGuideEvent) {
					DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)peekedGuideEvent;
					if (currentPeekedDialogGuideEvent.getCondition() != null) {
						if (currentPeekedDialogGuideEvent.getCondition().isStartConditionMet()) {
							currentGuideEvent = GameModel.guidedEvents.poll();
						}
					} else {
						currentGuideEvent = GameModel.guidedEvents.poll();
					}
				} else {
					currentGuideEvent = GameModel.guidedEvents.poll();
				}
			}
			
			if (currentGuideEvent != null && currentGuideEvent instanceof DialogGuideEvent) {
				DialogGuideEvent currentDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
				if (currentDialogGuideEvent.getAction() != null) {
					currentDialogGuideEvent.getAction().executeAtStart();
				}
			}
		} else {
			if (GameModel.guidedEvents == null) {
				currentGuideEvent = null;
			}
		}
	}
	
	private void renderEvent(PerspectiveCamera camera) {
		if (currentGuideEvent instanceof CameraMoveGuideEvent) {
		    CameraMoveGuideEvent camEvent = (CameraMoveGuideEvent) currentGuideEvent;
		    
		    if (camEvent.getStartCameraPos() == null) {
		        camEvent.setStartCameraPos(new Vector3(camera.position));
		    }
		    
		    float ratio = camEvent.getElapsed() / camEvent.getDuration();
		    
		    Vector3 startPos = camEvent.getStartCameraPos();
		    Vector3 targetPos = camEvent.getTargetCameraPos();
		    float newX = MathUtil.easeInOut(startPos.x, targetPos.x, ratio);
		    float newY = MathUtil.easeInOut(startPos.y, targetPos.y, ratio);
		    float newZ = MathUtil.easeInOut(startPos.z, targetPos.z, ratio);
		    
		    camera.position.set(newX, newY, newZ);
		    camera.update();
		}
		if (currentGuideEvent instanceof DialogGuideEvent) {
			DialogGuideEvent currentDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
			if (currentDialogGuideEvent.getMessage() != null) {
				Label messageLabel = new Label(currentDialogGuideEvent.getMessage().getText());
				switch (currentDialogGuideEvent.getMessage().getVerticalAlignment()) {
				case TOP:
					messageLabel.setBounds(new Rectangle(20, Gdx.graphics.getHeight() - 150, Gdx.graphics.getWidth() - 40, 70));
					break;
				case CENTER:
					messageLabel.setBounds(new Rectangle(20, (Gdx.graphics.getHeight() / 2) - 35, Gdx.graphics.getWidth() - 40, 70));
					break;
				case BOTTOM:
					messageLabel.setBounds(new Rectangle(20, 20, Gdx.graphics.getWidth() - 40, 70));
					break;
					
				}
				messageLabel.setBackgroundColor(Color.BLACK);
				Border labelBorder = new Border();
				labelBorder.setColor(DefaultUIValues.skinColor);
				labelBorder.setSize(2);
				messageLabel.setBorder(labelBorder);
				messageLabel.render();
			}
			
		}
	}
	
	private void processEventTermination() {
		boolean setToTerminate = false;
		currentGuideEvent.setElapsed(currentGuideEvent.getElapsed() + Gdx.graphics.getDeltaTime());
		if (currentGuideEvent instanceof DialogGuideEvent) {
			DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
			if (currentPeekedDialogGuideEvent.getCondition() != null) {
				if (currentPeekedDialogGuideEvent.getCondition().isEndConditionMet()) {
					setToTerminate = true;
				}
			} else {
				if (currentGuideEvent.getElapsed() > currentGuideEvent.getDuration()) {
					setToTerminate = true;
				}
			}
		} else {
			if (currentGuideEvent.getElapsed() > currentGuideEvent.getDuration()) {
				setToTerminate = true;
			}
		}
		
		if (setToTerminate) {
			if (currentGuideEvent instanceof DialogGuideEvent) {
				DialogGuideEvent currentPeekedDialogGuideEvent = (DialogGuideEvent)currentGuideEvent;
				if  (currentPeekedDialogGuideEvent.getAction() != null) {
					currentPeekedDialogGuideEvent.getAction().executeAtEnd();
				}
			}
			currentGuideEvent = null;
		}
	}

}
