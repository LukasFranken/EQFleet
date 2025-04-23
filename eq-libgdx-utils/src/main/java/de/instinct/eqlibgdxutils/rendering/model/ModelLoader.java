package de.instinct.eqlibgdxutils.rendering.model;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class ModelLoader {

	private static Map<String, SceneAsset> sceneAssets = new HashMap<>();

	public static ModelInstance instanciate(String name) {
		SceneAsset currentSceneAsset = null;
		if (sceneAssets.containsKey(name)) {
			currentSceneAsset = sceneAssets.get(name);
		} else {
			currentSceneAsset = new GLBLoader().load(Gdx.files.internal("model/" + name + ".glb"));
			sceneAssets.put(name, currentSceneAsset);
		}
        ModelInstance planetInstance = new ModelInstance(currentSceneAsset.scene.model);
        planetInstance.transform.translate(new Vector3(0, 0, 0));
        return planetInstance;
	}

}
