package de.instinct.eqfleet.mining.frontend;

import de.instinct.eqfleet.mining.MiningModel;
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
	
	public MiningRenderer() {
		cameraManager = new MiningCameraManager();
		shipRenderer = new MiningShipRenderer();
		projectileRenderer = new MiningProjectileRenderer();
		hudRenderer = new MiningHudRenderer();
		asteroidRenderer = new AsteroidRenderer();
		worldHudRenderer = new MiningWorldHudRenderer();
		gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(25f)
				.build());
	}
	
	public void init() {
		cameraManager.init();
		shipRenderer.init();
		projectileRenderer.init();
		hudRenderer.init();
		asteroidRenderer.init();
		worldHudRenderer.init();
	}
	
	public void update() {
	    if (MiningModel.state != null) {
	    	cameraManager.update();
		    worldHudRenderer.update();
		    shipRenderer.update();
		    asteroidRenderer.update();
		    projectileRenderer.update();
		    hudRenderer.update();
	    }
	}
	
	public void render() {
		if (MiningModel.state != null) {
			gridRenderer.drawGrid(cameraManager.getCamera());
			worldHudRenderer.render(cameraManager.getCamera());
			asteroidRenderer.render(cameraManager.getCamera());
			projectileRenderer.render(cameraManager.getCamera());
			shipRenderer.render(cameraManager.getCamera());
			hudRenderer.render();
		}
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
