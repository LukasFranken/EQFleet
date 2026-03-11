package de.instinct.eqfleet.menu.module.profile;

import java.util.Queue;

import de.instinct.api.commander.dto.CommanderData;
import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.api.meta.dto.ProfileData;
import de.instinct.api.meta.dto.ResourceData;
import de.instinct.eqfleet.menu.module.profile.message.ProfileMessage;

public class ProfileModel {
	
	public static volatile Queue<ProfileMessage> messageQueue;
	
	public static volatile ProfileData profile;
	public static volatile CommanderData commanderData;
	public static volatile NameRegisterResponseCode nameRegisterResponseCode;

	public static volatile ResourceData resources;

}
