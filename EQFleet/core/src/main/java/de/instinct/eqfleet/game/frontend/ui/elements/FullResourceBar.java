package de.instinct.eqfleet.game.frontend.ui.elements;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.model.GameState;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.game.frontend.ui.UIDataUtility;
import de.instinct.eqfleet.game.frontend.ui.model.PlayerData;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class FullResourceBar extends Component {
	
	private BoxedRectangularLoadingBar bar;
	private Image resourceIcon;
	private Border border;
	private Label workingLabel;
	
	private GameState state;
	
	public FullResourceBar() {
		super();
		border = new Border();
		border.setSize(2f);
		border.setColor(new Color());
		
		bar = new BoxedRectangularLoadingBar();
		bar.setBorder(border);
		bar.setPartialSegments(true);
		resourceIcon = new Image(TextureManager.getTexture("ui/image/resources", "general_blue"));
		resourceIcon.setBorder(border);
		
		workingLabel = new Label("");
		workingLabel.setColor(new Color());
		workingLabel.setType(FontType.SMALL_BOLD);
	}
	
	@Override
	protected void updateComponent() {
		if (state == null) return;
		PlayerData playerData = UIDataUtility.getPlayerData(state);
		border.getColor().set(GameConfig.getPlayerColor(playerData.getSelf().id));
		
		bar.setSegments((int)playerData.getSelf().maxResources);
		bar.setMaxValue(playerData.getSelf().maxResources);
		bar.setCurrentValue(playerData.getSelf().currentResources);
		bar.setBounds(getBounds());
		
		resourceIcon.setBounds(getBounds().x - getBounds().height + 2, getBounds().y, getBounds().height, getBounds().height);
		
		workingLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		workingLabel.setColor(GameConfig.getPlayerColor(playerData.getSelf().id));
		workingLabel.setText(
				StringUtils.format(playerData.getSelf().currentResources, playerData.getSelf().maxResources < 100 ? 1 : 0) 
				+ "/" 
				+ StringUtils.format(playerData.getSelf().maxResources, playerData.getSelf().maxResources < 10 ? 1 : 0));
		workingLabel.setBounds(getBounds().x + getBounds().width - 50, getBounds().y + getBounds().height, 50, 20);
		workingLabel.render();
		
		workingLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		double totalResourceGenerationSpeed = UIDataUtility.calculateTotalResourceGenerationSpeed(state, playerData.getSelf());
		workingLabel.setText(StringUtils.format(totalResourceGenerationSpeed, totalResourceGenerationSpeed < 10 ? 2 : 1) + " /s");
		workingLabel.setBounds(getBounds().x, getBounds().y + getBounds().height, getBounds().width, 20);
		workingLabel.render();
	}

	public void setGameState(GameState state) {
		this.state = state;
	}

	@Override
	protected void renderComponent() {
		bar.render();
		resourceIcon.render();
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
		bar.dispose();
	}

}
