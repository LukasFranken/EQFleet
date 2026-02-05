package de.instinct.eqfleet.menu.module.ship.component.shippart;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.matchmaking.dto.ShipComponentResult;
import de.instinct.api.matchmaking.dto.ShipResult;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShipProgressSection extends Component {
	
	private ShipResult result;
	private float progress;
	private List<ShipComponentProgressBar> progressBars;
	
	public ShipProgressSection(ShipResult result) {
		this.result = result;
		progressBars = new ArrayList<>();
		
		for (ShipComponentResult componentResult : result.getComponentResults()) {
			ShipComponentProgressBar progressBar = new ShipComponentProgressBar(componentResult.getType(), componentResult.getMaxProgress());
			progressBars.add(progressBar);
		}
	}
	
	@Override
	protected void updateComponent() {
		int i = 0;
		for (ShipComponentResult componentResult : result.getComponentResults()) {
			float easedProgress = MathUtil.easeInOut(componentResult.getStartProgress(), componentResult.getEndProgress(), progress);
			ShipComponentProgressBar currentProgressBar = progressBars.get(i);
			currentProgressBar.setCurrentProgress(easedProgress);
			currentProgressBar.setFixedWidth(getFixedWidth() - 10);
			currentProgressBar.setPosition(getBounds().x + 5f, getBounds().y + getBounds().height - 30f - (i * 10f));
			i++;
		}
	}
	
	@Override
	protected void renderComponent() {
		Shapes.draw(EQRectangle.builder()
				.bounds(getBounds())
				.color(SkinManager.skinColor)
				.label(result.getModel())
				.build());
		for (ShipComponentProgressBar progressBar : progressBars) {
			progressBar.render();
		}
	}

	@Override
	public float calculateHeight() {
		return 15f + (result.getComponentResults().size() + 1) * 10;
	}

	@Override
	public float calculateWidth() {
		return getFixedWidth();
	}

	@Override
	public void dispose() {
		for (ShipComponentProgressBar progressBar : progressBars) {
			progressBar.dispose();
		}
	}

}
