package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model;

import com.badlogic.gdx.graphics.Color;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BarFragment {
	
	private Color color;
	private float value;

}
