package de.instinct.engine.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JPanel;

import com.badlogic.gdx.math.Vector2;

import de.instinct.engine.combat.Combat;
import de.instinct.engine.combat.Projectile;
import de.instinct.engine.combat.Ship;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.util.EngineUtility;

public class GameVisualisationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final float WORLD_TO_PIXEL_SCALE = 0.4f;
	private final int PANEL_WIDTH = 400;
	private final int PANEL_HEIGHT = 800;
	private final Color NEUTRAL_COLOR = Color.GRAY;
	private final Color ANCIENT_COLOR = Color.ORANGE;
	private final Color PLAYER_COLOR = Color.BLUE;
	private final Color PLAYER2_COLOR = Color.PINK;
	private final Color PLAYER3_COLOR = Color.MAGENTA;
	private final Color ENEMY_COLOR = Color.RED;
	
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
            renderShips(g, TestEngineManager.state.activeCombats);
            renderProjectiles(g, TestEngineManager.state.activeCombats);
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
		Color planetColor = planet.ancient ? ANCIENT_COLOR : getOwnerColor(planet.ownerId);
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
	
	private Color getOwnerColor(int ownerId) {
		if (ownerId == 0) return NEUTRAL_COLOR;
		if (ownerId == 1) return PLAYER_COLOR;
		if (ownerId == 2) return PLAYER2_COLOR;
		if (ownerId == 3) return PLAYER3_COLOR;
		return ENEMY_COLOR;
	}

	private void renderPlanetUI(Graphics g, Planet planet) {
		Vector2 screenPosition = convertToScreenPosition(planet.position);
		screenPosition.x -= 8;
		screenPosition.y += 5;
		g.setColor(Color.WHITE);
		g.drawString(format(planet.currentResources, 1), (int) screenPosition.x, (int) screenPosition.y);
		g.setColor(Color.GRAY);
		g.drawString(planet.id + "", (int) screenPosition.x - 18, (int) screenPosition.y - 18);
		if (planet.defense != null) {
			g.setColor(Color.GRAY);
			g.drawRect((int)screenPosition.x - 7, (int)screenPosition.y + 18, 30, 3);
			g.setColor(Color.BLUE);
			g.drawRect((int)screenPosition.x - 6, (int)screenPosition.y + 19, (int)(28 * (planet.currentShield / planet.defense.shield)), 1);
			g.setColor(Color.GRAY);
			g.drawRect((int)screenPosition.x - 7, (int)screenPosition.y + 21, 30, 3);
			g.setColor(Color.YELLOW);
			g.drawRect((int)screenPosition.x - 6, (int)screenPosition.y + 22, (int)(28 * (planet.currentArmor / planet.defense.armor)), 1);
		}
	}
	
	private void renderShips(Graphics g, List<Combat> activeCombats) {
		for (Combat combat : activeCombats) {
			for (Ship ship : combat.ships) {
				renderShip(g, ship);
			}
		}
	}
	
	private void renderShip(Graphics g, Ship ship) {
		g.setColor(getOwnerColor(ship.ownerId));
		Vector2 screenPosition = convertToScreenPosition(ship.position);
		g.drawRect((int)screenPosition.x - 1, (int)screenPosition.y - 1, 3, 3);
	}

	private void renderProjectiles(Graphics g, List<Combat> activeCombats) {
		for (Combat combat : activeCombats) {
			for (Projectile projectile : combat.projectiles) {
				renderProjectile(g, projectile);
			}
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
