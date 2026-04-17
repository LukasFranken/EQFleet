package de.instinct.eqfleet.mining.frontend.hud.element;

import java.util.ArrayList;
import java.util.List;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.entity.ship.cargo.CargoItem;
import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.frontend.OreManager;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.model.BarFragment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.FragmentRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;

public class CargoHudElement extends Component {
	
	private FragmentRectangularLoadingBar cargoBar;
	private List<BarFragment> cargoFragments;
	
	public CargoHudElement() {
		super();
		Border barBorder = new Border();
		barBorder.setColor(GameConfig.teammate1Color);
		barBorder.setSize(1f);
		
		cargoBar = new FragmentRectangularLoadingBar();
		cargoBar.setBorder(barBorder);
		cargoFragments = new ArrayList<>();
	}
	
	@Override
	protected void updateComponent() {
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);
		updateFragmentsForCargo(ship);
		cargoBar.setMaxValue(ship.cargo.capacity);
		cargoBar.setFragments(cargoFragments);
		cargoBar.setBounds(20, GraphicsUtil.screenBounds().height - 86, 160, 10);
	}
	
	private void updateFragmentsForCargo(MiningPlayerShip ship) {
		cargoFragments.clear();
		for (CargoItem item : ship.cargo.items) {
			cargoFragments.add(BarFragment.builder()
					.value(item.amount)
					.color(OreManager.getColorForResourceType(item.resourceType))
					.build());
		}
	}

	@Override
	protected void renderComponent() {
		cargoBar.render();
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
		cargoBar.dispose();
	}
	
}
