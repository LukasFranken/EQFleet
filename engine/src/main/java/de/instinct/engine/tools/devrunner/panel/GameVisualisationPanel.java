package de.instinct.engine.tools.devrunner.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.tools.core.EngineUtils;
import de.instinct.engine.tools.devrunner.TestEngineManager;
import de.instinct.engine.util.EngineUtility;

public class GameVisualisationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final float WORLD_TO_PIXEL_SCALE = 0.4f;
	private final int PANEL_WIDTH = 400;
	private final int PANEL_HEIGHT = 800;
	
	public GameVisualisationPanel() {
		setLayout(null);
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (TestEngineManager.state != null) {
        	renderUI(g, TestEngineManager.state);
            renderPlanets(g, TestEngineManager.state);
            renderShips(g, TestEngineManager.state);
            renderTurrets(g, TestEngineManager.state);
            renderProjectiles(g, TestEngineManager.state);
        }
    }

	private void renderTurrets(Graphics g, GameState state) {
		for (Turret turret : state.turrets) {
			Vector2 screenPosition = convertToScreenPosition(turret.position);
			g.setColor(Color.GRAY);
			g.drawRect((int)screenPosition.x - 7, (int)screenPosition.y + 18, 30, 3);
			g.setColor(Color.YELLOW);
			g.drawRect((int)screenPosition.x - 6, (int)screenPosition.y + 19, (int)(28 * (turret.hull.currentStrength / turret.data.hull.strength)), 1);
			int shieldCounter = 0;
			for (Shield shield : turret.shields) {
				g.setColor(Color.GRAY);
				g.drawRect((int)screenPosition.x - 7, (int)screenPosition.y + 21 + (shieldCounter * 3), 30, 3);
				g.setColor(Color.BLUE);
				g.drawRect((int)screenPosition.x - 6, (int)screenPosition.y + 22 + (shieldCounter * 3), (int)(28 * (shield.currentStrength / shield.data.strength)), 1);
				shieldCounter++;
			}
			int radius = (int) (EngineUtility.PLANET_RADIUS * WORLD_TO_PIXEL_SCALE + turret.data.weapons.get(0).range * WORLD_TO_PIXEL_SCALE);
			screenPosition = convertToScreenPosition(turret.position);
			screenPosition.x -= radius;
			screenPosition.y -= radius;
			g.drawOval((int) screenPosition.x, 
			           (int) screenPosition.y, 
			           radius * 2, 
			           radius * 2);
		}
		
	}

	private void renderUI(Graphics g, GameState state) {
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("Game Time: " + state.gameTimeMS + "ms", 8, 15);
		g.drawString(state.started ? "running" : (state.gameTimeMS == 0 ? "not started" : "paused"), 8, 28);
		g.drawString("Own CP: " + format(EngineUtility.getPlayer(state.players, 1).currentCommandPoints, 1), 190, 800);
		g.drawString("Enemy1 CP: " + format(EngineUtility.getPlayer(state.players, 4).currentCommandPoints, 1), 190, 15);
		if (state.players.size() > 2) {
			g.drawString("Teammate1 CP: " + format(EngineUtility.getPlayer(state.players, 2).currentCommandPoints, 1), 190, 785);
			g.drawString("Enemy2 CP: " + format(EngineUtility.getPlayer(state.players, 5).currentCommandPoints, 1), 190, 30);
		}
		if (state.players.size() > 4) {
			g.drawString("Teammate2 CP: " + format(EngineUtility.getPlayer(state.players, 3).currentCommandPoints, 1), 190, 770);
			g.drawString("Enemy3 CP: " + format(EngineUtility.getPlayer(state.players, 6).currentCommandPoints, 1), 190, 45);
		}
		g.drawString("Own ATP: " + format(state.teamATPs.get(1), 1), 8, 615);
		g.drawString("Enemy ATP: " + format(state.teamATPs.get(2), 1), 8, 200);
	}

	private void renderPlanets(Graphics g, GameState state) {
		for (Planet planet : state.planets) {
			renderPlanetCircle(g, planet);
			renderPlanetUI(g, planet);
		}
	}

	private void renderPlanetCircle(Graphics g, Planet planet) {
		Color planetColor = EngineUtils.getPlanetColor(planet.ownerId, planet.ancient);
		g.setColor(planetColor);
		Vector2 screenPosition = convertToScreenPosition(planet.position);
		int radius = (int) (EngineUtility.PLANET_RADIUS * WORLD_TO_PIXEL_SCALE);
		screenPosition.x -= radius;
		screenPosition.y -= radius;
		g.fillOval((int) screenPosition.x, 
		           (int) screenPosition.y, 
		           radius * 2, 
		           radius * 2);
	}

	private void renderPlanetUI(Graphics g, Planet planet) {
		Vector2 screenPosition = convertToScreenPosition(planet.position);
		screenPosition.x -= 8;
		screenPosition.y += 5;
		g.setColor(Color.WHITE);
		g.drawString(format(planet.currentResources, 1), (int) screenPosition.x, (int) screenPosition.y);
		g.setColor(Color.GRAY);
		g.drawString(planet.id + "", (int) screenPosition.x - 18, (int) screenPosition.y - 18);
		Color planetColor = EngineUtils.getPlanetColor(planet.ownerId, planet.ancient);
		g.setColor(planetColor);
		
	}
	
	private void renderShips(Graphics g, GameState state) {
		for (Ship ship : state.ships) {
			renderShip(g, ship);
		}
	}
	
	private void renderShip(Graphics g, Ship ship) {
		g.setColor(EngineUtils.getOwnerColor(ship.ownerId));
		Vector2 screenPosition = convertToScreenPosition(ship.position);
		g.drawRect((int)screenPosition.x - 1, (int)screenPosition.y - 1, 3, 3);
		if (ship.weapons != null && !ship.weapons.isEmpty()) {
			int radius = (int) (ship.weapons.get(0).data.range * WORLD_TO_PIXEL_SCALE);
			g.drawOval((int) screenPosition.x + 1 - radius, 
			           (int) screenPosition.y + 1 - radius, 
			           radius * 2, 
			           radius * 2);
		}
	}

	private void renderProjectiles(Graphics g, GameState state) {
		for (Projectile projectile : state.projectiles) {
			renderProjectile(g, projectile);
		}
	}
	
	private void renderProjectile(Graphics g, Projectile projectile) {
		Vector2 screenPosition = convertToScreenPosition(projectile.position);
		switch (projectile.weaponType) {
			case LASER:
				g.setColor(Color.RED);
				break;
			case PROJECTILE:
				g.setColor(Color.GRAY);
				break;
			case MISSILE:
				g.setColor(Color.YELLOW);
				break;
			default:
				g.setColor(Color.WHITE);
				break;
		}
		g.drawRect((int)screenPosition.x, (int)screenPosition.y, 1, 1);
	}

	private String format(double value, int decimals) {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		decimalFormat.setMaximumFractionDigits(decimals);
		return decimalFormat.format(value).replace(",", ".");
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
