package de.instinct.eqfleet.intro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.instinct.eqfleet.App;
import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.driver.local.tutorial.TutorialMode;
import de.instinct.eqfleet.language.LanguageManager;
import de.instinct.eqfleet.language.model.Language;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.net.model.ConnectionStatus;
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
	
	private Slideshow introSlideshow;
	private ClipboardDialog authKeyInsertDialog;
	private MultiChoiceDialog languageSelectionDialog;
	private Label versionLabel;

	@Override
	public void init() {
		introSlideshow = new Slideshow();
		versionLabel = new Label("v" + App.VERSION);
		versionLabel.setBounds(30, 30, 60, 20);
		versionLabel.setColor(SkinManager.skinColor);
	}
	
	@Override
	public void open() {
		loadWelcomeSlides();
		AudioManager.stopRadio();
	}

	@Override
	public void close() {
		introSlideshow.getSlideList().clear();
	}
	
	private void changeScene(SceneType sceneType) {
		Macro startClientMacro = new Macro(new Action() {
			
			@Override
			public void execute() {
				SceneManager.changeTo(sceneType);
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
		
		int welcomeMessageIndex = LanguageManager.languageIsSet() ? new Random().nextInt(8) : 0;
		Message welcomeMessage = new Message(LanguageManager.get("intro_welcome_" + welcomeMessageIndex));
		welcomeMessage.setDuration(2f);
		welcomeMessage.getConditions().add(new SlideCondition() {
			
			@Override
			public boolean isMet() {
				return !MenuModel.loaded;
				
			}
			
		});
		introSlideshow.add(welcomeMessage);
		Macro checkAuthAndConnection = new Macro(new Action() {
			
			@Override
			public void execute() {
				if (WebManager.status == ConnectionStatus.ONLINE) {
					changeScene(SceneType.MENU);
				} else {
					createFirstTimeSlides();
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
		languageSelectionDialog = new MultiChoiceDialog(LanguageManager.get("intro_language"), getLanguageChoices());
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
					languageSelectionDialog.getLabel().setText(LanguageManager.get("intro_language"));
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
			SliderSlide volumeSelectionSlide = new SliderSlide(LanguageManager.get("intro_volume"), volumeChangeAction, AudioManager.getUserMusicVolume(), volumeConfirmAction);
			volumeSelectionSlide.setBackButtonEnabled(backButtonEnabled);
			volumeSelectionSlide.getBackButton().getLabel().setText(LanguageManager.get("generic_back"));
			volumeSelectionSlide.setBackAction(new Action() {
				
				@Override
				public void execute() {
					AudioManager.stop();
				}
				
			});
			volumeSelectionSlide.getAcceptButton().getLabel().setText(LanguageManager.get("generic_confirm"));
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
						if (WebManager.authenticate(authKey)) {
							changeScene(SceneType.MENU);
						} else {
							authKeyInsertDialog.setResponse("INVALID KEY"); 
						}
					}
					
				};
				
				authKeyInsertDialog = new ClipboardDialog(LanguageManager.get("intro_authkey"), useAction);
				authKeyInsertDialog.setBackButtonEnabled(true);
				authKeyInsertDialog.getBackButton().getLabel().setText(LanguageManager.get("generic_back"));
				introSlideshow.add(authKeyInsertDialog);
			}
			
		};
		
		Action denyAction = new Action() {
			
			@Override
			public void execute() {
				MultiChoiceDialog tutorialSelectionDialog = new MultiChoiceDialog(LanguageManager.get("intro_tutorial"), getTutorialChoices());
				introSlideshow.add(tutorialSelectionDialog);
			}
			
		};
		
		BinaryLabeledDialog firstTimeDialog = new BinaryLabeledDialog(LanguageManager.get("intro_account"), LanguageManager.get("generic_yes"), LanguageManager.get("generic_no"), acceptAction, denyAction);
		firstTimeDialog.setBackButtonEnabled(backButtonEnabled);
		firstTimeDialog.getBackButton().getLabel().setText(LanguageManager.get("generic_back"));
		introSlideshow.add(firstTimeDialog);
	}

	private List<SlideChoice> getTutorialChoices() {
		List<SlideChoice> choices = new ArrayList<>();
		SlideChoice slideChoiceFull = new SlideChoice();
		slideChoiceFull.setLabelText(LanguageManager.get("intro_tutorial_full"));
		slideChoiceFull.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.register();
				changeScene(SceneType.GAME);
				Game.startTutorial(TutorialMode.FULL);
			}
			
		});
		choices.add(slideChoiceFull);
		
		SlideChoice slideChoiceShort = new SlideChoice();
		slideChoiceShort.setLabelText(LanguageManager.get("intro_tutorial_short"));
		slideChoiceShort.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.register();
				changeScene(SceneType.GAME);
				Game.startTutorial(TutorialMode.SHORT);
			}
			
		});
		choices.add(slideChoiceShort);
		
		SlideChoice slideChoiceNo = new SlideChoice();
		slideChoiceNo.setLabelText(LanguageManager.get("intro_tutorial_none"));
		slideChoiceNo.setAction(new Action() {

			@Override
			public void execute() {
				WebManager.register();
				changeScene(SceneType.MENU);
			}

		});
		choices.add(slideChoiceNo);
		return choices;
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void render() {
		introSlideshow.render();
		versionLabel.render();
	}

	@Override
	public void dispose() {
		introSlideshow.dispose();
		versionLabel.dispose();
	}

}
