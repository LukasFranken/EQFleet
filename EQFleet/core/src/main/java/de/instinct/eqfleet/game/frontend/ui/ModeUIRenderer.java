package de.instinct.eqfleet.game.frontend.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.engine.model.GameState;
import de.instinct.engine.model.Player;
import de.instinct.engine.util.EngineUtility;
import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.game.frontend.InteractionMode;
import de.instinct.eqfleet.game.frontend.ui.modes.ModeRenderer;
import de.instinct.eqfleet.game.frontend.ui.modes.UnitModeRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class ModeUIRenderer {
	
	private ColorButton unitControlButton;
    private ColorButton constructionButton;
    private ColorButton qLinkButton;
    
    private Map<InteractionMode, ModeRenderer> modeRenderers;
	
	public ModeUIRenderer() {
		modeRenderers = new HashMap<>();
		modeRenderers.put(InteractionMode.UNIT_CONTROL, new UnitModeRenderer());
	}
	
	public void init() {
		unitControlButton = DefaultButtonFactory.colorButton("U", () -> {
        	if (GameModel.mode != InteractionMode.UNIT_CONTROL) {
        		GameModel.mode = InteractionMode.UNIT_CONTROL;
        		AudioManager.playVoice("game", "units");
        	}
		});
        unitControlButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 100, 40, 40));
        
        constructionButton = DefaultButtonFactory.colorButton("C", () -> {
			if (GameModel.mode != InteractionMode.CONSTRUCTION) {
        		GameModel.mode = InteractionMode.CONSTRUCTION;
        		AudioManager.playVoice("game", "construction");
        	}
		});
        constructionButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 160, 40, 40));
        
        qLinkButton = DefaultButtonFactory.colorButton("Q", () -> {
			if (GameModel.mode != InteractionMode.Q_LINK) {
        		GameModel.mode = InteractionMode.Q_LINK;
        		AudioManager.playVoice("game", "qlink");
        	}
		});
        qLinkButton.setBounds(new Rectangle(GraphicsUtil.screenBounds().width - 60, 220, 40, 40));
	}

	public void render(PerspectiveCamera camera) {
		if (GameModel.activeGameState.teamPause == 0 && GameModel.activeGameState.resumeCountdownMS <= 0) {
			renderModeButtons(GameModel.activeGameState);
			modeRenderers.get(GameModel.mode).render(camera, GameModel.activeGameState);
		}
	}
	
	private void renderModeButtons(GameState state) {
		Player self = EngineUtility.getPlayer(state.players, GameModel.playerId);
		if (!self.turrets.isEmpty()) {
			if (GameModel.mode == InteractionMode.UNIT_CONTROL) {
				unitControlButton.getBorder().setColor(Color.GREEN);
			} else {
				unitControlButton.getBorder().setColor(SkinManager.skinColor);
			}
			//unitControlButton.render();
			
			if (GameModel.mode == InteractionMode.CONSTRUCTION) {
				constructionButton.getBorder().setColor(Color.GREEN);
			} else {
				constructionButton.getBorder().setColor(SkinManager.skinColor);
			}
			//constructionButton.render();
			
			if (GameModel.mode == InteractionMode.Q_LINK) {
				qLinkButton.getBorder().setColor(Color.GREEN);
			} else {
				qLinkButton.getBorder().setColor(SkinManager.skinColor);
			}
			//qLinkButton.render();
		}
	}

}
