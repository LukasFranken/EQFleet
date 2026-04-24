package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.logging.LogLine;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.Metric;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.debug.modulator.Modulator;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

@SuppressWarnings("rawtypes")
public class Console {
	
	private static CommandProcessor commandProcessor;
	
	private static int tapSize = 100;
	private static int metricsHeight = 200;
	private static int profilerHeight = 200;
	private static int modulatorHeight = 200;
	private static int consoleInputHeight = 30;
	private static int borderMargin = 30;
	
	private static int logLineHeight = 14;
	
	private static List<ActivationScreenTap> activationScreenTaps;
	
	private static LimitedInputField commandTextField;
	
	private static boolean active;
	
	private static List<String> inputList;
	private static int lastCommandIndex = -1;
	private static List<String> tagFilter;
	
	private static Rectangle logsBounds;
	
	private static EQRectangle logsContainerShape;
	
	private static Label logLineLabel;
	private static List<LogLine> logLines;
	
	private static ColorButton commandUpButton;
	private static ColorButton commandDownButton;
	private static ColorButton commandCompleteButton;
	private static ColorButton commandSendButton;
	
	public static void init() {
		commandProcessor = new CommandProcessor();
		BaseCommandLoader baseCommandLoader = new BaseCommandLoader();
		commandProcessor.addCommands(baseCommandLoader.getCommands());
		inputList = new ArrayList<>();
		MetricUtil.init();
		Profiler.init();
		Modulator.init();
		logsBounds = new Rectangle();
		
		logLineLabel = new Label("");
		logLineLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		logLineLabel.setType(FontType.MICRO);
	}

