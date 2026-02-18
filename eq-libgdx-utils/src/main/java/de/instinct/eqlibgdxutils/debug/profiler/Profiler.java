package de.instinct.eqlibgdxutils.debug.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.debug.profiler.model.Capture;
import de.instinct.eqlibgdxutils.debug.profiler.model.Checkpoint;
import de.instinct.eqlibgdxutils.debug.profiler.model.Frame;
import de.instinct.eqlibgdxutils.debug.profiler.model.Section;
import de.instinct.eqlibgdxutils.debug.profiler.screen.types.active.MainActiveScreen;
import de.instinct.eqlibgdxutils.debug.profiler.screen.types.inactive.MainInactiveScreen;
import de.instinct.eqlibgdxutils.debug.profiler.screen.types.intro.IntroScreen;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class Profiler {
	
	private static float horizontalPanelOffset = 10f;
	
	private static int fixedHeight;
	private static boolean active;
	
	private static ColorButton startButton;
	private static ColorButton stopButton;
	
	private static Map<String, Frame> currentFrames;
	
	private static IntroScreen introScreen;
	private static MainActiveScreen activeScreen;
	private static MainInactiveScreen inactiveScreen;

	public static void init() {
		ProfilerModel.captures = new ArrayList<>();
		ProfilerModel.trackedCheckpoints = new ArrayList<>();
	}
	
	public static void build() {
		startButton = new ColorButton("start");
		startButton.setConsoleBypass(true);
		startButton.setAction(new Action() {
			
			@Override
			public void execute() {
				capture();
			}
			
		});
		
		stopButton = new ColorButton("stop");
		stopButton.setConsoleBypass(true);
		stopButton.setAction(new Action() {
			
			@Override
			public void execute() {
				stopCapture();
			}
			
		});
		
		introScreen = new IntroScreen();
		activeScreen = new MainActiveScreen();
		inactiveScreen = new MainInactiveScreen();
	}
	
	private static void capture() {
		reset();
		long captureStartTime = System.nanoTime();
		ProfilerModel.currentCapture = Capture.builder()
				.startTimeNS(captureStartTime)
				.sections(new ArrayList<>())
				.build();
	}
	
	private static void reset() {
		introScreen.init();
		activeScreen.init();
		inactiveScreen.init();
	}

	private static void stopCapture() {
		ProfilerModel.currentCapture.setDurationNS(System.nanoTime() - ProfilerModel.currentCapture.getStartTimeNS());
		ProfilerModel.captures.add(ProfilerModel.currentCapture);
		ProfilerModel.currentCapture = null;
	}

	public static void render() {
		update();
		
		Shapes.draw(EQRectangle.builder()
				.bounds(ProfilerModel.bounds)
				.color(SkinManager.skinColor)
				.thickness(1)
				.round(true)
				.build());
		
		Shapes.draw(EQRectangle.builder()
				.bounds(ProfilerModel.screenBounds)
				.color(Color.DARK_GRAY)
				.thickness(2)
				.round(true)
				.build());
		
		if (ProfilerModel.currentCapture == null) {
			startButton.render();
		} else {
			stopButton.render();
		}
		
		if (ProfilerModel.currentCapture == null) {
			if (!ProfilerModel.captures.isEmpty()) {
				inactiveScreen.render();
			} else {
				introScreen.render();
			}
		} else {
			activeScreen.render();
		}
	}
	
	public static void update() {
		ProfilerModel.bounds = new Rectangle(horizontalPanelOffset, GraphicsUtil.screenBounds().height - fixedHeight - MetricUtil.getFixedHeight(), GraphicsUtil.screenBounds().width - (2 * horizontalPanelOffset), fixedHeight - 10f);
		ProfilerModel.screenBounds = new Rectangle(ProfilerModel.bounds.x + 10f, ProfilerModel.bounds.y + 30f + 20f, ProfilerModel.bounds.width - 20f, ProfilerModel.bounds.height - 30f - 30f);
		
		startButton.setPosition(ProfilerModel.bounds.x + 10f, ProfilerModel.bounds.y + 10f);
		startButton.setFixedWidth(60f);
		startButton.setFixedHeight(ProfilerModel.buttonHeight);
		
		stopButton.setPosition(startButton.getBounds().x, startButton.getBounds().y);
		stopButton.setFixedWidth(startButton.getFixedWidth());
		stopButton.setFixedHeight(startButton.getFixedHeight());
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
	
	public static void startFrame(String sectionTag) {
		if (ProfilerModel.currentCapture != null) {
			if (currentFrames == null) currentFrames = new HashMap<>();
			
			if (ProfilerModel.currentCapture.getSection(sectionTag) == null) {
				ProfilerModel.currentCapture.getSections().add(Section.builder()
						.tag(sectionTag)
						.frames(new ArrayList<>())
						.build());
			}
			
			Frame currentFrame = currentFrames.get(sectionTag);
			if (currentFrame == null) {
				currentFrame = Frame.builder()
						.checkpoints(new ArrayList<>())
						.build();
				currentFrame.setStartTimeNS(System.nanoTime());
				currentFrames.put(sectionTag, currentFrame);
			}
		}
	}
	
	public static void checkpoint(String sectionTag, String tag) {
		if (ProfilerModel.currentCapture != null) {
			if (currentFrames != null) {
				Frame currentFrame = currentFrames.get(sectionTag);
				if (currentFrame != null) {			
					Section currentSection = ProfilerModel.currentCapture.getSection(sectionTag);
					Checkpoint lastFrameCheckpoint = null;
					if (currentSection != null && !currentSection.getFrames().isEmpty()) {
						for (Checkpoint checkpoint : currentSection.getFrames().get(currentSection.getFrames().size() - 1).getCheckpoints()) {
							if (checkpoint.getTag().equals(tag)) {
								lastFrameCheckpoint = checkpoint;
								break;
							}
						}
					}
					long durationNS = currentFrame.getCheckpoints().isEmpty() ? System.nanoTime() - currentFrame.getStartTimeNS() : System.nanoTime() - currentFrame.getCheckpoints().get(currentFrame.getCheckpoints().size() - 1).getTimeStampNS();
					long minDurationNS = lastFrameCheckpoint == null ? durationNS : Math.min(lastFrameCheckpoint.getMinDurationNS(), durationNS);
					long maxDurationNS = lastFrameCheckpoint == null ? durationNS : Math.max(lastFrameCheckpoint.getMaxDurationNS(), durationNS);
					long totalDurationNS = lastFrameCheckpoint == null ? durationNS : lastFrameCheckpoint.getTotalDurationNS() + durationNS;
					int frames = lastFrameCheckpoint == null ? 1 : lastFrameCheckpoint.getFrames() + 1;
					long avgDurationNS = totalDurationNS / frames;
					currentFrame.getCheckpoints().add(Checkpoint.builder()
							.tag(tag)
							.timeStampNS(System.nanoTime())
							.durationNS(durationNS)
							.minDurationNS(minDurationNS)
							.maxDurationNS(maxDurationNS)
							.avgDurationNS(avgDurationNS)
							.totalDurationNS(totalDurationNS)
							.frames(frames)
							.build());
				}
			}
		}
	}
	
	public static void endFrame(String sectionTag) {
		if (ProfilerModel.currentCapture != null) {
			if (currentFrames != null) {
				if (currentFrames.containsKey(sectionTag)) {
					Section currentSection = ProfilerModel.currentCapture.getSection(sectionTag);
					Frame currentFrame = currentFrames.get(sectionTag);
					currentFrame.setDurationNS(System.nanoTime() - currentFrame.getStartTimeNS());
					
					if (currentSection.getMaxFrameTimeNS() < currentFrame.getDurationNS()) {
						currentSection.setMaxFrameTimeNS(currentFrame.getDurationNS());
						currentSection.setMaxFrame(currentFrame);
					}
					if (currentSection.getMinFrameTimeNS() == 0 || currentSection.getMinFrameTimeNS() > currentFrame.getDurationNS()) {
						currentSection.setMinFrameTimeNS(currentFrame.getDurationNS());
						currentSection.setMinFrame(currentFrame);
					}
					
					if (currentSection.getAvgFrame() == null) currentSection.setAvgFrame(Frame.builder()
							.durationNS(currentFrame.getDurationNS())
							.totalDurationNS(currentFrame.getDurationNS())
							.checkpoints(new ArrayList<>())
							.build());
					
					for (Checkpoint currentFrameCheckpoint : currentFrame.getCheckpoints()) {
						Checkpoint avgCheckpoint = null;
						for (Checkpoint currentAvgCheckpoint : currentSection.getAvgFrame().getCheckpoints()) {
							if (currentAvgCheckpoint.getTag().contentEquals(currentFrameCheckpoint.getTag())) {
								avgCheckpoint = currentAvgCheckpoint;
							}
						}
						if (avgCheckpoint == null) {
							currentSection.getAvgFrame().getCheckpoints().add(currentFrameCheckpoint);
						} else {
							currentSection.getAvgFrame().getCheckpoints().set(currentSection.getAvgFrame().getCheckpoints().indexOf(avgCheckpoint), currentFrameCheckpoint);
						}
					}
					
					
					currentSection.getFrames().add(currentFrame);
					currentFrames.remove(sectionTag);
				}
			}
		}
	}

}
