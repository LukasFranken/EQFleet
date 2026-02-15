package de.instinct.eqlibgdxutils.debug.profiler.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Capture {
	
	private long startTimeNS;
	private long durationNS;
	private List<Section> sections;
	
	public Section getSection(String sectionTag) {
		Section currentSection = null;
		for (Section section : sections) {
			if (section.getTag().equals(sectionTag)) {
				currentSection = section;
				break;
			}
		}
		return currentSection;
	}

}
