package de.instinct.eqfleet.menu.module.profile.model;

import java.util.ArrayList;
import java.util.List;

import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
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
			ColorButton button = DefaultButtonFactory.colorButton(option.getLabel(), option.getSwitchAction());
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
		for (ColorButton button : buttons) {
			button.setFixedHeight(getHeight());
		}
	}
	
	@Override
	protected void renderComponent() {
		
	}

	@Override
	public void dispose() {
		
	}

}
