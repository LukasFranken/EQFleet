package de.instinct.eqlibgdxutils.debug.console;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.Metric;
import de.instinct.eqlibgdxutils.debug.MetricUtil;
import de.instinct.eqlibgdxutils.debug.logging.LogLine;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.SimpleShapeRenderer;

@SuppressWarnings("rawtypes")
public class Console {
	
	private static int tapSize = 100;
	private static int metricsHeight = 200;
	private static int consoleInputHeight = 30;
	private static int borderMargin = 30;
	
	private static int logLineHeight = 14;
	
	private static List<ActivationScreenTap> activationScreenTaps;
	private static Texture backgroundTexture;
	
	private static LimitedInputField commandTextField;
	
	private static boolean active;
	
	public static void init() {
		backgroundTexture = TextureManager.createTexture(new Color(0, 0, 0, 0.8f));
		initializeMetrics();
		activationScreenTaps = new ArrayList<>();
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(0, 0, tapSize, tapSize))
				.build());
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(0, Gdx.graphics.getHeight() - tapSize, tapSize, tapSize))
				.build());
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(Gdx.graphics.getWidth() - tapSize, Gdx.graphics.getHeight() - tapSize, tapSize, tapSize))
				.build());
		activationScreenTaps.add(ActivationScreenTap.builder()
				.region(new Rectangle(Gdx.graphics.getWidth() - tapSize, 0, tapSize, tapSize))
				.build());
		
		commandTextField = new LimitedInputField();
		commandTextField.setMaxChars(50);
		commandTextField.setPopupMessage("Enter command");
		commandTextField.setAction(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				Logger.log("Command", commandTextField.getContent());
				commandTextField.setContent("");
			}
			
		});
	}
	
	private static void initializeMetrics() {
    	MetricUtil.init();
    	MetricUtil.setFixedHeight(metricsHeight);
    	if (!MetricUtil.isActive()) {
			MetricUtil.toggle();
		}
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
			TextureManager.draw(backgroundTexture, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
			MetricUtil.render();
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

	private static void renderLogs() {
		int logPanelMargin = 10;
		Rectangle logsBounds = new Rectangle(
				logPanelMargin, 
				consoleInputHeight + logPanelMargin + borderMargin, 
				Gdx.graphics.getWidth() - (logPanelMargin * 2), 
				Gdx.graphics.getHeight() - consoleInputHeight - metricsHeight - (logPanelMargin * 2) - borderMargin);
		SimpleShapeRenderer.drawRectangle(logsBounds, DefaultUIValues.skinColor, 1);
		
		int logLineHorizontalMargin = 5;
		List<LogLine> logLines = Logger.getLogs((int)(logsBounds.height / logLineHeight));
		for (int i = 0; i < logLines.size(); i++) {
			LogLine logLine = logLines.get(logLines.size() - 1 - i);
			String logMessage = "[" + StringUtils.getTime(logLine.getTimestamp()) + "] " + logLine.getTag() + " - " + logLine.getMessage();
			String labelText = StringUtils.limitWithDotDotDot(logMessage, (int)((logsBounds.width - (logLineHorizontalMargin * 2)) / FontUtil.getFontTextWidthPx(1, FontType.TINY)));
			Label logLineLabel = new Label(labelText);
			logLineLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
			logLineLabel.setType(FontType.TINY);
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
		commandTextField.setBounds(new Rectangle(borderMargin, borderMargin, Gdx.graphics.getWidth() - (borderMargin * 2), consoleInputHeight));
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
		backgroundTexture.dispose();
		commandTextField.dispose();
	}

}
