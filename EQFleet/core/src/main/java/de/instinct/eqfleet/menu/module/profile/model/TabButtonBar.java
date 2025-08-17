package de.instinct.eqfleet.menu.module.profile.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TabButtonBar extends Component {
	
	private List<TabOption> options;
	
	private TabOption selectedOption;
	private List<ColorButton> buttons;
	
	public TabButtonBar(List<TabOption> options) {
		super();
		if (options == null || options.isEmpty()) {
			throw new IllegalArgumentException("Options list cannot be null or empty");
		}
		this.options = options;
		selectedOption = options.get(0);
		createButtons();
	}

	private void createButtons() {
		buttons = new ArrayList<>();
		for (TabOption option : options) {
			ColorButton button = DefaultButtonFactory.colorButton(option.getLabel(), () -> {
				selectedOption = option;
				if (option.getSwitchAction() != null) option.getSwitchAction().execute();
			});
			buttons.add(button);
		}
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
	protected void updateComponent() {
		int i = 0;
		float buttonWidth = getBounds().width / buttons.size();
		for (ColorButton button : buttons) {
			button.setFixedHeight(getBounds().height);
			button.setFixedWidth(buttonWidth);
			button.setPosition(getBounds().x + (i * buttonWidth), getBounds().y);
			if (button.getLabel().getText().equals(selectedOption.getLabel())) {
				button.getLabel().setColor(new Color(SkinManager.skinColor));
			} else {
				button.getLabel().setColor(Color.GRAY);
			}
			i++;
		}
	}
	
	@Override
	protected void renderComponent() {
		for (ColorButton button : buttons) {
			button.render();
		}
	}

	@Override
	public void dispose() {
		
	}

}
