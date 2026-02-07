package de.instinct.eqfleet.language.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation {
	
	private String key;
	private Map<String, String> translations;

}
