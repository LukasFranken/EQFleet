package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.logging.LogLine;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.Metric;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
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
	private static int consoleInputHeight = 30;
	private static int borderMargin = 30;
	
	private static int logLineHeight = 14;
	
	private static List<ActivationScreenTap> activationScreenTaps;
	
	private static LimitedInputField commandTextField;
	
	private static boolean active;
	
	private static List<String> inputList;
	private static int lastCommandIndex = -1;
	private static List<String> tagFilter;
	
	public static void init() {
		commandProcessor = new CommandProcessor();
		BaseCommandLoader baseCommandLoader = new BaseCommandLoader();
		commandProcessor.addCommands(baseCommandLoader.getCommands());
		inputList = new ArrayList<>();
		MetricUtil.init();
		Profiler.init();
	}
	
	public static void build() {
		buildMetrics();
		buildProfiler();
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
		commandTextField.setMaxChars(50);
		commandTextField.setPopupMessage("Enter command");
		commandTextField.setAction(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				lastCommandIndex = -1;
				Logger.log("Command", commandTextField.getContent());
				inputList.add(commandTextField.getContent());
				commandProcessor.process(commandTextField.getContent());
				commandTextField.setContent("");
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
		if (active) {
			Shapes.draw(EQRectangle.builder()
					.bounds(GraphicsUtil.screenBounds())
					.color(new Color(0, 0, 0, 0.85f))
					.build());
			MetricUtil.render();
			Profiler.render();
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
		Rectangle logsBounds = new Rectangle(
				logPanelMargin, 
				consoleInputHeight + logPanelMargin + borderMargin, 
				GraphicsUtil.screenBounds().getWidth() - (logPanelMargin * 2), 
				GraphicsUtil.screenBounds().getHeight() - consoleInputHeight - metricsHeight - profilerHeight - (logPanelMargin * 2) - borderMargin);
		Shapes.draw(EQRectangle.builder()
				.bounds(logsBounds)
				.color(SkinManager.skinColor)
				.thickness(1)
				.round(true)
				.build());
		
		int logLineHorizontalMargin = 5;
		List<LogLine> logLines = Logger.getLogs((int)(logsBounds.height / logLineHeight), tagFilter);
		for (int i = 0; i < logLines.size(); i++) {
			LogLine logLine = logLines.get(logLines.size() - 1 - i);
			String logMessage = "[" + StringUtils.getTime(logLine.getTimestamp()) + "] " + logLine.getTag() + " - " + logLine.getMessage();
			String labelText = StringUtils.limitWithDotDotDot(logMessage, (int)((logsBounds.width - (logLineHorizontalMargin * 2)) / FontUtil.getFontTextWidthPx(1, FontType.MICRO)));
			Label logLineLabel = new Label(labelText);
			logLineLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
			logLineLabel.setType(FontType.MICRO);
			logLineLabel.setColor(logLine.getColor().getGameColor());
			logLineLabel.setBounds(new Rectangle(
					logsBounds.x + logLineHorizontalMargin,
					logsBounds.y + (i * logLineHeight),
					logsBounds.width - (logLineHorizontalMargin * 2),
					logLineHeight));
			logLineLabel.render();
		}
	}
	
	private static void renderConsoleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
	        if (!inputList.isEmpty()) {
	            if (lastCommandIndex < inputList.size() - 1) {
	                lastCommandIndex++;
	            }
	            commandTextField.setContent(inputList.get(inputList.size() - 1 - lastCommandIndex));
	        }
	    } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
	        if (lastCommandIndex > 0) {
	            lastCommandIndex--;
	            commandTextField.setContent(inputList.get(inputList.size() - 1 - lastCommandIndex));
	        } else if (lastCommandIndex == 0) {
	            lastCommandIndex = -1;
	            commandTextField.setContent("");
	        }
	    }
		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			commandTextField.setContent(commandProcessor.autocomplete(commandTextField.getContent()));
		}
		commandTextField.setBounds(new Rectangle(borderMargin, borderMargin, GraphicsUtil.screenBounds().getWidth() - (borderMargin * 2), consoleInputHeight));
		commandTextField.render();
	}

	private static void pollForConsoleActivation() {
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
		commandTextField.dispose();
	}

}
