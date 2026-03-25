package de.instinct.eqfleet.menu.module.starmap;

import java.util.Queue;

import de.instinct.api.starmap.dto.PlayerStarmapData;
import de.instinct.api.starmap.dto.SectorData;
import de.instinct.eqfleet.menu.module.starmap.message.StarmapMessage;

public class StarmapModel {
	
	public static volatile Queue<StarmapMessage> messageQueue;
	
	public static volatile boolean dataUpdated;
	public static volatile PlayerStarmapData starmapData;
	public static volatile SectorData sectorData;
	public static volatile int selectedGalaxyId;
	public static volatile int selectedStarsystemId;

}
