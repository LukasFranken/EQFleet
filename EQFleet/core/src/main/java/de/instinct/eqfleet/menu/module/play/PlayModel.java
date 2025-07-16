package de.instinct.eqfleet.menu.module.play;

import de.instinct.api.matchmaking.dto.InviteResponse;
import de.instinct.api.matchmaking.dto.InvitesStatusResponse;
import de.instinct.api.matchmaking.dto.LobbyStatusResponse;
import de.instinct.api.matchmaking.dto.MatchmakingStatusResponse;
import de.instinct.api.matchmaking.dto.PlayerReward;

public class PlayModel {
	
	public static volatile String lobbyUUID;
	public static volatile LobbyStatusResponse lobbyStatus;
	public static volatile MatchmakingStatusResponse currentMatchmakingStatus;
	public static volatile InvitesStatusResponse inviteStatus;
	public static volatile InviteResponse inviteResponse;
	public static volatile PlayerReward reward;

}
