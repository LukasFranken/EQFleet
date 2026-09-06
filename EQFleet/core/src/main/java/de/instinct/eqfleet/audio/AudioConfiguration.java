package de.instinct.eqfleet.audio;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioConfiguration {
	
	private List<String> availableRadioTracks;
	private List<String> availableNonRadioTracks;

}
