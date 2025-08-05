package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.core.API;
import de.instinct.api.meta.dto.ExperienceUpdateResponseCode;
import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.api.meta.dto.ResourceUpdateResponseCode;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.console.Command;
import de.instinct.eqlibgdxutils.debug.console.CommandAction;
import de.instinct.eqlibgdxutils.debug.console.CommandLoader;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
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
				.description("close the application")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Gdx.app.exit();
					}
				})
				.build());
		
		commands.add(Command.builder()
				.method("version")
				.description("show the current EQFleet version")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("EQFleet Version: v" + App.VERSION);
					}
				})
				.build());
		commands.add(Command.builder()
				.method("clear")
				.description("clear the console log")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Logger.clear();
					}
				})
				.build());
		commands.addAll(getMenuCommands());
		commands.addAll(getConfigCommands());
		commands.addAll(getGameCommands());
		return commands;
	}

	private List<Command> getMenuCommands() {
		List<Command> menuCommands = new ArrayList<>();
		menuCommands.add(Command.builder()
				.method("menu.reload")
				.description("reload the menu and all its modules")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						Menu.loadModules();
						log("Menu reloaded");
					}
					
				})
				.build());
		return menuCommands;
	}
	
	private List<Command> getConfigCommands() {
		List<Command> configCommands = new ArrayList<>();
		configCommands.add(Command.builder()
				.method("config.key")
				.description("show the auth key for the EQFleet API")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("saved authkey: " + PreferenceUtil.load("authkey"));
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.deletekey")
				.description("delete the local auth key for the EQFleet API")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("authkey", "");
						log("local auth key deleted");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=off")
				.description("disable the glow effect for UI elements")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("glow", "0");
						TextureManager.setDefaultGlowRadius(0);
						Menu.reload();
						log("Glow effect disabled");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=low")
				.description("set the glow effect for UI elements to low (10px)")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("glow", "10");
						TextureManager.setDefaultGlowRadius(10);
						Menu.reload();
						log("Glow effect set to low");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=high")
				.description("set the glow effect for UI elements to high (30px)")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("glow", "30");
						TextureManager.setDefaultGlowRadius(30);
						Menu.reload();
						log("Glow effect set to high");
					}
					
				})
				.build());
		
		configCommands.add(Command.builder()
				.method("config.skin=red")
				.description("set the skin color to red (default)")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "RED");
						SkinManager.setSkinColor(SkinColor.RED);
						Menu.reload();
						log("Skin color set to red");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=blue")
				.description("set the skin color to blue")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "BLUE");
						SkinManager.setSkinColor(SkinColor.BLUE);
						Menu.reload();
						log("Skin color set to blue");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=purple")
				.description("set the skin color to purple")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "PURPLE");
						SkinManager.setSkinColor(SkinColor.PURPLE);
						Menu.reload();
						log("Skin color set to purple");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=pink")
				.description("set the skin color to pink")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "PINK");
						SkinManager.setSkinColor(SkinColor.PINK);
						Menu.reload();
						log("Skin color set to pink");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=orange")
				.description("set the skin color to orange")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "ORANGE");
						SkinManager.setSkinColor(SkinColor.ORANGE);
						Menu.reload();
						log("Skin color set to orange");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=yellow")
				.description("set the skin color to yellow")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "YELLOW");
						SkinManager.setSkinColor(SkinColor.YELLOW);
						Menu.reload();
						log("Skin color set to yellow");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=gray")
				.description("set the skin color to gray")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "GRAY");
						SkinManager.setSkinColor(SkinColor.GRAY);
						Menu.reload();
						log("Skin color set to gray");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.skin=white")
				.description("set the skin color to white")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceUtil.save("skin", "WHITE");
						SkinManager.setSkinColor(SkinColor.WHITE);
						Menu.reload();
						log("Skin color set to white");
					}
					
				})
				.build());
		return configCommands;
	}
	
	private List<Command> getGameCommands() {
		List<Command> gameCommands = new ArrayList<>();
		gameCommands.add(Command.builder()
				.method("resourcehelp")
				.description("get help for resource commands")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("usage: addresource=<type>,<amount>");
						log("Types: credits, metal, crystal, deuterium, equilibrium");
					}
					
				})
				.build());
		gameCommands.add(Command.builder()
				.method("addresource=")
				.description("adds an amount of a resource (see resourcehelp)")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String type = message.replace("addresource=", "").split(",")[0].trim();
						String amount = message.replace("addresource=", "").split(",")[1].trim();
						ResourceAmount resourceAmount = new ResourceAmount();
						try {
							resourceAmount.setType(Resource.valueOf(type.toUpperCase()));
							resourceAmount.setAmount(Long.parseLong(amount));
							WebManager.enqueue(
								    () -> API.meta().addResource(API.authKey, resourceAmount),
								    result -> {
								    	if (result == ResourceUpdateResponseCode.SUCCESS) {
								    		log("added " + amount + " " + type);
								    	} else {
								    		log("error adding resource: " + result);
								    	}
								    }
							);
						} catch (IllegalArgumentException e) {
							Logger.log(LOG_TAG, "'" + type + "' does not exist as a resource", LOG_COLOR);
						} catch (Exception e) {
							Logger.log(LOG_TAG, "malformed command. see resourcehelp", LOG_COLOR);
						}
					}
					
				})
				.build());
		gameCommands.add(Command.builder()
				.method("addexperience=")
				.description("adds an amount of experience")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						try {
							long experience = Long.parseLong(message.replace("addexperience=", "").trim());
							WebManager.enqueue(
								    () -> API.meta().experience(API.authKey, experience),
								    result -> {
								    	if (result == ExperienceUpdateResponseCode.SUCCESS) {
								    		log("added " + experience + " experience");
								    	} else {
								    		log("error adding resource: " + result);
								    	}
								    }
							);
						} catch (NumberFormatException e) {
							Logger.log(LOG_TAG, "malformed command. experience is not a number", LOG_COLOR);
						}
					}
					
				})
				.build());
		return gameCommands;
	}

	private void log(String message) {
		Logger.log(LOG_TAG, message, LOG_COLOR);
	}

}
