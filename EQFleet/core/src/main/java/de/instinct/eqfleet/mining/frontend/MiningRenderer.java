package de.instinct.eqfleet.mining.frontend;

import de.instinct.eqfleet.mining.frontend.hud.MiningHudRenderer;
import de.instinct.eqfleet.mining.frontend.hud.MiningWorldHudRenderer;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class MiningRenderer {
	
	private MiningCameraManager cameraManager;
	private MiningShipRenderer shipRenderer;
	private MiningProjectileRenderer projectileRenderer;
	private MiningHudRenderer hudRenderer;
	private MiningWorldHudRenderer worldHudRenderer;
	private GridRenderer gridRenderer;
	private AsteroidRenderer asteroidRenderer;
	
	public void init() {
		cameraManager = new MiningCameraManager();
		cameraManager.init();
		shipRenderer = new MiningShipRenderer();
		shipRenderer.init();
		projectileRenderer = new MiningProjectileRenderer();
		projectileRenderer.init();
		hudRenderer = new MiningHudRenderer();
		hudRenderer.init();
		asteroidRenderer = new AsteroidRenderer();
		asteroidRenderer.init();
		worldHudRenderer = new MiningWorldHudRenderer();
		worldHudRenderer.init();
		gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(25f)
				.build());
	}
	
	public void update() {
	    cameraManager.update();
	    worldHudRenderer.update();
	    shipRenderer.update();
	    asteroidRenderer.update();
	    projectileRenderer.update();
	    hudRenderer.update();
	}
	
	public void render() {
		gridRenderer.drawGrid(cameraManager.getCamera());
		worldHudRenderer.render(cameraManager.getCamera());
		asteroidRenderer.render(cameraManager.getCamera());
		projectileRenderer.render(cameraManager.getCamera());
		shipRenderer.render(cameraManager.getCamera());
		hudRenderer.render();
	}
	
	public void dispose() {
		gridRenderer.dispose();
		worldHudRenderer.dispose();
		shipRenderer.dispose();
		projectileRenderer.dispose();
		asteroidRenderer.dispose();
		hudRenderer.dispose();
	}

}
