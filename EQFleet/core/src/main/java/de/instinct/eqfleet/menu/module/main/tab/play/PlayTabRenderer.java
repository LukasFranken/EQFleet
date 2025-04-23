package de.instinct.eqfleet.menu.module.main.tab.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.menu.Menu;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqfleetshared.net.enums.FactionMode;
import de.instinct.eqfleetshared.net.enums.GameMode;
import de.instinct.eqfleetshared.net.enums.VersusMode;
import de.instinct.eqfleetshared.net.message.types.MatchmakingRequest;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class PlayTabRenderer extends Renderer {
	
	private ColorButton startMatchmakingButton;
	private ColorButton startSoloButton;
	
	@Override
	public void init() {
		Border buttonBorder = new Border();
		buttonBorder.setSize(2);
		buttonBorder.setColor(Color.GRAY);
		
		startMatchmakingButton = new ColorButton("Find Match");
		startMatchmakingButton.setBorder(buttonBorder);
		startMatchmakingButton.setColor(Color.BLACK);
		startMatchmakingButton.setFixedHeight(30);
		startMatchmakingButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		startMatchmakingButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		startMatchmakingButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		startMatchmakingButton.setActive(true);
		startMatchmakingButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MatchmakingRequest matchmakingRequest = new MatchmakingRequest();
				matchmakingRequest.versusMode = VersusMode.PVP;
				matchmakingRequest.gameMode = GameMode.KING_OF_THE_HILL;
				matchmakingRequest.factionMode = FactionMode.ONE_VS_ONE;
				Menu.deactivate();
				Game.start(matchmakingRequest);
			}
			
		});
		startMatchmakingButton.setBorder(buttonBorder);

		startSoloButton = new ColorButton("Play Solo");
		startSoloButton.setBorder(buttonBorder);
		startSoloButton.setColor(Color.BLACK);
		startSoloButton.setFixedHeight(30);
		startSoloButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		startSoloButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		startSoloButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		startSoloButton.setActive(true);
		startSoloButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MatchmakingRequest matchmakingRequest = new MatchmakingRequest();
				matchmakingRequest.versusMode = VersusMode.AI;
				matchmakingRequest.gameMode = GameMode.KING_OF_THE_HILL;
				matchmakingRequest.factionMode = FactionMode.ONE_VS_ONE;
				Menu.deactivate();
				Game.start(matchmakingRequest);
			}
			
		});
	}
	@Override
	public void render() {
		float buttonWidth = 120f;
		float buttonHeight = 40f;
		startMatchmakingButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2), Gdx.graphics.getHeight() / 2 + 20, buttonWidth, buttonHeight));
		startMatchmakingButton.setFixedWidth(buttonWidth);
		startMatchmakingButton.setFixedHeight(buttonHeight);
		startMatchmakingButton.render();
		
		startSoloButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2), Gdx.graphics.getHeight() / 2 - 20 - buttonHeight, buttonWidth, buttonHeight));
		startSoloButton.setFixedWidth(buttonWidth);
		startSoloButton.setFixedHeight(buttonHeight);
		startSoloButton.render();
	}
	@Override
	public void dispose() {
		startMatchmakingButton.dispose();
		startSoloButton.dispose();
	}

}
