package de.instinct.eqfleet.menu.module.settings;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabelUpdateAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabeledSlider;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class SettingsRenderer extends BaseModuleRenderer {
	
	private Label musicLabel;
	private Label voiceLabel;
	private Label sfxLabel;
	private LabeledSlider musicVolumeSlider;
	private LabeledSlider voiceVolumeSlider;
	private LabeledSlider sfxVolumeSlider;
	
	@Override
	public void init() {
		musicLabel = new Label("Music Volume");
		musicLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		musicLabel.setFixedHeight(30);
		musicLabel.setFixedWidth(100);
		musicVolumeSlider = new LabeledSlider(new ValueChangeAction() {
			
			@Override
			public void execute(float newValue) {
				AudioManager.updateUserMusicVolume(newValue);
			}
			
		}, AudioManager.getUserMusicVolume(), new LabelUpdateAction() {
			
			@Override
			public String getLabelText(float currentValue) {
				return StringUtils.format(MathUtil.linear(0, 100, currentValue), 0);
			}
			
		});
		musicVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserMusicVolume(musicVolumeSlider.getSlider().getCurrentValue());
			}
			
		});
		
		voiceLabel = new Label("Voice Volume");
		voiceLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		voiceLabel.setFixedHeight(30);
		voiceLabel.setFixedWidth(100);
		voiceVolumeSlider = new LabeledSlider(new ValueChangeAction() {
			
			@Override
			public void execute(float newValue) {
				AudioManager.updateUserVoiceVolume(newValue);
			}
			
		}, AudioManager.getUserVoiceVolume(), new LabelUpdateAction() {
			
			@Override
			public String getLabelText(float currentValue) {
				return StringUtils.format(MathUtil.linear(0, 100, currentValue), 0);
			}
			
		});
		voiceVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserVoiceVolume(voiceVolumeSlider.getSlider().getCurrentValue());
				AudioManager.playVoice("game", "units");
			}
			
		});
		
		sfxLabel = new Label("SFX Volume");
		sfxLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		sfxLabel.setFixedHeight(30);
		sfxLabel.setFixedWidth(100);
		sfxVolumeSlider = new LabeledSlider(new ValueChangeAction() {
			
			@Override
			public void execute(float newValue) {
				AudioManager.updateUserSfxVolume(newValue);
			}
			
		}, AudioManager.getUserSfxVolume(), new LabelUpdateAction() {
			
			@Override
			public String getLabelText(float currentValue) {
				return StringUtils.format(MathUtil.linear(0, 100, currentValue), 0);
			}
			
		});
		sfxVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserSfxVolume(sfxVolumeSlider.getSlider().getCurrentValue());
				AudioManager.playSfx("projectile");
			}
			
		});
	}

	@Override
	public void update() {
		sfxLabel.setPosition(MenuModel.moduleBounds.x + 20, voiceLabel.getBounds().y - sfxLabel.getFixedHeight() - 20);
		sfxVolumeSlider.setBounds(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, sfxLabel.getBounds().y, 160, 30);
		voiceLabel.setPosition(MenuModel.moduleBounds.x + 20, musicLabel.getBounds().y - voiceLabel.getFixedHeight() - 20);
		voiceVolumeSlider.setBounds(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, voiceLabel.getBounds().y, 160, 30);
		musicLabel.setPosition(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - musicLabel.getFixedHeight() - 20);
		musicVolumeSlider.setBounds(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, musicLabel.getBounds().y, 160, 30);
	}

	@Override
	public void render() {
		musicVolumeSlider.render();
		voiceVolumeSlider.render();
		sfxVolumeSlider.render();
		musicLabel.render();
		voiceLabel.render();
		sfxLabel.render();
	}

	@Override
	public void dispose() {
		
	}

}
