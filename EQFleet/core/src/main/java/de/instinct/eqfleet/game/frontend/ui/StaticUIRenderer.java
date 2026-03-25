package de.instinct.eqfleet.game.frontend.ui;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.ui.model.GameUIElement;
import de.instinct.eqfleet.game.frontend.ui.model.UIBounds;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;

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
				.ownResBar(new Rectangle(47, 33, 330, 25))
				.teammate1ResBar(new Rectangle(48, (33 + 25 + 2), 120, 10))
				.teammate2ResBar(new Rectangle(48, (45 + 25 + 2), 120, 10))
				.enemy1ResBarLabel(new Rectangle((51 + 10), 800, 27, 27))
				.enemy1ResBar(new Rectangle((51 + 27 + 10), 800, 82, 27))
				.enemy2ResBar(new Rectangle((51 + 27 + 10 + 82 + 5), 800, 82, 27))
				.enemy3ResBar(new Rectangle((51 + 27 + 10 + 82 + 5 + 82 + 5), 800, 82, 27))
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
		Profiler.startFrame("GAME_STATIC_UI_RENDERER");
		updateStaticUI();
		Profiler.checkpoint("GAME_STATIC_UI_RENDERER", "update");
		renderStaticUI();
		Profiler.checkpoint("GAME_STATIC_UI_RENDERER", "render");
		Profiler.endFrame("GAME_STATIC_UI_RENDERER");
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
