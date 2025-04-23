package de.instinct.eqlibgdxutils.debug;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricUpdate {

	private String tag;
	private Object value;

}
