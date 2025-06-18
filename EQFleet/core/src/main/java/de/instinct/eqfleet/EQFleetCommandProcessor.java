package de.instinct.eqfleet;

import com.badlogic.gdx.Gdx;

import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.debug.console.CommandProcessor;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class EQFleetCommandProcessor implements CommandProcessor {
	
	private static final String LOG_TAG = "CMD";
	private static final ConsoleColor LOG_COLOR = ConsoleColor.DEFAULT;

	@Override
	public void process(String command) {
		if (command.equalsIgnoreCase("exit")) {
			Gdx.app.exit();
		}
		if (command.equalsIgnoreCase("version")) {
			log("EQFleet Version: v" + App.VERSION);
		}
		if (command.equalsIgnoreCase("clear")) {
			Logger.clear();
		}
		if (command.startsWith("menu.")) {
			processMenuCommand(command.substring(5));
		}
	}
	
	private void processMenuCommand(String command) {
		if (command.equalsIgnoreCase("reload")) {
			Menu.reload();
			log("Menu reloaded");
		}
	}

	private void log(String message) {
		Logger.log(LOG_TAG, message, LOG_COLOR);
	}

}
