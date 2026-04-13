
package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.instinct.engine.mining.entity.ship.PlayerShip;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class MiningShipRenderer {
	
	private ModelInstance model;
	
	public void init() {
		model = ModelLoader.instanciate("ship");
        for (Material material : model.materials) {
            material.set(ColorAttribute.createDiffuse(SkinManager.skinColor));
        }
	}
	
	public void update() {
	    
	}
	
	public void render(PerspectiveCamera camera) {
		for (PlayerShip ship : MiningModel.state.playerShips) {
			model.transform.idt();
		    model.transform.translate(ship.position.x, ship.position.y, 0);
		    model.transform.rotate(0, 0, 1, ship.direction.angleDeg() - 90f);
		    model.transform.scale(15f, 15f, 15f);
			ModelRenderer.render(camera, model);
		}
	}

	public void dispose() {
		
	}

}
