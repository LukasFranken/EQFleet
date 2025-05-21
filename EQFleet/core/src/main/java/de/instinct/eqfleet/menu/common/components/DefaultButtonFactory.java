package de.instinct.eqfleet.menu.common.components;

import com.badlogic.gdx.graphics.Color;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class DefaultButtonFactory {
	
	public static ColorButton moduleButton(String label, MenuModule module) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton moduleButton = new ColorButton(label.substring(0, 1));
		moduleButton.setBorder(buttonBorder);
		moduleButton.setColor(Color.BLACK);
		moduleButton.setFixedHeight(50);
		moduleButton.setFixedWidth(50);
		moduleButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		moduleButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		moduleButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		moduleButton.setAction(new Action() {
			
			@Override
			public void execute() {
				Menu.openModule(module);
			}
			
		});
		
		return moduleButton;
	}
	
	public static ColorButton colorButton(String label, Action clickAction) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton colorButton = new ColorButton(label);
		colorButton.setBorder(buttonBorder);
		colorButton.setColor(Color.BLACK);
		colorButton.setFixedHeight(50);
		colorButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		colorButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		colorButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		colorButton.setAction(clickAction);
		
		return colorButton;
	}

}
