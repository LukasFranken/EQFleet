package de.instinct.eqfleet.mining;

import com.badlogic.gdx.Gdx;

import de.instinct.engine.mining.data.MiningGameState;
import de.instinct.eqfleet.mining.frontend.MiningInputManager;
import de.instinct.eqfleet.mining.frontend.MiningRenderer;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqlibgdxutils.engine.cursor.CursorUtil;
import de.instinct.eqlibgdxutils.engine.cursor.Hotspot;

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
		MiningModel.state = new MiningGameState();
		MiningEngineAPI.initialize(MiningModel.state);
		//CursorUtil.setCursor("crosshair", Hotspot.CENTER);
	}

	@Override
	public void close() {
		//CursorUtil.setCursor("cursor", Hotspot.TOPLEFT);
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
 