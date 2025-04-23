package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model;

import com.badlogic.gdx.InputAdapter;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;

public class InputFieldProcessor extends InputAdapter {

	private LimitedInputField textfield;

    public InputFieldProcessor(LimitedInputField textfield) {
        this.textfield = textfield;
    }

    @Override
    public boolean keyTyped(char character) {
        if (textfield.getContent().length() < textfield.getMaxChars() && textfield.getInputFilter().accept(character)) {
        	textfield.setContent(textfield.getContent() + character);
        }
        return true;
    }

}
