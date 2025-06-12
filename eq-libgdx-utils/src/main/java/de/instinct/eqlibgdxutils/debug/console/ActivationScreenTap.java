package de.instinct.eqlibgdxutils.debug.console;

import com.badlogic.gdx.math.Rectangle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivationScreenTap {
	
	private Rectangle region;
	private boolean activated;

}
