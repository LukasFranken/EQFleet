package de.instinct.eqfleet.menu.module.main.tab.profile;

import de.instinct.api.core.API;
import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.net.GlobalStaticData;
import de.instinct.eqfleet.net.WebManager;

public class ProfileTab {
	
	private static ProfileTabRenderer renderer;
	private static ProfileTabLogic logic;
	
	public static NameRegisterResponseCode nameRegisterReponseCode;

	public static void init() {
		renderer = new ProfileTabRenderer();
		renderer.init();
		logic = new ProfileTabLogic();
		logic.init();
	}

	public static void update() {
		logic.update();
	}
	
	public static void render() {
		renderer.render();
	}
	
	public static void dispose() {
		renderer.dispose();
	}

	public static void register(String username) {
		WebManager.enqueue(
			    () -> API.meta().registerName(username),
			    result -> {
			    	if (result == NameRegisterResponseCode.SUCCESS) {
			    		loadData();
					} else {
						renderer.processNameRegisterResponseCode(nameRegisterReponseCode);
					}
			    }
		);
	}

	public static void loadData() {
		WebManager.enqueue(
			    () -> API.meta().profile(API.authKey),
			    result -> {
			        GlobalStaticData.profile = result;
			    }
		);
	}

}
