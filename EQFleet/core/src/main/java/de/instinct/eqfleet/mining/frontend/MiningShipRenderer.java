
package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class MiningShipRenderer {
	
	private ModelInstance model;
	
	public void init() {
		model = ModelLoader.instanciate("miningship_0");
		model.materials.get(1).set(ColorAttribute.createDiffuse(SkinManager.darkerSkinColor));
		model.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
	}
	
	public void update() {
	    
	}
	
	public void render(PerspectiveCamera camera) {
		for (MiningPlayerShip ship : MiningModel.state.entityData.playerShips) {
			model.transform.idt();
		    model.transform.translate(ship.position.x, ship.position.y, 0);
		    model.transform.rotate(0, 0, 1, ship.direction.angleDeg());
		    model.transform.scale(1f, 1f, 1f);
			ModelRenderer.render(camera, model);
		}
	}

	public void dispose() {
		
	}

}
