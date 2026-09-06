package de.instinct.eqlibgdxutils.audio.config.types;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternalAudioConfiguration {
	
	private List<String> availableRadioTracks;
	private List<String> availableNonRadioTracks;

}
