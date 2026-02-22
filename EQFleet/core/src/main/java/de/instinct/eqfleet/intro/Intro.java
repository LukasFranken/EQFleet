package de.instinct.eqfleet.intro;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.auth.dto.TokenVerificationResponse;
import de.instinct.api.core.API;
import de.instinct.eqfleet.App;
import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialMode;
import de.instinct.eqfleet.language.LanguageManager;
import de.instinct.eqfleet.language.model.Language;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.Slideshow;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.Macro;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.BinaryLabeledDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.ClipboardDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.MultiChoiceDialog;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.interactive.SliderSlide;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideChoice;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.model.SlideCondition;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed.Message;
import de.instinct.eqlibgdxutils.rendering.ui.module.slideshow.slide.timed.Pause;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class Intro extends Scene {
	
	private boolean active;
	private Slideshow introSlideshow;
	private ClipboardDialog authKeyInsertDialog;
	private Label versionLabel;

	@Override
	public void init() {
		introSlideshow = new Slideshow();
		versionLabel = new Label("v" + App.VERSION);
		versionLabel.setBounds(new Rectangle(30, 30, 60, 20));
		versionLabel.setColor(SkinManager.skinColor);
	}
	
	@Override
	public void open() {
		loadWelcomeSlides();
		active = true;
		AudioManager.stopRadio();
		AudioManager.playMusic("to_the_stars_intro", true);
	}

	@Override
	public void close() {
		introSlideshow.getSlideList().clear();
		active = false;
		AudioManager.startRadio();
	}
	
	private void loadMenu() {
		Macro startClientMacro = new Macro(new Action() {
			
			@Override
			public void execute() {
				SceneManager.changeTo(SceneType.MENU);
			}
			
		});
		introSlideshow.add(startClientMacro);
		if (authKeyInsertDialog != null) {
			authKeyInsertDialog.setActive(false);
		}
	}

	private void loadWelcomeSlides() {
		Pause startDelay = new Pause();
		startDelay.setDuration(1f);
		introSlideshow.add(startDelay);
		
		Message welcomeMessage = new Message("Welcome");
		welcomeMessage.setDuration(2f);
		welcomeMessage.getConditions().add(new SlideCondition() {
			
			@Override
			public boolean isMet() {
				return !MenuModel.loaded;
				
			}
			
		});
		introSlideshow.add(welcomeMessage);
		Logger.log("INTRO", "Creating welcome slides", ConsoleColor.YELLOW);
		Macro checkAuthAndConnection = new Macro(new Action() {
			
			@Override
			public void execute() {
				if (WebManager.isOnline() && API.authKey.contentEquals("invalid") || API.authKey.isEmpty()) {
					createFirstTimeSlides();
				} else {
					loadMenu();
				}
			}
			
		});
		introSlideshow.add(checkAuthAndConnection);
	}
	
	private void createFirstTimeSlides() {
		Logger.log("INTRO", "Creating first time slides", ConsoleColor.YELLOW);
		if (!LanguageManager.languageIsSet()) {
			createSelectLanguageSlide();
		} else {
			createVolumeSelectionSlide(false);
		}
	}
	
	private void createSelectLanguageSlide() {
		MultiChoiceDialog languageSelectionDialog = new MultiChoiceDialog("Choose your prefered language:", getLanguageChoices());
		languageSelectionDialog.setBackButtonEnabled(false);
		introSlideshow.add(languageSelectionDialog);
	}
	
	private List<SlideChoice> getLanguageChoices() {
		List<SlideChoice> choices = new ArrayList<>();
		for (Language language : LanguageManager.getAvailableLanguages()) {
			SlideChoice slideChoice = new SlideChoice();
			slideChoice.setLabelText(language.getName());
			slideChoice.setAction(new Action() {
				
				@Override
				public void execute() {
					LanguageManager.setLanguage(language.getCode());
					createVolumeSelectionSlide(true);
				}
				
			});
			choices.add(slideChoice);
		}
		return choices;
	}
	
	private void createVolumeSelectionSlide(boolean backButtonEnabled) {
		String volume = PreferenceManager.load("initialvolume");
		if (volume.isEmpty()) {
			AudioManager.playMusic("eqspace2", false);
			ValueChangeAction volumeChangeAction = new ValueChangeAction() {
				
				@Override
				public void execute(float volume) {
					AudioManager.updateUserMusicVolume(volume);
				}
				
			};
			Action volumeConfirmAction = new Action() {

				@Override
				public void execute() {
					String newVolume = String.valueOf(AudioManager.getUserMusicVolume());
					PreferenceManager.save("initialvolume", newVolume);
					AudioManager.updateUserMusicVolume(Float.parseFloat(newVolume));
					AudioManager.updateUserVoiceVolume(Float.parseFloat(newVolume));
					AudioManager.updateUserSfxVolume(Float.parseFloat(newVolume));
					AudioManager.saveUserMusicVolume(Float.parseFloat(newVolume));
					AudioManager.saveUserVoiceVolume(Float.parseFloat(newVolume));
					AudioManager.saveUserSfxVolume(Float.parseFloat(newVolume));
					AudioManager.stop();
					createDoYouHaveAnAccountSlide(true);
				}
				
			};
			SliderSlide volumeSelectionSlide = new SliderSlide("Adjust the audio volume", volumeChangeAction, AudioManager.getUserMusicVolume(), volumeConfirmAction);
			volumeSelectionSlide.setBackButtonEnabled(backButtonEnabled);
			introSlideshow.add(volumeSelectionSlide);
			
		} else {
			createDoYouHaveAnAccountSlide(false);
		}
	}

	private void createDoYouHaveAnAccountSlide(boolean backButtonEnabled) {
		Action acceptAction = new Action() {
			
			@Override
			public void execute() {
				Action useAction = new Action() {
					
					@Override
					public void execute() {
						final String authKey = authKeyInsertDialog.getAuthKey();
						WebManager.enqueue(
							    () -> API.authentication().verify(authKey),
							    result -> {
									if (result == TokenVerificationResponse.VERIFIED) {
										API.authKey = authKey;
										PreferenceManager.save("authkey", authKey);
										loadMenu();
									} else {
										authKeyInsertDialog.setResponse("INVALID KEY"); 
									}
							    }
						);
					}
					
				};
				
				authKeyInsertDialog = new ClipboardDialog("Copy your auth key to the clipboard", useAction);
				introSlideshow.add(authKeyInsertDialog);
			}
			
		};
		
		Action denyAction = new Action() {
			
			@Override
			public void execute() {
				MultiChoiceDialog tutorialSelectionDialog = new MultiChoiceDialog("Choose your prefered tutorial:", getTutorialChoices());
				introSlideshow.add(tutorialSelectionDialog);
			}
			
		};
		
		BinaryLabeledDialog firstTimeDialog = new BinaryLabeledDialog("Do you have an account?", "Yes", "No", acceptAction, denyAction);
		firstTimeDialog.setBackButtonEnabled(backButtonEnabled);
		introSlideshow.add(firstTimeDialog);
	}

	private List<SlideChoice> getTutorialChoices() {
		List<SlideChoice> choices = new ArrayList<>();
		SlideChoice slideChoiceFull = new SlideChoice();
		slideChoiceFull.setLabelText("Full (~3 min)");
		slideChoiceFull.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.enqueue(
					    () -> API.authentication().register(),
					    result -> {
					    	handleRegister(result);
							Game.startTutorial(TutorialMode.FULL);
							active = false;
					    }
				);
			}
			
		});
		choices.add(slideChoiceFull);
		
		SlideChoice slideChoiceShort = new SlideChoice();
		slideChoiceShort.setLabelText("Shorter (~2 min)");
		slideChoiceShort.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.enqueue(
					    () -> API.authentication().register(),
					    result -> {
					    	handleRegister(result);
							Game.startTutorial(TutorialMode.SHORT);
							active = false;
					    }
				);
			}
			
		});
		choices.add(slideChoiceShort);
		
		SlideChoice slideChoiceNo = new SlideChoice();
		slideChoiceNo.setLabelText("None");
		slideChoiceNo.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.enqueue(
					    () -> API.authentication().register(),
					    result -> {
					    	handleRegister(result);
							loadMenu();
					    }
				);
			}

		});
		choices.add(slideChoiceNo);
		return choices;
	}
	
	private void handleRegister(String result) {
		if (result != null) {
			PreferenceManager.save("authkey", result);
		}
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void render() {
		if (active) {
			introSlideshow.render();
			versionLabel.render();
		}
	}

	@Override
	public void dispose() {
		introSlideshow.dispose();
		versionLabel.dispose();
	}

}
