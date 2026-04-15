package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.instinct.engine.mining.entity.projectile.MiningProjectile;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class MiningProjectileRenderer {
	
	private ModelInstance model;
	
	public void init() {
		model = ModelLoader.instanciate("projectile");
        for (Material material : model.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.skinColor));
        }
	}
	
	public void update() {
	    
	}
	
	public void render(PerspectiveCamera camera) {
		for (MiningProjectile projectile : MiningModel.state.entityData.projectiles) {
			model.transform.idt();
		    model.transform.translate(projectile.position.x, projectile.position.y, 0);
		    model.transform.rotate(0, 0, 1, projectile.direction.angleDeg() - 90f);
		    model.transform.scale(1f, 1f, 1f);
			ModelRenderer.render(camera, model);
		}
	}

	public void dispose() {
		
	}

}