	public static void build() {
		logsContainerShape = EQRectangle.builder()
				.bounds(logsBounds)
				.color(new Color(SkinManager.skinColor))
				.thickness(1)
				.round(true)
				.build();
		
		commandUpButton = new ColorButton("^");
		commandUpButton.setConsoleBypass(true);
		commandUpButton.setAction(() -> {
			commandListUp();
		});
		
		commandDownButton = new ColorButton("v");
		commandDownButton.setConsoleBypass(true);
		commandDownButton.setAction(() -> {
			commandListDown();
		});
		
		commandCompleteButton = new ColorButton("..");
		commandCompleteButton.setConsoleBypass(true);
		commandCompleteButton.setAction(() -> {
			autoCompleteCommand();
		});
		
		commandSendButton = new ColorButton(">>");
		commandSendButton.setConsoleBypass(true);
		commandSendButton.setAction(() -> {
			sendCommand();
		});
		
		buildMetrics();
		buildProfiler();
		buildModulator();
		activationScreenTaps = new ArrayList<>();
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(0, GraphicsUtil.screenBounds().getHeight() - tapSize, tapSize, tapSize))
				.activated(false)
				.build());
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(GraphicsUtil.screenBounds().getWidth() - tapSize, GraphicsUtil.screenBounds().getHeight() - tapSize, tapSize, tapSize))
				.activated(false)
				.build());
		
		commandTextField = new LimitedInputField();
		commandTextField.getContentLabel().setType(FontType.SMALL);
		commandTextField.setMaxChars(25);
		commandTextField.setPopupMessage("Enter command");
		commandTextField.setAction(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				if (!PlatformUtil.isMobile()) {
					sendCommand();
				}
			}
			
		});
		loadFilter();
	}
	
	public static void loadFilter() {
		tagFilter = new ArrayList<>();
		String filterString = PreferenceUtil.load("console_tag_filter");
		for (String tag : filterString.split(";")) {
			if (!tag.isEmpty()) {
				Console.getTagFilter().add(tag);
			}
		}
	}
	
	public static List<Command> getRegisteredCommands() {
		return commandProcessor.getCommands();
	}
	
	public static void saveFilter() {
		String filterString = "";
		for (String tag : Console.getTagFilter()) {
			if (!filterString.isEmpty()) {
				filterString += ";";
			}
			filterString += tag;
		}
		PreferenceUtil.save("console_tag_filter", filterString);
	}
	
	public static List<String> getTagFilter() {
		return tagFilter;
	}
	
	private static void buildMetrics() {
    	MetricUtil.build();
    	MetricUtil.setFixedHeight(metricsHeight);
    	if (!MetricUtil.isActive()) {
			MetricUtil.toggle();
		}
	}
	
	private static void buildProfiler() {
    	Profiler.build();
    	Profiler.setFixedHeight(profilerHeight);
    	if (!Profiler.isActive()) {
    		Profiler.toggle();
		}
	}
	
	private static void buildModulator() {
    	Modulator.build();
    	Modulator.setFixedHeight(modulatorHeight);
	}
	
	public static void addCommands(List<Command> commands) {
		commandProcessor.addCommands(commands);
	}
	
	public static void toggle() {
		active = !active;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static void render() {
		pollForConsoleActivation();
		MetricUtil.update();
		if (active) {
			Shapes.draw(EQRectangle.builder()
					.bounds(GraphicsUtil.screenBounds())
					.color(new Color(0, 0, 0, 0.85f))
					.filled(true)
					.build());
			MetricUtil.render();
			Profiler.render();
			Modulator.render();
			renderLogs();
			renderConsoleInput();
		}
	}
	
	public static void registerMetric(Metric metric) {
		MetricUtil.register(metric);
	}
	
	public static void updateMetric(Metric metric) {
		MetricUtil.update(metric.getTag(), metric.getValue());
	}

	public static void updateMetric(String tag, Object value) {
		MetricUtil.update(tag, value);
	}
	
	public static void remove(String tag) {
		MetricUtil.remove(tag);
	}

	private static void renderLogs() {
		int logPanelMargin = 10;
		logsBounds.set(logPanelMargin, 
					consoleInputHeight + logPanelMargin + borderMargin, 
					GraphicsUtil.screenBounds().getWidth() - (logPanelMargin * 2), 
					GraphicsUtil.screenBounds().getHeight() - consoleInputHeight - metricsHeight - profilerHeight - modulatorHeight - (logPanelMargin * 3) - borderMargin);
		Shapes.draw(logsContainerShape);
		
		int logLineHorizontalMargin = 5;
		logLines = Logger.getLogs((int)(logsBounds.height / logLineHeight), tagFilter);
		for (int i = 0; i < logLines.size(); i++) {
			LogLine logLine = logLines.get(logLines.size() - 1 - i);
			String logMessage = "[" + StringUtils.getTime(logLine.getTimestamp()) + "] " + logLine.getTag() + " - " + logLine.getMessage();
			String labelText = StringUtils.limitWithDots(logMessage, (int)((logsBounds.width - (logLineHorizontalMargin * 2)) / FontUtil.getFontTextWidthPx(1, FontType.MICRO)));
			logLineLabel.setText(labelText);
			logLineLabel.setColor(logLine.getColor().getGameColor());
			logLineLabel.setBounds(logsBounds.x + logLineHorizontalMargin,
					logsBounds.y + (i * logLineHeight),
					logsBounds.width - (logLineHorizontalMargin * 2),
					logLineHeight);
			logLineLabel.render();
		}
	}
	
	private static void renderConsoleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
	        commandListUp();
	    }
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
	        commandListDown();
	    }
		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			autoCompleteCommand();
		}
		
		commandUpButton.setBounds(10, borderMargin, 30, consoleInputHeight);
		commandUpButton.render();
		
		commandCompleteButton.setBounds(45, borderMargin, 30, consoleInputHeight);
		commandCompleteButton.render();
		
		commandSendButton.setBounds(GraphicsUtil.screenBounds().getWidth() - 75, borderMargin, 30, consoleInputHeight);
		commandSendButton.render();
		
		commandDownButton.setBounds(GraphicsUtil.screenBounds().getWidth() - 40, borderMargin, 30, consoleInputHeight);
		commandDownButton.render();
		
		commandTextField.setBounds(80, borderMargin, GraphicsUtil.screenBounds().getWidth() - 160, consoleInputHeight);
		commandTextField.render();
	}
	
	private static void commandListUp() {
		if (!inputList.isEmpty()) {
            if (lastCommandIndex < inputList.size() - 1) {
                lastCommandIndex++;
            }
            commandTextField.setContent(inputList.get(inputList.size() - 1 - lastCommandIndex));
        }
	}
	
	private static void commandListDown() {
		if (lastCommandIndex > 0) {
            lastCommandIndex--;
            commandTextField.setContent(inputList.get(inputList.size() - 1 - lastCommandIndex));
        } else if (lastCommandIndex == 0 || lastCommandIndex == -1) {
            lastCommandIndex = -1;
            commandTextField.setContent("");
        }
	}
	
	private static void autoCompleteCommand() {
		commandTextField.setContent(commandProcessor.autocomplete(commandTextField.getContent()));
	}
	
	private static void sendCommand() {
		if (!commandTextField.getContent().contentEquals("")) {
			lastCommandIndex = -1;
			Logger.log("Command", commandTextField.getContent());
			inputList.add(commandTextField.getContent());
			commandProcessor.process(commandTextField.getContent());
			commandTextField.setContent("");
		}
	}

	private static void pollForConsoleActivation() {
		if (InputUtil.isJustPressed(Keys.F1)) {
			toggle();
		}
		if (InputUtil.isClickedConsole()) {
			boolean wasValidTap = false;
			for (ActivationScreenTap screenTap : activationScreenTaps) {
				if (!screenTap.isActivated()) { 
					if (InputUtil.mouseIsOver(screenTap.getRegion())) {
						screenTap.setActivated(true);
						wasValidTap = true;
					} else {
						break;
					}
				}
			}
			if (!wasValidTap) {
				for (ActivationScreenTap screenTap : activationScreenTaps) {
					screenTap.setActivated(false);
				}
			}
			
			for (ActivationScreenTap screenTap : activationScreenTaps) {
				if (!screenTap.isActivated()) {
					return;
				}
			}
			toggle();
			for (ActivationScreenTap screenTap : activationScreenTaps) {
				screenTap.setActivated(false);
			}
		}
	}

	public static void dispose() {
		MetricUtil.dispose();
		Profiler.dispose();
		Modulator.dispose();
		commandTextField.dispose();
	}

}
