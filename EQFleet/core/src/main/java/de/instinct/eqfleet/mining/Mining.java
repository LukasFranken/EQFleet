package de.instinct.eqfleet.mining;

import de.instinct.eqfleet.mining.driver.MiningDriver;
import de.instinct.eqfleet.mining.driver.offline.MiningOfflineDriver;
import de.instinct.eqfleet.mining.driver.online.MiningOnlineDriver;
import de.instinct.eqfleet.mining.frontend.MiningRenderer;
import de.instinct.eqfleet.mining.input.MiningInputManager;
import de.instinct.eqfleet.scene.Scene;

public class Mining extends Scene {
	
	private MiningRenderer renderer;
	private MiningInputManager inputManager;
	private MiningDriver driver;

	@Override
	public void init() {
		renderer = new MiningRenderer();
		renderer.init();
		inputManager = new MiningInputManager();
	}

	@Override
	public void open() {
		switch (MiningModel.mode) {
		case ONLINE:
			driver = new MiningOnlineDriver();
			break;
		case OFFLINE:
			driver = new MiningOfflineDriver();
			break;
		}
	}

	@Override
	public void close() {
		driver.dispose();
	}

	@Override
	public void update() {
		renderer.update();
		inputManager.update();
		driver.update();
	}

	@Override
	public void render() {
		renderer.render();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		if (driver != null) driver.dispose();
	}

}
 