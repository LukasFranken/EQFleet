package de.instinct.eqfleet.menu.module.profile.model;

import java.util.List;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UsernameTextField extends Component {
	
	private LimitedInputField textField;
	
	public UsernameTextField(TextfieldActionHandler action) {
		textField = new LimitedInputField();
		textField.setMaxChars(12);
		textField.setInputFilter(new UsernameTexfieldInputFilter());
		textField.setPopupMessage("Enter name");
		textField.setAction(action);
	}

	@Override
	protected void updateComponent() {
		textField.setBounds(getBounds());
	}
	
	@Override
	protected void renderComponent() {
		textField.render();
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		textField.dispose();
	}

	public String getContent() {
		return textField.getContent();
	}

	public List<Character> getForbiddenCharsMobile() {
		return textField.getForbiddenCharsMobile();
	}

	public void setContent(String value) {
		textField.setContent(value);
	}

}
