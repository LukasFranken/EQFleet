package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.instinct.engine.mining.entity.asteroid.Asteroid;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;

public class AsteroidRenderer {
	
	private ModelInstance model;
	private EQCircle asteroidBoundsCircle;
	
	public void init() {
		model = ModelLoader.instanciate("asteroid");
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		
		asteroidBoundsCircle = EQCircle.builder()
				.build();
	}
	
	public void update() {
	    
	}
	
	public void render(PerspectiveCamera camera) {
		for (Asteroid asteroid : MiningModel.state.entityData.asteroids) {
			Color oreColor = OreManager.getColorForResourceType(asteroid.resourceType);
			model.materials.get(1).set(ColorAttribute.createDiffuse(oreColor));
			model.transform.idt();
		    model.transform.translate(asteroid.position.x, asteroid.position.y, 0);
		    model.transform.rotate(0, 0, 1, asteroid.direction.angleDeg());
		    float scale = asteroid.radius * 0.6f;
		    model.transform.scale(scale, scale, scale);
			ModelRenderer.render(camera, model);
			
			asteroidBoundsCircle.setProjectionMatrix(camera.combined);
			asteroidBoundsCircle.setPosition(asteroid.position);
			asteroidBoundsCircle.setRadius(asteroid.radius);
			asteroidBoundsCircle.setColor(oreColor.r, oreColor.g, oreColor.b, 0.4f);
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Shapes.draw(asteroidBoundsCircle);
		}
	}

	public void dispose() {
		
	}

}
