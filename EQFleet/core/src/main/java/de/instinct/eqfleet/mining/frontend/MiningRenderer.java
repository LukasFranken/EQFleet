package de.instinct.eqfleet.mining.frontend;

import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;

public class MiningRenderer {
	
	private MiningCameraManager cameraManager;
	private MiningShipRenderer shipRenderer;
	private MiningHudRenderer hudRenderer;
	private GridRenderer gridRenderer;
	
	public void init() {
		cameraManager = new MiningCameraManager();
		cameraManager.init();
		shipRenderer = new MiningShipRenderer();
		shipRenderer.init();
		hudRenderer = new MiningHudRenderer();
		hudRenderer.init();
		gridRenderer = new GridRenderer(GridConfiguration.builder().build());
	}
	
	public void update() {
	    cameraManager.update();
	    shipRenderer.update();
	    hudRenderer.update();
	}
	
	public void render() {
		gridRenderer.drawGrid(cameraManager.getCamera());
		shipRenderer.render(cameraManager.getCamera());
		hudRenderer.render();
	}
	
	public void dispose() {
		shipRenderer.dispose();
		hudRenderer.dispose();
	}

}
