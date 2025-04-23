package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.EnableConditionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputProcessor;
import lombok.Data;

@Data
public class Textfield {
	
	private Rectangle bounds;
	private String content;
	private String unfocusedContent;
	private String disabledContent;
	private boolean focused;
	private TextfieldActionHandler action;
	private int maxChars;
	private TextfieldInputProcessor inputProcessor;
	private TextfieldInputFilter inputFilter;
	private boolean enabled;
	private EnableConditionHandler enableConditionHandler;
	private boolean renderMaxCharsLabel;

}
