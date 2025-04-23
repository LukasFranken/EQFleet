package de.instinct.eqfleet.intro;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.eqfleet.game.backend.engine.local.tutorial.TutorialMode;
import de.instinct.eqfleet.menu.Menu;
import de.instinct.eqfleet.net.GlobalStaticData;
import de.instinct.eqfleet.net.auth.AuthenticationInterface;
import de.instinct.eqfleet.net.auth.dto.TokenVerificationResponse;
import de.instinct.eqfleet.net.meta.MetaInterface;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slideshow;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.BinaryLabeledDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.ClipboardDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.MultiChoiceDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideAction;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideButton;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed.Macro;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed.Message;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed.Pause;

public class Intro {
	
	public static boolean active;
	
	private static Slideshow introSlideshow;
	private static Queue<Slide> elementQueue;
	
	private static ClipboardDialog authKeyInsertDialog;

	public static void init() {
		initializeSlideshow();
		loadWelcomeSlides();
		active = true;
		String authKey = PreferenceUtil.load("authkey");
		if (!authKey.contentEquals("")) {
			verifyAuthKey(authKey, true);
		} else {
			loadFirstTimeSlides();
		}
	}

	private static void verifyAuthKey(String authKey, boolean loadfirst) {
		AuthenticationInterface.verify(authKey, new ResponseAction() {
			
			@Override
			public void execute(NetworkResponse response) {
				TokenVerificationResponse tokenVerificationResponse = ObjectJSONMapper.mapJSON(response.getPayload(), TokenVerificationResponse.class);
				if (tokenVerificationResponse == TokenVerificationResponse.VERIFIED) {
					GlobalStaticData.authKey = authKey;
					PreferenceUtil.save("authkey", authKey);
					MetaInterface.loadProfile();
					loadMenu();
				} else {
					if (loadfirst) {
						loadFirstTimeSlides();
					} else {
						authKeyInsertDialog.setResponse("INVALID KEY"); 
					}
				}
			}
			
		});
	}
	
	private static void loadMenu() {
		Macro startClientMacro = new Macro();
		startClientMacro.setAction(new Action() {
			
			@Override
			public void execute() {
				active = false;
				Menu.activate();
			}
			
		});
		elementQueue.add(startClientMacro);
		if (authKeyInsertDialog != null) {
			authKeyInsertDialog.setActive(false);
		}
	}

	private static void initializeSlideshow() {
		introSlideshow = new Slideshow();
		elementQueue = new ConcurrentLinkedQueue<>();
		introSlideshow.setElementQueue(elementQueue);
	}

	private static void loadWelcomeSlides() {
		Pause startDelay = new Pause();
		startDelay.setDuration(1f);
		elementQueue.add(startDelay);
		
		Message welcomeMessage = new Message();
		welcomeMessage.setDuration(2.5f);
		welcomeMessage.setMessage("Welcome!");
		elementQueue.add(welcomeMessage);
	}
	
	private static void loadFirstTimeSlides() {
		BinaryLabeledDialog firstTimeDialog = new BinaryLabeledDialog();
		firstTimeDialog.setMessage("Do you have an account?");
		firstTimeDialog.setAcceptLabel("Yes");
		firstTimeDialog.setAcceptAction(new SlideAction() {
					boolean triggered = false;
					
					@Override
					public void execute() {
						if (!triggered) {
							authKeyInsertDialog = new ClipboardDialog();
							authKeyInsertDialog.setMessage("Copy your auth key to the clipboard");
							authKeyInsertDialog.getUseButton().setAction(new Action() {
								
								@Override
								public void execute() {
									verifyAuthKey(authKeyInsertDialog.getAuthKey(), false);
								}
								
							});
							elementQueue.add(authKeyInsertDialog);
							
							
						}
						triggered = true;
					}
					
					@Override
					public boolean executed() {
						return triggered;
					}
					
				});
		firstTimeDialog.setDenyLabel("No");
		firstTimeDialog.setDenyAction(new SlideAction() {
					boolean triggered = false;
					
					@Override
					public void execute() {
						if (!triggered) {
							MultiChoiceDialog tutorialSelectionDialog = new MultiChoiceDialog("Choose your prefered tutorial:");
							List<SlideButton> choices = new ArrayList<>();
							
							SlideButton slideButtonFullStory = new SlideButton();
							slideButtonFullStory.setLabelText("Full with story (~5 min)");
							slideButtonFullStory.setAction(new SlideAction() {
								boolean triggered = false;
								
								@Override
								public void execute() {
									AuthenticationInterface.register(new ResponseAction() {
										
										@Override
										public void execute(NetworkResponse response) {
											Menu.loadTutorial(TutorialMode.STORY_FULL);
											triggered = true;
										}
										
									});
								}
								
								@Override
								public boolean executed() {
									return triggered;
								}
								
							});
							choices.add(slideButtonFullStory);
							
							SlideButton slideButtonFull = new SlideButton();
							slideButtonFull.setLabelText("Full (~4 min)");
							slideButtonFull.setAction(new SlideAction() {
								boolean triggered = false;
								
								@Override
								public void execute() {
									AuthenticationInterface.register(new ResponseAction() {
										
										@Override
										public void execute(NetworkResponse response) {
											Menu.loadTutorial(TutorialMode.FULL);
											triggered = true;
										}
										
									});
								}
								
								@Override
								public boolean executed() {
									return triggered;
								}
								
							});
							choices.add(slideButtonFull);
							
							SlideButton slideButtonShort = new SlideButton();
							slideButtonShort.setLabelText("Short (~2 min)");
							slideButtonShort.setAction(new SlideAction() {
								boolean triggered = false;
								
								@Override
								public void execute() {
									AuthenticationInterface.register(new ResponseAction() {
										
										@Override
										public void execute(NetworkResponse response) {
											Menu.loadTutorial(TutorialMode.SHORT);
											triggered = true;
										}
										
									});
								}
								
								@Override
								public boolean executed() {
									return triggered;
								}
								
							});
							choices.add(slideButtonShort);
							
							SlideButton slideButtonNo = new SlideButton();
							slideButtonNo.setLabelText("None");
							slideButtonNo.setAction(new SlideAction() {
								boolean triggered = false;
								
								@Override
								public void execute() {
									AuthenticationInterface.register(new ResponseAction() {
										
										@Override
										public void execute(NetworkResponse response) {
											loadMenu();
											triggered = true;
										}
										
									});
								}
								
								@Override
								public boolean executed() {
									return triggered;
								}
								
							});
							choices.add(slideButtonNo);
							
							tutorialSelectionDialog.setChoices(choices);
							tutorialSelectionDialog.build();
							elementQueue.add(tutorialSelectionDialog);
						}
						triggered = true;
					}

					@Override
					public boolean executed() {
						return triggered;
					}
					
				});
		firstTimeDialog.build();
		elementQueue.add(firstTimeDialog);
	}

	public static void render() {
		if (active) {
			introSlideshow.render();
		}
	}

}
