package de.instinct.eqfleet.menu.common.components.label;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.eqfleet.menu.module.profile.ProfileModuleAPI;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class DefaultLabelFactory {
	
	public static ElementStack createLabelStack(String tag, String value, Color colorTag, Color colorValue) {
		ElementStack labelStack = new ElementStack();
		Label tagLabel = new Label(tag);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		tagLabel.setColor(colorTag);
		labelStack.getElements().add(tagLabel);
		
		Label valueLabel = new Label(value);
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		valueLabel.setColor(colorValue);
		labelStack.getElements().add(valueLabel);
		labelStack.setFixedHeight(12f);
		return labelStack;
	}
	
	public static ElementStack createLabelStack(LabelStackConfiguration config) {
		ElementStack labelStack = new ElementStack();
		Label tagLabel = new Label(config.getTag());
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		if (config.getColorTag() != null) tagLabel.setColor(config.getColorTag());
		if (config.getType() != null) tagLabel.setType(config.getType());
		labelStack.getElements().add(tagLabel);
		
		Label valueLabel = new Label(config.getValue());
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		if (config.getColorTag() != null) valueLabel.setColor(config.getColorTag());
		if (config.getType() != null) valueLabel.setType(config.getType());
		labelStack.getElements().add(valueLabel);
		labelStack.setFixedWidth(config.getWidth());
		labelStack.setFixedHeight(12f);
		return labelStack;
	}
	
	public static ElementStack createLabelStack(String tag, String value) {
		return createLabelStack(tag, value, SkinManager.skinColor, SkinManager.skinColor);
	}
	
	public static ElementStack createLabelStack(String tag, String value, float width) {
		ElementStack labelStack = createLabelStack(tag, value);
		labelStack.setFixedWidth(width);
		return labelStack;
	}

	public static ElementStack createCostStack(ResourceAmount cost, float width) {
		ElementStack labelStack = createCostStack(cost);
		labelStack.setFixedWidth(width);
		return labelStack;
	}
	
	public static ElementStack createCostStack(ResourceAmount cost) {
		String valueString = (ProfileModuleAPI.canAfford(cost) ? "" : ProfileModuleAPI.getResource(cost.getType()) + "/") + StringUtils.formatBigNumber(Math.abs(cost.getAmount()));
		Color costColor = ProfileModuleAPI.canAfford(cost) ? Color.GREEN : Color.RED;
		return createLabelStack(cost.getType().toString(), valueString, ProfileModuleAPI.getColorForResource(cost.getType()), costColor);
	}
	
	public static ElementStack createResourceStack(ResourceAmount cost) {
		String valueString = StringUtils.formatBigNumber(Math.abs(cost.getAmount()));
		Color resourceColor = ProfileModuleAPI.getColorForResource(cost.getType());
		return createLabelStack(cost.getType().toString(), valueString, resourceColor, resourceColor);
	}
	
}
