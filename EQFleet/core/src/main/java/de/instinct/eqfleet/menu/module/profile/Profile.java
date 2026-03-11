package de.instinct.eqfleet.menu.module.profile;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.API;
import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.profile.message.ProfileMessage;
import de.instinct.eqfleet.menu.module.profile.message.types.LoadProfileMessage;
import de.instinct.eqfleet.menu.module.profile.message.types.RegisterMessage;
import de.instinct.eqfleet.net.WebManager;

public class Profile extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.PROFILE;
	}
	
	@Override
	public void init() {
		ProfileModel.messageQueue = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public void load() {
		loadData();
	}

	@Override
	public void update() {
		if (!ProfileModel.messageQueue.isEmpty()) {
			process(ProfileModel.messageQueue.poll());
		}
	}
	
	private void process(ProfileMessage message) {
		if (message instanceof LoadProfileMessage) {
			loadData();
		}
		if (message instanceof RegisterMessage) {
			register(((RegisterMessage) message).getUsername());
		}
	}

	public void register(String username) {
		WebManager.enqueue(
			    () -> API.meta().registerName(username),
			    result -> {
			    	ProfileModel.nameRegisterResponseCode = result;
			    	if (result == NameRegisterResponseCode.SUCCESS) {
			    		loadData();
			    	}
			    }
		);
	}

	private void loadData() {
		WebManager.enqueue(
			    () -> API.meta().profile(API.authKey),
			    result -> {
			    	ProfileModel.profile = result;
			    }
		);
		WebManager.enqueue(
			    () -> API.commander().data(API.authKey),
			    result -> {
			    	ProfileModel.commanderData = result;
			    }
		);
		WebManager.enqueue(
			    () -> API.meta().resources(API.authKey),
			    result -> {
			        ProfileModel.resources = result;
			    }
		);
	}

	@Override
	public void close() {
		
	}

}
