package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;

public class MiningWorldHudRenderer {
	
	private EQCircle recallAreaIndicator;
	
	public void init() {
		recallAreaIndicator = EQCircle.builder()
				.color(new Color(0f, 0f, 0.5f, 0.3f))
				.build();
	}
	
	public void update() {
		recallAreaIndicator.setRadius(MiningModel.state.recallRadius);
	}
	
	public void render(PerspectiveCamera camera) {
		recallAreaIndicator.setProjectionMatrix(camera.combined);
		Shapes.draw(recallAreaIndicator);
	}
	
	public void dispose() {
		
	}

}
