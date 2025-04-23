package de.instinct.eqfleet.menu.module.main.tab.profile;

import de.instinct.eqfleet.net.meta.MetaInterface;
import de.instinct.eqfleet.net.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.net.model.NetworkResponse;
import de.instinct.eqfleet.net.model.ResponseAction;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;

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
		MetaInterface.registerName(username, new ResponseAction() {
			
			@Override
			public void execute(NetworkResponse response) {
				nameRegisterReponseCode = ObjectJSONMapper.mapJSON(response.getPayload(), NameRegisterResponseCode.class);
				if (nameRegisterReponseCode == NameRegisterResponseCode.SUCCESS) {
					MetaInterface.loadProfile();
				} else {
					renderer.processNameRegisterResponseCode(nameRegisterReponseCode);
				}
			}
			
		});
	}

	public static void loadData() {
		MetaInterface.loadProfile();
	}

}
