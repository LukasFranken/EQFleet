package de.instinct.eqlibgdxutils.rendering.ui.component.passive.description.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import lombok.Data;

@Data
public class DescriptionLine {

	private String content;
	private Color textColor;
	private boolean bold;
	private List<ColorModifier> modifiers;

	public DescriptionLine() {
		content = "";
		textColor = Color.LIGHT_GRAY;
		modifiers = new ArrayList<>();
	}

}
