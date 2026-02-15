package de.instinct.eqlibgdxutils.debug.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private static ColorButton selectButton;
	private static ColorButton upButton;
	private static ColorButton downButton;
	
	private static ColorButton backButton;
	private static ColorButton maxButton;
	private static ColorButton minButton;
	private static ColorButton avgButton;
	
	private static int buttonHeight = 30;
	
	private static List<Capture> captures;
	private static Capture currentCapture;
	private static Map<String, Frame> currentFrames;
	
	private static int selectedSectionIndex;
	private static int hoveredSectionIndex;

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
		
		selectButton = new ColorButton("set");
		selectButton.setConsoleBypass(true);
		selectButton.setAction(new Action() {
			
			@Override
			public void execute() {
				selectedSectionIndex = hoveredSectionIndex;
				statisticsRenderer.setSelectedSection(captures.get(captures.size() - 1).getSections().get(selectedSectionIndex));
			}
			
		});
		
		upButton = new ColorButton("^");
		upButton.setConsoleBypass(true);
		upButton.setAction(new Action() {
			
			@Override
			public void execute() {
				hoveredSectionIndex = Math.max(0, hoveredSectionIndex - 1);
				statisticsRenderer.setHoveredSection(captures.get(captures.size() - 1).getSections().get(hoveredSectionIndex).getTag());
			}
			
		});
		
		downButton = new ColorButton("v");
		downButton.setConsoleBypass(true);
		downButton.setAction(new Action() {
			
			@Override
			public void execute() {
				hoveredSectionIndex = Math.min(captures.get(captures.size() - 1).getSections().size() - 1, hoveredSectionIndex + 1);
				statisticsRenderer.setHoveredSection(captures.get(captures.size() - 1).getSections().get(hoveredSectionIndex).getTag());
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
		
		backButton = new ColorButton("<-");
		backButton.setConsoleBypass(true);
		backButton.setAction(new Action() {
			
			@Override
			public void execute() {
				statisticsRenderer.setSelectedSection(null);
				selectedSectionIndex = -1;
			}
			
		});
	}
	
	private static void capture() {
		selectedSectionIndex = -1;
		hoveredSectionIndex = 0;
		long captureStartTime = System.nanoTime();
		currentCapture = Capture.builder()
				.startTimeNS(captureStartTime)
				.sections(new ArrayList<>())
				.build();
		statisticsRenderer.reset();
	}
	
	private static void stopCapture() {
		currentCapture.setDurationNS(System.nanoTime() - currentCapture.getStartTimeNS());
		for (Section section : currentCapture.getSections()) {
			Frame avgFrame = Frame.builder()
					.checkpoints(getAvgCheckpoints(section.getTag()))
					.durationNS(currentCapture.getDurationNS() / section.getFrames().size())
					.build();
			section.setAvgFrame(avgFrame);
		}
		captures.add(currentCapture);
		statisticsRenderer.setHoveredSection(currentCapture.getSections().get(hoveredSectionIndex).getTag());
		currentCapture = null;
	}
	
	//Requires every frame to have the same checkpoints in the same order
	private static List<Checkpoint> getAvgCheckpoints(String sectionTag) {
		List<Checkpoint> avgCheckpoints = new ArrayList<>();
		Section currentSection = getSection(sectionTag);
		if (currentSection != null) {
			for (Frame frame : currentSection.getFrames()) {
				for (int i = 0; i < frame.getCheckpoints().size(); i++) {
					Checkpoint currentAvgCheckpoint = null;
					for (Checkpoint avgCheckpoint : avgCheckpoints) {
						if (avgCheckpoint.getTag().equals(frame.getCheckpoints().get(i).getTag())) {
							currentAvgCheckpoint = avgCheckpoint;
							break;
						}
					}
					if (currentAvgCheckpoint == null) {
						currentAvgCheckpoint = Checkpoint.builder()
								.tag(frame.getCheckpoints().get(i).getTag())
								.build();
						avgCheckpoints.add(currentAvgCheckpoint);
					}
					currentAvgCheckpoint.setDurationNS(avgCheckpoints.get(i).getDurationNS() + frame.getCheckpoints().get(i).getDurationNS());
					currentAvgCheckpoint.setFrames(currentAvgCheckpoint.getFrames() + 1);
				}
			}
			for (Checkpoint checkpoint : avgCheckpoints) {
				checkpoint.setDurationNS(checkpoint.getDurationNS() / checkpoint.getFrames());
			}
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
				if (selectedSectionIndex == -1) {
					upButton.render();
					downButton.render();
					selectButton.render();
				} else {
					maxButton.render();
					minButton.render();
					avgButton.render();
					backButton.render();
				}
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
		
		selectButton.setFixedWidth(40f);
		selectButton.setPosition(bounds.x + bounds.width - selectButton.getFixedWidth() - 10f, bounds.y + 10f);
		selectButton.setFixedHeight(buttonHeight);
		
		downButton.setFixedWidth(40f);
		downButton.setPosition(selectButton.getBounds().x - downButton.getFixedWidth() - 5f, bounds.y + 10f);
		downButton.setFixedHeight(buttonHeight);
		
		upButton.setFixedWidth(40f);
		upButton.setPosition(downButton.getBounds().x - upButton.getFixedWidth() - 5f, bounds.y + 10f);
		upButton.setFixedHeight(buttonHeight);
		
		maxButton.setFixedWidth(40f);
		maxButton.setPosition(bounds.x + bounds.width - maxButton.getFixedWidth() - 10f, bounds.y + 10f);
		maxButton.setFixedHeight(buttonHeight);
		
		minButton.setFixedWidth(40f);
		minButton.setPosition(maxButton.getBounds().x - minButton.getFixedWidth() - 5f, bounds.y + 10f);
		minButton.setFixedHeight(buttonHeight);
		
		avgButton.setFixedWidth(40f);
		avgButton.setPosition(minButton.getBounds().x - avgButton.getFixedWidth() - 5f, bounds.y + 10f);
		avgButton.setFixedHeight(buttonHeight);
		
		backButton.setFixedWidth(40f);
		backButton.setPosition(avgButton.getBounds().x - backButton.getFixedWidth() - 5f, bounds.y + 10f);
		backButton.setFixedHeight(buttonHeight);
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
		if (currentCapture != null) {
			if (currentFrames == null) currentFrames = new HashMap<>();
			
			if (getSection(sectionTag) == null) {
				currentCapture.getSections().add(Section.builder()
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
		if (currentCapture != null) {
			if (currentFrames != null) {
				Frame currentFrame = currentFrames.get(sectionTag);
				if (currentFrame != null) {
					currentFrame.getCheckpoints().add(Checkpoint.builder()
							.tag(tag)
							.timeStampNS(System.nanoTime())
							.durationNS(currentFrame.getCheckpoints().isEmpty() ? System.nanoTime() - currentFrame.getStartTimeNS() : System.nanoTime() - currentFrame.getCheckpoints().get(currentFrame.getCheckpoints().size() - 1).getTimeStampNS())
							.build());
				}
			}
		}
	}
	
	public static void endFrame(String sectionTag) {
		if (currentCapture != null) {
			if (currentFrames != null) {
				if (currentFrames.containsKey(sectionTag)) {
					Section currentSection = getSection(sectionTag);
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
					
					currentSection.getFrames().add(currentFrame);
					currentFrames.remove(sectionTag);
				}
			}
		}
	}

	private static Section getSection(String sectionTag) {
		Section currentSection = null;
		for (Section section : currentCapture.getSections()) {
			if (section.getTag().equals(sectionTag)) {
				currentSection = section;
				break;
			}
		}
		return currentSection;
	}

}
