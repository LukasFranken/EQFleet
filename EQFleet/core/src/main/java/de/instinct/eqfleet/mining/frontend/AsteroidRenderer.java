package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.instinct.engine.mining.entity.asteroid.Asteroid;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class AsteroidRenderer {
	
private ModelInstance model;
	
	public void init() {
		model = ModelLoader.instanciate("asteroid");
		model.materials.get(1).set(ColorAttribute.createDiffuse(SkinManager.darkerSkinColor));
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
	}
	
	public void update() {
	    
	}
	
	public void render(PerspectiveCamera camera) {
		for (Asteroid asteroid : MiningModel.state.entityData.asteroids) {
			model.transform.idt();
		    model.transform.translate(asteroid.position.x, asteroid.position.y, 0);
		    model.transform.rotate(0, 0, 1, asteroid.direction.angleDeg());
		    model.transform.scale(8f, 8f, 8f);
			ModelRenderer.render(camera, model);
		}
	}

	public void dispose() {
		
	}

}
