package de.instinct.eqfleet.menu.module.mining;

import java.util.List;
import java.util.Queue;

import de.instinct.api.mining.dto.Maps;
import de.instinct.api.mining.dto.player.MiningPlayerMissionData;
import de.instinct.engine_api.mining.model.MiningPlayerInventoryData;
import de.instinct.engine_api.mining.service.model.MiningMissionOverview;
import de.instinct.eqfleet.menu.module.mining.message.MiningMenuMessage;

public class MiningMenuModel {
	
	public static volatile Queue<MiningMenuMessage> messageQueue;
	
	public static volatile MiningMenuTab currentTab;
	public static volatile boolean requestedMissionData;
	
	public static volatile MiningPlayerInventoryData inventory;
	public static volatile MiningPlayerMissionData playerMissionData;
	public static volatile Maps missions;
	public static volatile List<MiningMissionOverview> missionOverviews;
	
}
