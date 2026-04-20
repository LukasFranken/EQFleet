package de.instinct.eqfleet.menu.module.mining;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.mining.MiningMode;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class MiningMenuRenderer extends BaseModuleRenderer {
	
	private ColorButton startOnlineButton;
	private ColorButton startButton;

	@Override
	public void init() {
		startOnlineButton = new ColorButton("Online");
		startOnlineButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mode = MiningMode.ONLINE;
				SceneManager.changeTo(SceneType.MINING);
			}
		});
		
		startButton = new ColorButton("Offline");
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MiningModel.mode = MiningMode.OFFLINE;
				SceneManager.changeTo(SceneType.MINING);
			}
		});
	}

	@Override
	public void update() {
		startOnlineButton.setBounds(150, 370, 100, 40);
		startButton.setBounds(150, 430, 100, 40);
	}

	@Override
	public void render() {
		startOnlineButton.render();
		startButton.render();
	}

	@Override
	public void dispose() {
		startOnlineButton.dispose();
		startButton.dispose();
	}

}
