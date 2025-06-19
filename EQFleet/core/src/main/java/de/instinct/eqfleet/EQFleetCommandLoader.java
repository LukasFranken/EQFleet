package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.console.Command;
import de.instinct.eqlibgdxutils.debug.console.CommandLoader;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinColor;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class EQFleetCommandLoader implements CommandLoader {
	
	private static final String LOG_TAG = "Console";
	private static final ConsoleColor LOG_COLOR = ConsoleColor.DEFAULT;

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new ArrayList<>();
		commands.add(Command.builder()
				.method("exit")
				.action(new Action() {
					
					@Override
					public void execute() {
						Gdx.app.exit();
					}
				})
				.build());
		
		commands.add(Command.builder()
				.method("version")
				.action(new Action() {
					
					@Override
					public void execute() {
						log("EQFleet Version: v" + App.VERSION);
					}
				})
				.build());
		commands.add(Command.builder()
				.method("clear")
				.action(new Action() {
					
					@Override
					public void execute() {
						Logger.clear();
					}
				})
				.build());
		commands.addAll(getMenuCommands());
		commands.addAll(getConfigCommands());
		return commands;
	}

	private List<Command> getMenuCommands() {
		List<Command> menuCommands = new ArrayList<>();
		menuCommands.add(Command.builder()
				.method("menu.reload")
				.action(new Action() {
					
					@Override
					public void execute() {
						Menu.reload();
						log("Menu reloaded");
					}
					
				})
				.build());
		return menuCommands;
	}
	
	private List<Command> getConfigCommands() {
		List<Command> configCommands = new ArrayList<>();
		configCommands.add(Command.builder()
				.method("config.glow")
				.action(new Action() {
					
					@Override
					public void execute() {
						log("options: glow=off, glow=low, glow=high");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=off")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("glow", "0");
						TextureManager.setDefaultGlowRadius(0);
						log("Glow effect disabled");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=low")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("glow", "10");
						TextureManager.setDefaultGlowRadius(10);
						log("Glow effect set to low");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=high")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("glow", "30");
						TextureManager.setDefaultGlowRadius(30);
						log("Glow effect set to high");
					}
					
				})
				.build());
		
		configCommands.add(Command.builder()
				.method("config.skin=red")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("skin", "RED");
						SkinManager.setSkinColor(SkinColor.RED);
						log("Skin color set to red");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=green")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("skin", "GREEN");
						SkinManager.setSkinColor(SkinColor.GREEN);
						log("Skin color set to green");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=blue")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("skin", "BLUE");
						SkinManager.setSkinColor(SkinColor.BLUE);
						log("Skin color set to blue");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=gray")
				.action(new Action() {
					
					@Override
					public void execute() {
						PreferenceUtil.save("skin", "GRAY");
						SkinManager.setSkinColor(SkinColor.GRAY);
						log("Skin color set to gray");
					}
					
				})
				.build());
		return configCommands;
	}

	private void log(String message) {
		Logger.log(LOG_TAG, message, LOG_COLOR);
	}

}
