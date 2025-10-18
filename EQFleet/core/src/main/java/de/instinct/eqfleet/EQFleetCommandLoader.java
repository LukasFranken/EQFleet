package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import de.instinct.api.core.API;
import de.instinct.api.meta.dto.ExperienceUpdateResponseCode;
import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.api.meta.dto.ResourceData;
import de.instinct.api.meta.dto.ResourceUpdateResponseCode;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.debug.console.Command;
import de.instinct.eqlibgdxutils.debug.console.CommandAction;
import de.instinct.eqlibgdxutils.debug.console.CommandLoader;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.rendering.GlobalRenderingConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinColor;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class EQFleetCommandLoader implements CommandLoader {
	
	private static final String LOG_TAG = "Console";
	private static final ConsoleColor LOG_COLOR = ConsoleColor.DEFAULT;

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new ArrayList<>();
		commands.add(Command.builder()
				.method("exit")
				.logMethod("exit")
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
				.logMethod("version")
				.description("show the current EQFleet version")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("EQFleet Version: v" + App.VERSION);
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
				.logMethod("menu.reload")
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
				.logMethod("config.key")
				.description("show the auth key for the EQFleet API")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("saved authkey: " + PreferenceManager.load("authkey"));
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.deletekey")
				.logMethod("config.deletekey")
				.description("delete the local auth key for the EQFleet API")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						PreferenceManager.save("authkey", "");
						log("local auth key deleted");
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("config.glow=")
				.logMethod("config.glow=<mode>")
				.description("glow effect for UI elements, see modes with 'listglow'")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String mode = message.replace("config.glow=", "").trim();
						switch (mode) {
						case "off":
							PreferenceManager.save("glow", "0");
							GlobalRenderingConfiguration.defaultGlowRadius = 0;
							Menu.reload();
							log("Glow effect disabled");
							break;
						case "low":
							PreferenceManager.save("glow", "10");
							GlobalRenderingConfiguration.defaultGlowRadius = 10;
							Menu.reload();
							log("Glow effect set to low");
							break;
						case "high":
							PreferenceManager.save("glow", "30");
							GlobalRenderingConfiguration.defaultGlowRadius = 30;
							Menu.reload();
							log("Glow effect set to high");
							break;

						default:
							log("Invalid glow mode, use 'listglow'");
							break;
						}
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("listglow")
				.logMethod("listglow")
				.description("lists all glow modes")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("off, low, high");
					}
					
				})
				.build());
		
		configCommands.add(Command.builder()
				.method("config.skin=")
				.logMethod("config.skin=<color>")
				.description("set the skin color, see available colors with 'listskin'")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						String color = message.replace("config.skin=", "").trim().toUpperCase();
						try {
							SkinManager.setSkinColor(SkinColor.valueOf(color));
							PreferenceManager.save("skin", color);
							Menu.reload();
							log("Skin color set to " + color);
						} catch (Exception e) {
							log("Error setting skin color: " + color);
							log(e.getMessage());
						}
					}
					
				})
				.build());
		configCommands.add(Command.builder()
				.method("listskin")
				.logMethod("listskin")
				.description("list all available skin colors")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("red, blue, purple, pink, orange, yellow, gray, white");
					}
					
				})
				.build());
		return configCommands;
	}
	
	private List<Command> getGameCommands() {
		List<Command> gameCommands = new ArrayList<>();
		gameCommands.add(Command.builder()
				.method("listresource")
				.logMethod("listresource")
				.description("list all available resource types")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						log("credits, metal, crystal, deuterium, equilibrium");
					}
					
				})
				.build());
		gameCommands.add(Command.builder()
				.method("addresource=")
				.logMethod("addresource=<type>,<amount>")
				.description("adds an amount of a resource, see 'listresource' for types")
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
				.method("addallresources")
				.logMethod("addallresources")
				.description("adds 1000 of all resources")
				.action(new CommandAction() {
					
					@Override
					public void execute(String message) {
						ResourceData resources = new ResourceData();
						resources.setResources(new ArrayList<>());
						ResourceAmount credits = new ResourceAmount();
						credits.setType(Resource.CREDITS);
						credits.setAmount(10000);
						resources.getResources().add(credits);
						ResourceAmount metal = new ResourceAmount();
						metal.setType(Resource.METAL);
						metal.setAmount(1000);
						resources.getResources().add(metal);
						ResourceAmount crystal = new ResourceAmount();
						crystal.setType(Resource.CRYSTAL);
						crystal.setAmount(1000);
						resources.getResources().add(crystal);
						ResourceAmount deuterium = new ResourceAmount();
						deuterium.setType(Resource.DEUTERIUM);
						deuterium.setAmount(1000);
						resources.getResources().add(deuterium);
						ResourceAmount equilibrium = new ResourceAmount();
						equilibrium.setType(Resource.EQUILIBRIUM);
						equilibrium.setAmount(1000);
						resources.getResources().add(equilibrium);
						try {
							WebManager.enqueue(
								    () -> API.meta().addResources(API.authKey, resources),
								    result -> {
								    	if (result == ResourceUpdateResponseCode.SUCCESS) {
								    		log("added resources");
								    	} else {
								    		log("error adding resources: " + result);
								    	}
								    }
							);
						} catch (Exception e) {
							Logger.log(LOG_TAG, "malformed command. see resourcehelp", LOG_COLOR);
						}
					}
					
				})
				.build());
		gameCommands.add(Command.builder()
				.method("addexperience=")
				.logMethod("addexperience=<amount>")
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
