package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputFilter;

public class DefaultTextfieldInputFilter implements TextfieldInputFilter {

	private List<Character> forbiddenCharacters;

	public DefaultTextfieldInputFilter() {
		forbiddenCharacters = new ArrayList<>();
		forbiddenCharacters.add('#');
		forbiddenCharacters.add('{');
		forbiddenCharacters.add('}');
		forbiddenCharacters.add((char) 34);
		forbiddenCharacters.add((char) 39);
	}

	@Override
	public boolean accept(char character) {
		if (character >= 32 && character < 127) {
			return isAllowed(character);
		}
		return false;
	}

	private boolean isAllowed(char character) {
		return !forbiddenCharacters.contains(character);
	}

}
