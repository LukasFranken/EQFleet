package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputFilter;

public class NumberTextfieldInputFilter implements TextfieldInputFilter {

	private List<Character> allowedCharacters;

	public NumberTextfieldInputFilter() {
		allowedCharacters = new ArrayList<>();
		allowedCharacters.add('0');
		allowedCharacters.add('1');
		allowedCharacters.add('2');
		allowedCharacters.add('3');
		allowedCharacters.add('4');
		allowedCharacters.add('5');
		allowedCharacters.add('6');
		allowedCharacters.add('7');
		allowedCharacters.add('8');
		allowedCharacters.add('9');
	}

	@Override
	public boolean accept(char character) {
		return allowedCharacters.contains(character);
	}

}
