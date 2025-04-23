package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model;

import com.badlogic.gdx.InputAdapter;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.Textfield;

public class TextfieldInputProcessor extends InputAdapter {

    private Textfield textfield;

    public TextfieldInputProcessor(Textfield textfield) {
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
