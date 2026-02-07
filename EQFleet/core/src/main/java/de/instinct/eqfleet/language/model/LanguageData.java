package de.instinct.eqfleet.language.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageData {
	
	private List<Language> availableLanguages;
	private List<Translation> translations;

}
