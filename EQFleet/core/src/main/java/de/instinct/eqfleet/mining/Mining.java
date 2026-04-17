package de.instinct.eqfleet.mining;

import com.badlogic.gdx.Gdx;

import de.instinct.eqfleet.mining.frontend.MiningRenderer;
import de.instinct.eqfleet.mining.input.MiningInputManager;
import de.instinct.eqfleet.scene.Scene;

public class Mining extends Scene {
	
	private MiningRenderer renderer;
	private MiningInputManager inputManager;

	@Override
	public void init() {
		renderer = new MiningRenderer();
		renderer.init();
		inputManager = new MiningInputManager();
	}

	@Override
	public void open() {
		MiningEngineAPI.initialize();
	}

	@Override
	public void close() {
		
	}

	@Override
	public void update() {
		inputManager.update();
		MiningEngineAPI.update(MiningModel.state, (long)(Gdx.graphics.getDeltaTime() * 1000L));
		renderer.update();
	}

	@Override
	public void render() {
		renderer.render();
	}

	@Override
	public void dispose() {
		renderer.dispose();
	}

}
 