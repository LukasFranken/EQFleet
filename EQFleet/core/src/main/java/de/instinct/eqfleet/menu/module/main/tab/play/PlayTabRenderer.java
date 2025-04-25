package de.instinct.eqfleet.menu.module.main.tab.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.API;
import de.instinct.api.matchmaking.dto.MatchmakingRegistrationRequest;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class PlayTabRenderer extends Renderer {
	
	private ColorButton startMatchmakingButton;
	
	private ColorButton aiButton;
	private ColorButton pvpButton;
	
	private ColorButton soloButton;
	private ColorButton duoButton;
	private ColorButton trioButton;
	
	private VersusMode selectedVersusMode;
	private FactionMode selectedFactionMode;
	
	@Override
	public void init() {
		startMatchmakingButton = getBaseButton("Find Match");
		startMatchmakingButton.setAction(new Action() {
			
			@Override
			public void execute() {
				WebManager.enqueue(
					    () -> API.matchmaking().register(MatchmakingRegistrationRequest.builder()
					    		.gameType(GameType.builder()
					    				.gameMode(GameMode.KING_OF_THE_HILL)
					    				.versusMode(selectedVersusMode)
					    				.factionMode(selectedFactionMode)
					    				.build())
					    		.build()),
					    result -> {
					    	PlayTab.processMatchmakingResponse(result);
					    }
				);
			}
			
		});

		aiButton = getBaseButton("AI");
		aiButton.setAction(new Action() {
			
			@Override
			public void execute() {
				aiButton.setActive(true);
				pvpButton.setActive(false);
				selectedVersusMode = VersusMode.AI;
			}
			
		});
		
		pvpButton = getBaseButton("PVP");
		pvpButton.setAction(new Action() {
			
			@Override
			public void execute() {
				aiButton.setActive(false);
				pvpButton.setActive(true);
				selectedVersusMode = VersusMode.PVP;
			}
			
		});
		
		soloButton = getBaseButton("Solo");
		soloButton.setAction(new Action() {
			
			@Override
			public void execute() {
				soloButton.setActive(true);
				duoButton.setActive(false);
				trioButton.setActive(false);
				selectedFactionMode = FactionMode.ONE_VS_ONE;
			}
			
		});
		
		duoButton = getBaseButton("Duo");
		duoButton.setAction(new Action() {
			
			@Override
			public void execute() {
				soloButton.setActive(false);
				duoButton.setActive(true);
				trioButton.setActive(false);
				selectedFactionMode = FactionMode.TWO_VS_TWO;
			}
			
		});
		
		trioButton = getBaseButton("Trio");
		trioButton.setAction(new Action() {
			
			@Override
			public void execute() {
				soloButton.setActive(false);
				duoButton.setActive(false);
				trioButton.setActive(true);
				selectedFactionMode = FactionMode.THREE_VS_THREE;
			}
			
		});
	}
	
	private ColorButton getBaseButton(String label) {
		Border buttonBorder = new Border();
		buttonBorder.setSize(2);
		buttonBorder.setColor(Color.GRAY);
		ColorButton newColorButton = new ColorButton(label);
		newColorButton.setBorder(buttonBorder);
		newColorButton.setColor(Color.BLACK);
		newColorButton.setFixedHeight(30);
		newColorButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		newColorButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		newColorButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		newColorButton.setActiveColor(new Color(DefaultUIValues.darkestSkinColor));
		return newColorButton;
	}
	
	@Override
	public void render() {
		if (PlayTab.currentMatchmakingStatus == null) {
			renderQueueSelection();
		} else {
			renderQueueStatus();
		}
	}

	private void renderQueueSelection() {
		float buttonWidth = 100f;
		float buttonHeight = 40f;
		
		aiButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - buttonWidth - 10, Gdx.graphics.getHeight() / 2 + 100 - buttonHeight, buttonWidth, buttonHeight));
		aiButton.setFixedWidth(buttonWidth);
		aiButton.setFixedHeight(buttonHeight);
		aiButton.render();
		
		pvpButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) + 10, Gdx.graphics.getHeight() / 2 + 100 - buttonHeight, buttonWidth, buttonHeight));
		pvpButton.setFixedWidth(buttonWidth);
		pvpButton.setFixedHeight(buttonHeight);
		pvpButton.render();
		
		if (selectedVersusMode != null) {
			soloButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2) - 10 - buttonWidth, Gdx.graphics.getHeight() / 2 - buttonHeight, buttonWidth, buttonHeight));
			soloButton.setFixedWidth(buttonWidth);
			soloButton.setFixedHeight(buttonHeight);
			soloButton.render();
			
			duoButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2), Gdx.graphics.getHeight() / 2 - buttonHeight, buttonWidth, buttonHeight));
			duoButton.setFixedWidth(buttonWidth);
			duoButton.setFixedHeight(buttonHeight);
			duoButton.render();
			
			trioButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) + (buttonWidth / 2) + 10, Gdx.graphics.getHeight() / 2 - buttonHeight, buttonWidth, buttonHeight));
			trioButton.setFixedWidth(buttonWidth);
			trioButton.setFixedHeight(buttonHeight);
			trioButton.render();
			
			buttonWidth = 120f;
			startMatchmakingButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2), Gdx.graphics.getHeight() / 2 - 100 - buttonHeight, buttonWidth, buttonHeight));
			startMatchmakingButton.setFixedWidth(buttonWidth);
			startMatchmakingButton.setFixedHeight(buttonHeight);
			if (selectedFactionMode != null) {
				startMatchmakingButton.render();
			}
		}
	}
	
	private void renderQueueStatus() {
		FontUtil.drawLabel(PlayTab.currentMatchmakingStatus.getCode().toString(),
				new Rectangle(0, 50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		FontUtil.drawLabel(PlayTab.currentMatchmakingStatus.getFoundPlayers() + " / " + PlayTab.currentMatchmakingStatus.getRequiredPlayers() + " players found",
				new Rectangle(0, -50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	public void dispose() {
		startMatchmakingButton.dispose();
		aiButton.dispose();
	}

}
