package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputFilter;

public class UsernameTexfieldInputFilter implements TextfieldInputFilter {

	@Override
	public boolean accept(char character) {
		if (character >= 65 && character <= 90 || character >= 97 && character <= 122) {
			return true;
		}
		return false;
	}

}
