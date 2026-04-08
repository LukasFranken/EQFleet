package de.instinct.eqfleet.menu.module.mining;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;

public class MiningMenuRenderer extends BaseModuleRenderer {
	
	private ColorButton startButton;

	@Override
	public void init() {
		startButton = new ColorButton("Start");
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				SceneManager.changeTo(SceneType.MINING);
			}
		});
	}

	@Override
	public void update() {
		startButton.setBounds(160, 400, 80, 40);
	}

	@Override
	public void render() {
		startButton.render();
	}

	@Override
	public void dispose() {
		startButton.dispose();
	}

}
