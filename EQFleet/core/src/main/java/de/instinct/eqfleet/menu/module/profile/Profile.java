package de.instinct.eqfleet.menu.module.profile;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.module.core.ModuleMessage;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqfleet.menu.module.profile.message.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.profile.message.RegisterMessage;
import de.instinct.eqfleet.net.WebManager;

public class Profile extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.PROFILE;
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void open() {
		loadData();
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public boolean process(ModuleMessage message) {
		if (message instanceof RegisterMessage) {
			register(((RegisterMessage)message).getUsername());
			return true;
		}
		if (message instanceof LoadProfileMessage) {
			loadData();
			return true;
		}
		return false;
	}
	
	public void register(String username) {
		WebManager.enqueue(
			    () -> API.meta().registerName(username),
			    result -> {
			    	ProfileModel.nameRegisterResponseCode = result;
			    	if (result == NameRegisterResponseCode.SUCCESS) {
			    		loadData();
			    	} else {
			    		super.requireUIReload();
			    	}
			    }
		);
	}

	private void loadData() {
		WebManager.enqueue(
			    () -> API.meta().profile(API.authKey),
			    result -> {
			    	ProfileModel.profile = result;
			    	Menu.reloadContent();
			    	WebManager.enqueue(
						    () -> API.commander().data(API.authKey),
						    result2 -> {
						    	ProfileModel.commanderData = result2;
						    	super.requireUIReload();
						    }
					);
			    }
		);
		Inventory.loadData();
	}

	@Override
	public void close() {
		
	}

}
