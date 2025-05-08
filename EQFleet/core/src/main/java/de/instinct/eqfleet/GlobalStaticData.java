package de.instinct.eqfleet;

import de.instinct.api.core.config.APIConfiguration;
import de.instinct.api.meta.dto.ProfileData;

public class GlobalStaticData {
	
	public static ApplicationMode mode = ApplicationMode.DEV;
	public static ProfileData profile;
	public static APIConfiguration configuration = APIConfiguration.CLIENT;

}
