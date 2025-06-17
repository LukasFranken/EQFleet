package de.instinct.eqfleet.game.frontend.ships;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;

public class ShipRenderer {
	
	private Map<Ship, ModelInstance> shipModels;
	private ShapeRenderer shapeRenderer;
	
	public ShipRenderer() {
		shipModels = new HashMap<>();
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(GameState state, PerspectiveCamera camera) {
		for (Ship ship : state.ships) {
    		ModelInstance shipModel = shipModels.get(ship);
            if (shipModel == null) {
            	shipModel = ModelLoader.instanciate("ship");
            	shipModel.transform.scale(15f, 15f, 15f);
                shipModels.put(ship, shipModel);
            }

            Planet to = EngineUtility.getPlanet(state.planets, ship.targetPlanetId);
            float dx = to.position.x - ship.position.x;
            float dy = to.position.y - ship.position.y;
            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));

            shipModel.transform.idt();
            shipModel.transform.translate(new Vector3(ship.position.x, ship.position.y, 0f));
            shipModel.transform.rotate(Vector3.Z, angleDeg - 90f);
            shipModel.transform.scale(15f, 15f, 15f);
            renderShipRoute(ship, to, camera);
            Color color = GameConfig.getPlayerColor(ship.ownerId);
            for (Material material : shipModel.materials) {
                material.set(ColorAttribute.createDiffuse(color));
            }

            ModelRenderer.render(camera, shipModel);
    	}
	}
	
	private void renderShipRoute(Ship ship, Planet target, PerspectiveCamera camera) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.setProjectionMatrix(camera.combined);
		float baseLineWidth = 2f;
		float dynamicLineWidth = baseLineWidth * (Gdx.graphics.getDensity() > 1 ? Gdx.graphics.getDensity() : 1f);
		Gdx.gl.glLineWidth(dynamicLineWidth);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		Color faded = new Color(GameConfig.getPlayerColor(ship.ownerId));
		faded.a = 0.25f;
		shapeRenderer.setColor(faded);
		shapeRenderer.line(ship.position.x, ship.position.y, target.position.x, target.position.y);
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1f);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

}
