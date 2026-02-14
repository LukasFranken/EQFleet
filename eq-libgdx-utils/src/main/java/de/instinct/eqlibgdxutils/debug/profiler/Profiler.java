package de.instinct.eqlibgdxutils.debug.profiler;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class Profiler {
	
	private static float horizontalPanelOffset = 20f;
	
	private static ProfilerStatisticsRenderer statisticsRenderer;
	
	private static int fixedHeight;
	private static boolean active;
	private static Rectangle bounds;
	private static Rectangle resultAreaBounds;
	
	private static ColorButton startButton;
	private static ColorButton stopButton;
	private static ColorButton maxButton;
	private static ColorButton minButton;
	private static ColorButton avgButton;
	
	private static int buttonHeight = 30;
	
	private static List<Capture> captures;
	private static Capture currentCapture;
	private static Frame currentFrame;

	public static void init() {
		captures = new ArrayList<>();
		statisticsRenderer = new ProfilerStatisticsRenderer();
	}
	
	public static void build() {
		startButton = new ColorButton("start");
		startButton.setConsoleBypass(true);
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (currentCapture == null) {
					capture();
				}
			}
			
		});
		
		stopButton = new ColorButton("stop");
		stopButton.setConsoleBypass(true);
		stopButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (currentCapture != null) {
					stopCapture();
				}
			}
			
		});
		
		maxButton = new ColorButton("max");
		maxButton.setConsoleBypass(true);
		maxButton.setAction(new Action() {
			
			@Override
			public void execute() {
				statisticsRenderer.setMode(ProfilerStatisticMode.MAX);
			}
			
		});
		
		minButton = new ColorButton("min");
		minButton.setConsoleBypass(true);
		minButton.setAction(new Action() {
			
			@Override
			public void execute() {
				statisticsRenderer.setMode(ProfilerStatisticMode.MIN);
			}
			
		});
		
		avgButton = new ColorButton("avg");
		avgButton.setConsoleBypass(true);
		avgButton.setAction(new Action() {
			
			@Override
			public void execute() {
				statisticsRenderer.setMode(ProfilerStatisticMode.AVG);
			}
			
		});
	}
	
	private static void capture() {
		long captureStartTime = System.nanoTime();
		currentCapture = Capture.builder()
				.startTimeNS(captureStartTime)
				.frames(new ArrayList<>())
				.build();
	}
	
	private static void stopCapture() {
		currentCapture.setDurationNS(System.nanoTime() - currentCapture.getStartTimeNS());
		Frame avgFrame = Frame.builder()
				.checkpoints(getAvgCheckpoints())
				.durationNS(currentCapture.getDurationNS() / currentCapture.getFrames().size())
				.build();
		currentCapture.setAvgFrame(avgFrame);
		captures.add(currentCapture);
		currentCapture = null;
	}
	
	//Requires every frame to have the same checkpoints in the same order
	private static List<Checkpoint> getAvgCheckpoints() {
		List<Checkpoint> avgCheckpoints = new ArrayList<>();
		for (Checkpoint checkpoint : currentCapture.getFrames().get(0).getCheckpoints()) {
			avgCheckpoints.add(Checkpoint.builder()
					.tag(checkpoint.getTag())
					.build());
		}
		for (Frame frame : currentCapture.getFrames()) {
			for (int i = 0; i < frame.getCheckpoints().size(); i++) {
				avgCheckpoints.get(i).setDurationNS(avgCheckpoints.get(i).getDurationNS() + frame.getCheckpoints().get(i).getDurationNS());
			}
		}
		for (Checkpoint checkpoint : avgCheckpoints) {
			checkpoint.setDurationNS(checkpoint.getDurationNS() / currentCapture.getFrames().size());
		}
		return avgCheckpoints;
	}

	public static void render() {
		update();
		
		Shapes.draw(EQRectangle.builder()
				.bounds(bounds)
				.color(SkinManager.skinColor)
				.thickness(1)
				.round(true)
				.build());
		
		Shapes.draw(EQRectangle.builder()
				.bounds(resultAreaBounds)
				.color(SkinManager.skinColor)
				.thickness(1)
				.round(true)
				.build());
		
		startButton.render();
		stopButton.render();
		
		if (currentCapture == null) {
			if (!captures.isEmpty()) {
				statisticsRenderer.setCapture(captures.get(captures.size() - 1));
				maxButton.render();
				minButton.render();
				avgButton.render();
			}
		} else {
			statisticsRenderer.setCapture(currentCapture);
		}
		statisticsRenderer.render();
	}
	
	public static void update() {
		bounds = new Rectangle(horizontalPanelOffset, GraphicsUtil.screenBounds().height - fixedHeight - MetricUtil.getFixedHeight(), GraphicsUtil.screenBounds().width - (2 * horizontalPanelOffset), fixedHeight - 10f);
		resultAreaBounds = new Rectangle(bounds.x + 10f, bounds.y + buttonHeight + 20f, bounds.width - 20f, bounds.height - buttonHeight - 30f);
		statisticsRenderer.setBounds(resultAreaBounds);
		
		startButton.setPosition(bounds.x + 10f, bounds.y + 10f);
		startButton.setFixedWidth(60f);
		startButton.setFixedHeight(buttonHeight);
		
		stopButton.setPosition(startButton.getBounds().x + startButton.getFixedWidth() + 5f, bounds.y + 10f);
		stopButton.setFixedWidth(60f);
		stopButton.setFixedHeight(buttonHeight);
		
		maxButton.setFixedWidth(40f);
		maxButton.setPosition(bounds.x + bounds.width - maxButton.getFixedWidth() - 10f, bounds.y + 10f);
		maxButton.setFixedHeight(buttonHeight);
		
		minButton.setFixedWidth(40f);
		minButton.setPosition(maxButton.getBounds().x - minButton.getFixedWidth() - 5f, bounds.y + 10f);
		minButton.setFixedHeight(buttonHeight);
		
		avgButton.setFixedWidth(40f);
		avgButton.setPosition(minButton.getBounds().x - avgButton.getFixedWidth() - 5f, bounds.y + 10f);
		avgButton.setFixedHeight(buttonHeight);
	}
	
	public static void setFixedHeight(int height) {
		fixedHeight = height;
	}
	
	public static void toggle() {
		active = !active;
	}

	public static boolean isActive() {
		return active;
	}
	
	public static void startFrame() {
		if (currentCapture != null) {
			if (currentFrame == null) {
				currentFrame = Frame.builder()
						.checkpoints(new ArrayList<>())
						.build();
				currentFrame.setStartTimeNS(System.nanoTime());
			}
		}
	}
	
	public static void checkpoint(String tag) {
		if (currentCapture != null) {
			if (currentFrame != null) {
				currentFrame.getCheckpoints().add(Checkpoint.builder()
						.tag(tag)
						.timeStampNS(System.nanoTime())
						.durationNS(currentFrame.getCheckpoints().isEmpty() ? System.nanoTime() - currentFrame.getStartTimeNS() : System.nanoTime() - currentFrame.getCheckpoints().get(currentFrame.getCheckpoints().size() - 1).getTimeStampNS())
						.build());
			}
		}
	}
	
	public static void endFrame() {
		if (currentCapture != null) {
			if (currentFrame != null) {
				currentCapture.getFrames().add(currentFrame);
				currentFrame.setDurationNS(System.nanoTime() - currentFrame.getStartTimeNS());
				
				if (currentCapture.getMaxFrameTimeNS() < currentFrame.getDurationNS()) {
					currentCapture.setMaxFrameTimeNS(currentFrame.getDurationNS());
					currentCapture.setMaxFrame(currentFrame);
				}
				if (currentCapture.getMinFrameTimeNS() == 0 || currentCapture.getMinFrameTimeNS() > currentFrame.getDurationNS()) {
					currentCapture.setMinFrameTimeNS(currentFrame.getDurationNS());
					currentCapture.setMinFrame(currentFrame);
				}
				
				currentFrame = null;
			}
		}
	}

}
