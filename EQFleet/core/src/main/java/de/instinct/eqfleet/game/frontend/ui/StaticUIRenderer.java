package de.instinct.eqfleet.game.frontend.ui;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;

public class StaticUIRenderer {
	
	private GameUILoader uiLoader;
	private List<GameUIElement<?>> elements;
	
	public void init() {
		loadBounds();
		uiLoader = new GameUILoader();
		elements = uiLoader.loadElements();
		initializeElements();
	}
	
	private void loadBounds() {
		GameModel.uiBounds = UIBounds.builder()
				.time(new Rectangle(330, 830, 65, 25))
				.ownCPBar(new Rectangle(47, 23, 330, 27))
				.ownCPBarLabel(new Rectangle(20, 23, 27, 27))
				.teammate1CPBar(new Rectangle(75, (23 + 27 + 3), 135, 20))
				.teammate1CPBarLabel(new Rectangle(50, (23 + 27 + 3), 25, 20))
				.teammate2CPBar(new Rectangle((75 + 155 + 10), (23 + 27 + 3), 135, 20))
				.teammate2CPBarLabel(new Rectangle((50 + 155 + 10), (23 + 27 + 3), 25, 20))
				.enemy1CPBarLabel(new Rectangle((51 + 10), 800, 27, 27))
				.enemy1CPBar(new Rectangle((51 + 27 + 10), 800, 82, 27))
				.enemy2CPBar(new Rectangle((51 + 27 + 10 + 82 + 5), 800, 82, 27))
				.enemy3CPBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5), 800, 82, 27))
				.teamAPBar(new Rectangle(20, 174, 27, 207))
				.teamAPBarLabel(new Rectangle(20, 147, 27, 27))
				.enemyAPBar(new Rectangle(20, 492, 27, 207))
				.enemyAPBarLabel(new Rectangle(20, (492 + 207), 27, 27))
				.build();
	}
	
	private void initializeElements() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.getInitAction() != null) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				gameUIelement.getInitAction().execute();
			}
		}
	}
	
	public void render() {
		updateStaticUI();
		renderStaticUI();
	}
	
	private void updateStaticUI() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				gameUIelement.setCurrentGameState(GameModel.activeGameState);
				if (gameUIelement.getUpdateAction() != null) gameUIelement.getUpdateAction().execute();
			}
		}
	}

	private void renderStaticUI() {
		for (GameUIElement<?> gameUIelement : elements) {
			if (gameUIelement.isVisible()) {
				if (gameUIelement.getElement() != null) {
					gameUIelement.getElement().setBounds(gameUIelement.getBounds());
					gameUIelement.getElement().render();
				}
				if (gameUIelement.getPostRenderAction() != null) gameUIelement.getPostRenderAction().execute();
			}
		}
	}
	
	public void setElementVisible(String tag, boolean visible) {
		for (GameUIElement<?> element : elements) {
			if (element.getTag().contentEquals(tag)) {
				element.setVisible(visible);
			}
		}
	}

}
