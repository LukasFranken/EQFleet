package de.instinct.eqfleet.menu.module.social;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.module.social.message.SocialMessage;

public class Social extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.SOCIAL;
	}

	@Override
	public void init() {
		SocialModel.messageQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void load() {
		
	}

	@Override
	public void update() {
		if (!SocialModel.messageQueue.isEmpty()) {
			process(SocialModel.messageQueue.poll());
		}
	}
	
	private void process(SocialMessage message) {
		
	}

	@Override
	public void close() {
		
	}

}
