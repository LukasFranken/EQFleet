package de.instinct.eqlibgdxutils.audio.config;

import de.instinct.eqlibgdxutils.audio.config.types.InternalAudioConfiguration;
import de.instinct.eqlibgdxutils.audio.config.types.UserAudioConfiguration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioConfiguration {
	
	private InternalAudioConfiguration internalAudioConfiguration;
	private UserAudioConfiguration userAudioConfiguration;

}
