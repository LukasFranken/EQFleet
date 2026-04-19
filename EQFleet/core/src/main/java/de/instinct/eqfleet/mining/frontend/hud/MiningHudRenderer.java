package de.instinct.eqfleet.mining.frontend.hud;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.engine.mining.order.RecallOrder;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.frontend.hud.element.CargoHudElement;
import de.instinct.eqfleet.mining.frontend.hud.element.EngineHudElement;
import de.instinct.eqfleet.mining.frontend.hud.element.MobileInputHud;
import de.instinct.eqlibgdxutils.PlatformUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class MiningHudRenderer {
	
    private ColorButton recallButton;
    
    private EngineHudElement engineHud;
    private CargoHudElement cargoHud;
    private MobileInputHud mobileInputHud;
	
	public void init() {
		engineHud = new EngineHudElement();
		cargoHud = new CargoHudElement();
		
		recallButton = new ColorButton("RECALL");
		recallButton.setColor(new Color(0.5f, 0f, 0f, 0.5f));
		recallButton.setDownColor(new Color());
		recallButton.setHoverColor(new Color());
		recallButton.getLabel().setColor(Color.GRAY);
		recallButton.getLabel().setType(FontType.BOLD);
		recallButton.setGlowAnimation(true);
		recallButton.getBorder().setColor(Color.GRAY);
		recallButton.setAction(new Action() {
			
			@Override
			public void execute() {
				RecallOrder recallOrder = new RecallOrder();
				recallOrder.playerId = MiningModel.playerId;
				MiningEngineAPI.addOrder(MiningModel.state, recallOrder);
			}
			
		});
		
		if (PlatformUtil.isMobile()) {
        	mobileInputHud = new MobileInputHud();
        }
	}
	
	public void update() {
		engineHud.update();
		cargoHud.update();
		
		if (mobileInputHud != null) mobileInputHud.update();
		
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);
		recallButton.setBounds(160, 50, 80, 40);
		recallButton.setGlowAnimation(!ship.recalled);
		if (ship.recalled) {
			recallButton.getHoverColor().set(0.3f, 0f, 0f, 0.5f);
			recallButton.getDownColor().set(0.5f, 0f, 0f, 0.7f);
			recallButton.getLabel().setText("CANCEL");
		} else {
			recallButton.getHoverColor().set(0f, 0f, 0.3f, 0.5f);
			recallButton.getDownColor().set(0f, 0f, 0.7f, 0.7f);
			recallButton.getLabel().setText("RECALL");
		}
	}

	public void render() {
		engineHud.render();
		cargoHud.render();
		
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);
		if (mobileInputHud != null) {
			if (!ship.recalled && MiningModel.state.metaData.pauseData.resumeCountdownMS <= 0) {
				mobileInputHud.render();
			}
		}
		
		if (MiningEngineAPI.shipIsRecallable(ship)) {
			recallButton.render();
		}
	}
	
	public void dispose() {
		engineHud.dispose();
		cargoHud.dispose();
		
		recallButton.dispose();
		if (mobileInputHud != null) {
			mobileInputHud.dispose();
		}
	}

}
