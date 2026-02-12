package de.instinct.eqfleet.game.frontend.planet;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;

public class PlanetRenderer {

	private Map<Integer, ModelInstance> planetModels;
	private float planetRotationAngle;
	
	public PlanetRenderer() {
		planetModels = new HashMap<>();
		planetRotationAngle = 0f;
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		if (planetModels.isEmpty() && state != null) {
		    for (Planet planet : state.entityData.planets) {
		        ModelInstance planetInstance = ModelLoader.instanciate("planet");
		        planetInstance.transform.idt();
		        planetInstance.transform.translate(planet.position.x, planet.position.y, 0f);

		     	Vector3 planetPos = new Vector3(planet.position.x, planet.position.y, 0f);
		     	Vector3 toCamera = new Vector3(camera.position).sub(planetPos).nor();
		     	float angle = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.y));

		     	planetInstance.transform.rotate(Vector3.X, angle + 90);
		     	planetInstance.transform.scale(40f, 40f, 40f);

		        planetModels.put(planet.id, planetInstance);
		    }
		}
		planetRotationAngle += Gdx.graphics.getDeltaTime() * 10f;
		for (Planet planet : state.entityData.planets) {
		    ModelInstance instance = planetModels.get(planet.id);
		    if (instance != null) {
		    	
		    	instance.materials.get(1).set(ColorAttribute.createDiffuse(Color.BLACK));
		    	instance.materials.get(0).set(ColorAttribute.createDiffuse(planet.ancient && planet.ownerId == 0 ? GameConfig.ancientColor : GameConfig.getPlayerColor(planet.ownerId)));
		    	
		    	instance.transform.idt();
			    instance.transform.translate(planet.position.x, planet.position.y, 0f);

			    Vector3 planetPos = new Vector3(planet.position.x, planet.position.y, 0f);
			    Vector3 toCamera = new Vector3(camera.position).sub(planetPos).nor();
			    float angle = (float) Math.toDegrees(Math.atan2(toCamera.z, toCamera.y));
			    instance.transform.rotate(Vector3.X, angle + 90);
			    instance.transform.rotate(Vector3.Y, planetRotationAngle);
			    instance.transform.scale(50f, 50f, 50f);

			    ModelRenderer.render(camera, instance);
		    }
		}
	}

}
