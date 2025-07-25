package de.instinct.eqfleet.menu.common.components;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.module.inventory.Inventory;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class DefaultLabelFactory {
	
	public static ElementStack createLabelStack(String tag, String value, float width) {
		return createLabelStack(tag, value, SkinManager.skinColor, width);
	}

	public static ElementStack createLabelStack(String tag, String value, Color color, float width) {
		ElementStack labelStack = new ElementStack();
		Label tagLabel = new Label(tag);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		tagLabel.setFixedWidth(width);
		tagLabel.setColor(color);
		labelStack.getElements().add(tagLabel);
		
		Label valueLabel = new Label(value);
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		valueLabel.setFixedWidth(width);
		valueLabel.setColor(color);
		labelStack.getElements().add(valueLabel);
		return labelStack;
	}

	public static ElementStack createCostStack(ResourceAmount cost, float width) {
		String valueString = StringUtils.formatBigNumber(Math.abs(cost.getAmount()));
		Color costColor = Math.abs(cost.getAmount()) > Inventory.getResource(cost.getType()) ? Color.RED : Color.GREEN;
		
		ElementStack labelStack = new ElementStack();
		Label tagLabel = new Label(cost.getType().toString());
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		tagLabel.setFixedWidth(width);
		labelStack.getElements().add(tagLabel);
		
		Label valueLabel = new Label(valueString);
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		valueLabel.setFixedWidth(width);
		valueLabel.setColor(costColor);
		labelStack.getElements().add(valueLabel);
		return labelStack;
	}
	
}
