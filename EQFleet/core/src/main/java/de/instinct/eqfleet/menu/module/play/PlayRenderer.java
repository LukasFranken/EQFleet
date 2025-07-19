package de.instinct.eqfleet.menu.module.play;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.matchmaking.dto.InviteResponse;
import de.instinct.api.matchmaking.dto.LobbyStatusCode;
import de.instinct.api.matchmaking.model.FactionMode;
import de.instinct.api.matchmaking.model.GameMode;
import de.instinct.api.matchmaking.model.GameType;
import de.instinct.api.matchmaking.model.Invite;
import de.instinct.api.matchmaking.model.VersusMode;
import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.ship.Shipyard;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ActionList;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ActionListElement;
import de.instinct.eqlibgdxutils.rendering.ui.module.list.ListActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class PlayRenderer extends BaseModuleRenderer {

	private ColorButton startCustomButton;
	private ColorButton createLobbyButton;
	private ColorButton leaveLobbyButton;
	private ColorButton cancelMatchmakingButton;
	private ActionList invites;
	
	private ColorButton aiButton;
	private ColorButton pvpButton;
	
	private ColorButton soloButton;
	private ColorButton duoButton;
	private ColorButton trioButton;
	
	private ColorButton setTypeButton;
	
	private ColorButton inviteButton;
	private LimitedInputField usernameTextField;
	private ColorButton startMatchmakingButton;
	
	private VersusMode selectedVersusMode;
	private FactionMode selectedFactionMode;
	
	private String inviteMessage;
	private float inviteMessageElapsed;
	private float inviteMessageDuration = 3f;
	
	public PlayRenderer() {
		usernameTextField = new LimitedInputField();
		usernameTextField.setMaxChars(12);
		usernameTextField.setInputFilter(new UsernameTexfieldInputFilter());
		usernameTextField.setPopupMessage("Enter name");
		usernameTextField.setAction(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				if (usernameTextField.getForbiddenCharsMobile() == null) {
					Play.invite(usernameTextField.getContent());
					usernameTextField.setContent("");
				}
			}
			
		});
		
		inviteButton = getBaseButton("Invite");
		inviteButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Play.invite(usernameTextField.getContent());
				usernameTextField.setContent("");
			}
			
		});
		
		createLobbyButton = getBaseButton("Create Lobby");
		createLobbyButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (Shipyard.hasActiveShip()) {
					Play.createLobby();
				} else {
					PopupRenderer.createMessageDialog("Unable to start", "No active ship\nin Shipyard");
				}
			}
			
		});
		
		startCustomButton = getBaseButton("Start Custom");
		startCustomButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (Shipyard.hasActiveShip()) {
					Game.startCustom();
					Menu.close();
				} else {
					PopupRenderer.createMessageDialog("Unable to start", "No active ship\nin Shipyard");
				}
			}
			
		});
		
		leaveLobbyButton = getBaseButton("Leave Lobby");
		leaveLobbyButton.setFixedWidth(120f);
		leaveLobbyButton.setFixedHeight(30f);
		leaveLobbyButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Play.leaveLobby();
				selectedVersusMode = null;
				selectedFactionMode = null;
			}
			
		});
		
		cancelMatchmakingButton = getBaseButton("Cancel Matchmaking");
		cancelMatchmakingButton.setFixedWidth(200f);
		cancelMatchmakingButton.setFixedHeight(30f);
		cancelMatchmakingButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Play.stopMatching();
			}
			
		});
		
		setTypeButton = getBaseButton("Set Mode");
		setTypeButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Play.setType(GameType.builder()
						.map("solar")
						.threatLevel(1)
						.gameMode(GameMode.KING_OF_THE_HILL)
						.versusMode(selectedVersusMode)
						.factionMode(selectedFactionMode)
						.build());
				selectedVersusMode = null;
				selectedFactionMode = null;
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
		
		invites = new ActionList();
		invites.setConfirmHandler(new ListActionHandler() {
			
			@Override
			public void triggered(ActionListElement element) {
				if (Shipyard.hasActiveShip()) {
					Play.accept(element.getValue());
				} else {
					PopupRenderer.createMessageDialog("Unable to start", "No active ship\nin Shipyard");
				}
			}
			
		});
		
		invites.setDenyHandler(new ListActionHandler() {
			
			@Override
			public void triggered(ActionListElement element) {
				Play.decline(element.getValue());
			}
			
		});
		
		startMatchmakingButton = getBaseButton("Queue up");
		startMatchmakingButton.setFixedWidth(120f);
		startMatchmakingButton.setFixedHeight(30f);
		startMatchmakingButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Play.startMatchmaking();
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
		newColorButton.setLabelColor(new Color(SkinManager.skinColor));
		newColorButton.setHoverColor(new Color(SkinManager.darkerSkinColor));
		newColorButton.setDownColor(new Color(SkinManager.lighterSkinColor));
		newColorButton.setActiveColor(new Color(SkinManager.darkestSkinColor));
		return newColorButton;
	}
	
	@Override
	public void render() {
		if (PlayModel.lobbyUUID == null || PlayModel.lobbyUUID.contentEquals("")) {
			renderPreLobbyView();
		} else {
			if (PlayModel.lobbyStatus != null) {
				if (PlayModel.lobbyStatus.getType() == null) {
					renderModeSelection();
					leaveLobbyButton.setPosition(40, 100);
					leaveLobbyButton.render();
				} else {
					if (PlayModel.lobbyStatus.getCode() == LobbyStatusCode.MATCHING && PlayModel.currentMatchmakingStatus != null) {
						renderQueueStatus();
						cancelMatchmakingButton.setPosition((Gdx.graphics.getWidth() - cancelMatchmakingButton.getFixedWidth()) / 2, 100);
						cancelMatchmakingButton.render();
					} else {
						renderLobbyOverview();
						leaveLobbyButton.setPosition(40, 100);
						leaveLobbyButton.render();
					}
				}
			}
		}
	}

	private void renderPreLobbyView() {
		createLobbyButton.setPosition(50, Gdx.graphics.getHeight() - 400);
		createLobbyButton.setFixedWidth(Gdx.graphics.getWidth() - 100);
		createLobbyButton.setFixedHeight(40);
		createLobbyButton.render();
		
		startCustomButton.setPosition(50, Gdx.graphics.getHeight() - 300);
		startCustomButton.setFixedWidth(Gdx.graphics.getWidth() - 100);
		startCustomButton.setFixedHeight(40);
		startCustomButton.render();
		
		invites.setElements(new ArrayList<>());
		if (PlayModel.inviteStatus != null && PlayModel.inviteStatus.getInvites().size() > 0) {
			for (Invite invite : PlayModel.inviteStatus.getInvites()) {
				invites.getElements().add(ActionListElement.builder()
						.value(invite.getLobbyUUID())
						.label(invite.getFromUsername())
						.build());
			}
			invites.setBounds(new Rectangle(50, 100, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 500));
			invites.update();
			invites.render();
		} else {
			Label noPendingInvitesLabel = new Label("No pending invites");
			noPendingInvitesLabel.setBounds(new Rectangle(0, Gdx.graphics.getHeight() - 500, Gdx.graphics.getWidth(), 100));
			noPendingInvitesLabel.render();
		}
	}

	private void renderModeSelection() {
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
			setTypeButton.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - (buttonWidth / 2), Gdx.graphics.getHeight() / 2 - 100 - buttonHeight, buttonWidth, buttonHeight));
			setTypeButton.setFixedWidth(buttonWidth);
			setTypeButton.setFixedHeight(buttonHeight);
			if (selectedFactionMode != null) {
				setTypeButton.render();
			}
		}
	}
	
	private void renderLobbyOverview() {
		GameType selectedGameType = PlayModel.lobbyStatus.getType();
		Label gameTypeLabel = new Label(selectedGameType.getGameMode() + " - " + selectedGameType.getVersusMode() + " - " + selectedGameType.getFactionMode());
		gameTypeLabel.setBounds(new Rectangle(0, 550, Gdx.graphics.getWidth(), 30));
		gameTypeLabel.render();
		
		int i = 0;
		for (String userName : PlayModel.lobbyStatus.getUserNames()) {
			Label userNameLabel = new Label(userName == null ? "???" : userName);
			userNameLabel.setBounds(new Rectangle(0, 500 - i, Gdx.graphics.getWidth(), 30));
			userNameLabel.render();
			i += 30;
		}
		if (PlayModel.lobbyStatus.getUserNames().size() < selectedGameType.getFactionMode().teamPlayerCount) {
			usernameTextField.setBounds(new Rectangle((Gdx.graphics.getWidth() / 2) - 88, 340, 176, 40));
			usernameTextField.render();
			
			inviteButton.setFixedWidth(100f);
			inviteButton.setFixedHeight(30f);
			inviteButton.setPosition((Gdx.graphics.getWidth() / 2) - (inviteButton.getFixedWidth() / 2), 300);
			inviteButton.render();
			
			if (PlayModel.inviteResponse != null) {
				if (PlayModel.inviteResponse == InviteResponse.ALREADY_INVITED) inviteMessage = "Already invited";
				if (PlayModel.inviteResponse == InviteResponse.ERROR) inviteMessage = "Server error";
				if (PlayModel.inviteResponse == InviteResponse.USERNAME_DOESNT_EXIST) inviteMessage = "Username doesn't exist";
				if (PlayModel.inviteResponse == InviteResponse.INVITED) inviteMessage = "Invitation sent";
				PlayModel.inviteResponse = null;
			}
			if (inviteMessage != null) {
				inviteMessageElapsed += Gdx.graphics.getDeltaTime();
				Label inviteMessageLabel = new Label(inviteMessage);
				inviteMessageLabel.setBounds(new Rectangle(0, 270, Gdx.graphics.getWidth(), 30));
				inviteMessageLabel.render();
				if (inviteMessageElapsed >= inviteMessageDuration) {
					inviteMessage = null;
					inviteMessageElapsed = 0f;
				}
			}
		}
		
		startMatchmakingButton.setPosition(Gdx.graphics.getWidth() - startMatchmakingButton.getFixedWidth() - 40, 100);
		startMatchmakingButton.render();
	}
	
	private void renderQueueStatus() {
		Label statusLabel = new Label(PlayModel.currentMatchmakingStatus.getCode().toString());
		statusLabel.setBounds(new Rectangle(0, 50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		statusLabel.render();
		
		Label playerLabel = new Label(PlayModel.currentMatchmakingStatus.getFoundPlayers() + " / " + PlayModel.currentMatchmakingStatus.getRequiredPlayers() + " players found");
		playerLabel.setBounds(new Rectangle(0, -50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		playerLabel.render();
	}

	@Override
	public void dispose() {
		setTypeButton.dispose();
		aiButton.dispose();
	}

	@Override
	public void reload() {
		
	}

}
