package de.instinct.eqfleet.mining.frontend;

import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class MiningRenderer {
	
	private MiningCameraManager cameraManager;
	private MiningShipRenderer shipRenderer;
	private MiningProjectileRenderer projectileRenderer;
	private MiningHudRenderer hudRenderer;
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
		gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(20f)
				.build());
	}
	
	public void update() {
	    cameraManager.update();
	    shipRenderer.update();
	    asteroidRenderer.update();
	    projectileRenderer.update();
	    hudRenderer.update();
	}
	
	public void render() {
		gridRenderer.drawGrid(cameraManager.getCamera());
		projectileRenderer.render(cameraManager.getCamera());
		shipRenderer.render(cameraManager.getCamera());
		asteroidRenderer.render(cameraManager.getCamera());
		hudRenderer.render();
	}
	
	public void dispose() {
		shipRenderer.dispose();
		projectileRenderer.dispose();
		hudRenderer.dispose();
	}

}
