package de.instinct.devtools.mapmaker.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.badlogic.gdx.math.Vector2;

import de.instinct.devtools.mapmaker.MapModel;
import de.instinct.devtools.util.EngineUtils;
import de.instinct.engine.util.EngineUtility;
import de.instinct.engine_api.core.model.GameMap;
import de.instinct.engine_api.core.model.PlanetInitialization;

public class MapViewPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final float WORLD_TO_PIXEL_SCALE = 0.4f;
	private final int PANEL_WIDTH = 400;
	private final int PANEL_HEIGHT = 800;
	
	public MapViewPanel() {
		setLayout(null);
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (MapModel.map != null) {
			renderMap(g, MapModel.map);
		}
	}
	
	private void renderMap(Graphics g, GameMap map) {
		for (PlanetInitialization planet : map.getPlanets()) {
			renderPlanetCircle(g, planet);
		}
	}

	private void renderPlanetCircle(Graphics g, PlanetInitialization planet) {
		Color planetColor = EngineUtils.getPlanetColor(planet.getOwnerId(), planet.isAncient());
		g.setColor(planetColor);
		Vector2 screenPosition = convertToScreenPosition(planet.getPosition());
		int radius = (int) (EngineUtility.PLANET_RADIUS * WORLD_TO_PIXEL_SCALE);
		screenPosition.x -= radius;
		screenPosition.y -= radius;
		g.fillOval((int) screenPosition.x, 
		           (int) screenPosition.y, 
		           radius * 2, 
		           radius * 2);
	}
	
	private Vector2 convertToScreenPosition(Vector2 worldPosition) {
		Vector2 screenPosition = new Vector2(worldPosition);
		screenPosition.x *= WORLD_TO_PIXEL_SCALE;
		screenPosition.y *= WORLD_TO_PIXEL_SCALE;
		screenPosition.x += PANEL_WIDTH / 2f;
		screenPosition.y += PANEL_HEIGHT / 2f;
		screenPosition.y = PANEL_HEIGHT - screenPosition.y;
		return screenPosition;
	}

}
