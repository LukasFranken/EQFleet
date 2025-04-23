package de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar;

import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class LoadingBar extends Component {
	
	private double maxValue;
	private double currentValue;

}
