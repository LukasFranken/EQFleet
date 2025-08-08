package de.instinct.eqfleet.menu.module.settings;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.settings.model.LabelUpdateAction;
import de.instinct.eqfleet.menu.module.settings.model.LabeledSlider;
import de.instinct.eqfleet.menu.module.settings.model.ValueChangeAction;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class SettingsRenderer extends BaseModuleRenderer {
	
	private Label musicLabel;
	private Label voiceLabel;
	private Label sfxLabel;
	private LabeledSlider musicVolumeSlider;
	private LabeledSlider voiceVolumeSlider;
	private LabeledSlider sfxVolumeSlider;
	
	public SettingsRenderer() {
		
	}

	@Override
	public void render() {
		musicVolumeSlider.render();
		voiceVolumeSlider.render();
		sfxVolumeSlider.render();
		musicLabel.render();
		musicLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		voiceLabel.render();
		voiceLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		sfxLabel.render();
		sfxLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
	}

	@Override
	public void reload() {
		musicLabel = new Label("Music Volume");
		musicLabel.setFixedHeight(30);
		musicLabel.setFixedWidth(100);
		musicLabel.setPosition(MenuModel.moduleBounds.x + 20, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - musicLabel.getFixedHeight() - 20);
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
		musicVolumeSlider.setBounds(new Rectangle(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, musicLabel.getBounds().y, 160, 30));
		musicVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserMusicVolume(musicVolumeSlider.getSlider().getCurrentValue());
			}
			
		});
		
		voiceLabel = new Label("Voice Volume");
		voiceLabel.setFixedHeight(30);
		voiceLabel.setFixedWidth(100);
		voiceLabel.setPosition(MenuModel.moduleBounds.x + 20, musicLabel.getBounds().y - voiceLabel.getFixedHeight() - 20);
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
		voiceVolumeSlider.setBounds(new Rectangle(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, voiceLabel.getBounds().y, 160, 30));
		voiceVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserVoiceVolume(voiceVolumeSlider.getSlider().getCurrentValue());
				AudioManager.playVoice("units");
			}
			
		});
		
		sfxLabel = new Label("SFX Volume");
		sfxLabel.setFixedHeight(30);
		sfxLabel.setFixedWidth(100);
		sfxLabel.setPosition(MenuModel.moduleBounds.x + 20, voiceLabel.getBounds().y - sfxLabel.getFixedHeight() - 20);
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
		sfxVolumeSlider.setBounds(new Rectangle(MenuModel.moduleBounds.x + MenuModel.moduleBounds.width - 160 - 20, sfxLabel.getBounds().y, 160, 30));
		sfxVolumeSlider.getSlider().setDragEndAction(new Action() {
			
			@Override
			public void execute() {
				AudioManager.saveUserSfxVolume(sfxVolumeSlider.getSlider().getCurrentValue());
				AudioManager.playSfx("projectile");
			}
			
		});
	}

	@Override
	public void dispose() {
		
	}

}
