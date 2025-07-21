package de.instinct.eqlibgdxutils.rendering.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

public class ModelRenderer {

	private static ModelBatch modelBatch;
    private static Environment environment;

	public static void init() {
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));
        modelBatch = new ModelBatch();
	}

	public static void render(PerspectiveCamera camera, ModelInstance modelInstance) {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();
	}
	
	public static void render(PerspectiveCamera camera, ModelInstance model, float alpha) {
		if (model.materials != null) {
	        for (Material material : model.materials) {
	            ColorAttribute diffuse = (ColorAttribute) material.get(ColorAttribute.Diffuse);
	            if (diffuse != null) {
	                diffuse.color.a = alpha;
	            }
	            
	            ColorAttribute ambient = (ColorAttribute) material.get(ColorAttribute.Ambient);
	            if (ambient != null) {
	                ambient.color.a = alpha;
	            }
	            
	            if (alpha < 1.0f) {
	                material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
	            } else if (material.has(BlendingAttribute.Type)) {
	                material.remove(BlendingAttribute.Type);
	            }
	        }
	    }
		render(camera, model);
	}

	public static void dispose() {
		modelBatch.dispose();
	}

}
