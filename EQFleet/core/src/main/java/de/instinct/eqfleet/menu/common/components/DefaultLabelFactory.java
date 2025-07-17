package de.instinct.eqfleet.menu.common.components;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;

public class DefaultLabelFactory {
	
	public static ElementStack createLabelStack(String tag, String value, float width) {
		ElementStack labelStack = new ElementStack();
		Label tagLabel = new Label(tag);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		tagLabel.setFixedWidth(width);
		labelStack.getElements().add(tagLabel);
		
		Label valueLabel = new Label(value);
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		valueLabel.setFixedWidth(width);
		labelStack.getElements().add(valueLabel);
		return labelStack;
	}

}
