package de.instinct.eqlibgdxutils.debug.profiler.screen.types.inactive;

import java.util.List;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.profiler.ProfilerModel;
import de.instinct.eqlibgdxutils.debug.profiler.model.Capture;
import de.instinct.eqlibgdxutils.debug.profiler.model.Checkpoint;
import de.instinct.eqlibgdxutils.debug.profiler.model.Frame;
import de.instinct.eqlibgdxutils.debug.profiler.model.Section;
import de.instinct.eqlibgdxutils.debug.profiler.model.TrackedCheckpoint;
import de.instinct.eqlibgdxutils.debug.profiler.screen.Screen;
import de.instinct.eqlibgdxutils.debug.profiler.screen.element.ScreenButton;
import de.instinct.eqlibgdxutils.generic.Action;

public class MainInactiveScreen extends Screen {
	
	private ScreenButton selectButton;
	private ScreenButton upButton;
	private ScreenButton downButton;
	
	private ScreenButton backButton;
	private ScreenButton modeButton;
	
	private Capture capture;
	
	public MainInactiveScreen() {
		super();
		selectButton = new ScreenButton(0, "set", new Action() {
			
			@Override
			public void execute() {
				if (ProfilerInactiveMainModel.selectedSectionIndex == -1) {
					ProfilerInactiveMainModel.selectedSectionIndex = ProfilerInactiveMainModel.hoveredSectionIndex;
				} else {
					TrackedCheckpoint trackedCheckpoint = null;
					for (TrackedCheckpoint currentTrackedCheckpoint : ProfilerModel.trackedCheckpoints) {
						if (currentTrackedCheckpoint.getTag().contentEquals(ProfilerInactiveMainModel.hoveredCheckpoint.getTag()) && currentTrackedCheckpoint.getSectionTag().contentEquals(ProfilerInactiveMainModel.selectedSection.getTag())) {
							trackedCheckpoint = currentTrackedCheckpoint;
							break;
						}
					}
					if (trackedCheckpoint == null) {
						ProfilerModel.trackedCheckpoints.add(TrackedCheckpoint.builder()
								.sectionTag(ProfilerInactiveMainModel.selectedSection.getTag())
								.tag(ProfilerInactiveMainModel.hoveredCheckpoint.getTag())
								.build());
					} else {
						ProfilerModel.trackedCheckpoints.remove(trackedCheckpoint);
					}
				}
			}
			
		});
		
		upButton = new ScreenButton(1, "^", new Action() {
			
			@Override
			public void execute() {
				if (ProfilerInactiveMainModel.selectedSectionIndex == -1) {
					ProfilerInactiveMainModel.hoveredSectionIndex = Math.max(0, ProfilerInactiveMainModel.hoveredSectionIndex - 1);
				} else {
					ProfilerInactiveMainModel.hoveredCheckpointIndex = Math.max(0, ProfilerInactiveMainModel.hoveredCheckpointIndex - 1);
				}
			}
			
		});
		
		downButton = new ScreenButton(2, "v", new Action() {
			
			@Override
			public void execute() {
				if (ProfilerInactiveMainModel.selectedSectionIndex == -1) {
					ProfilerInactiveMainModel.hoveredSectionIndex = Math.min(ProfilerModel.captures.get(ProfilerModel.captures.size() - 1).getSections().size() - 1, ProfilerInactiveMainModel.hoveredSectionIndex + 1);
				} else {
					ProfilerInactiveMainModel.hoveredCheckpointIndex = Math.min(getCheckpoints().size() - 1, ProfilerInactiveMainModel.hoveredCheckpointIndex + 1);
				}
			}
			
		});
		
		modeButton = new ScreenButton(3, "mod", new Action() {
			
			@Override
			public void execute() {
				if (ProfilerInactiveMainModel.mode != null) {
					ProfilerInactiveMainModel.hoveredCheckpointIndex = 0;
					switch (ProfilerInactiveMainModel.mode) {
					case MAX:
						ProfilerInactiveMainModel.mode = ProfilerStatisticMode.MIN;
						break;
					case MIN:
						ProfilerInactiveMainModel.mode = ProfilerStatisticMode.AVG;
						break;
					case AVG:
						ProfilerInactiveMainModel.mode = ProfilerStatisticMode.MAX;
						break;
					}
				}
			}
			
		});
		
		backButton = new ScreenButton(4, "<-", new Action() {
			
			@Override
			public void execute() {
				ProfilerInactiveMainModel.selectedSectionIndex = -1;
				ProfilerInactiveMainModel.hoveredCheckpointIndex = 0;
			}
			
		});
	}
	
	@Override
	public void init() {
		ProfilerInactiveMainModel.mode = ProfilerStatisticMode.MAX;
		ProfilerInactiveMainModel.selectedSectionIndex = -1;
		ProfilerInactiveMainModel.hoveredSectionIndex = 0;
		ProfilerInactiveMainModel.selectedSection = null;
		ProfilerInactiveMainModel.hoveredSection = null;
		ProfilerInactiveMainModel.hoveredCheckpointIndex = 0;
		ProfilerInactiveMainModel.hoveredCheckpoint = null;
	}

	@Override
	public void update() {
		capture = ProfilerModel.captures.get(ProfilerModel.captures.size() - 1);
		if (ProfilerInactiveMainModel.selectedSectionIndex != -1) {
			ProfilerInactiveMainModel.selectedSection = capture.getSections().get(ProfilerInactiveMainModel.selectedSectionIndex);
		}
		if (ProfilerInactiveMainModel.hoveredSectionIndex != -1) {
			ProfilerInactiveMainModel.hoveredSection = capture.getSections().get(ProfilerInactiveMainModel.hoveredSectionIndex);
		}
		if (ProfilerInactiveMainModel.selectedSection != null) {
			if (ProfilerInactiveMainModel.hoveredCheckpointIndex != -1) {
				ProfilerInactiveMainModel.hoveredCheckpoint = getCheckpoints().get(ProfilerInactiveMainModel.hoveredCheckpointIndex);
			}
		}
	}

	@Override
	public void renderButtons() {
		upButton.render();
		downButton.render();
		selectButton.render();
		if (ProfilerInactiveMainModel.selectedSectionIndex != -1) {
			modeButton.render();
			backButton.render();
		}
	}

	@Override
	public void renderContent() {
		if (ProfilerInactiveMainModel.selectedSectionIndex == -1) {
			renderHeadline("SECTIONS");
			int i = 0;
			for (Section section : capture.getSections()) {
				 renderStatistic(section.getTag(), "", i);
				 if (ProfilerInactiveMainModel.hoveredSection != null && ProfilerInactiveMainModel.hoveredSection.getTag().equals(section.getTag())) {
					 drawLineHover(i);
				 }
				 i++;
			}
		} else {
			if (ProfilerInactiveMainModel.mode != null) {
				switch (ProfilerInactiveMainModel.mode) {
				case MAX:
					renderHeadline(ProfilerInactiveMainModel.selectedSection.getTag() + " - MAX");
					renderFrameStatistic(ProfilerInactiveMainModel.selectedSection.getMaxFrame());
					break;
				case MIN:
					renderHeadline(ProfilerInactiveMainModel.selectedSection.getTag() + " - MIN");
					renderFrameStatistic(ProfilerInactiveMainModel.selectedSection.getMinFrame());
					break;
				case AVG:
					renderHeadline(ProfilerInactiveMainModel.selectedSection.getTag() + " - AVG");
					renderFrameStatistic(ProfilerInactiveMainModel.selectedSection.getAvgFrame());
					break;
				}
			}
		}
	}

	private void renderFrameStatistic(Frame frame) {
		for (int i = 0; i < frame.getCheckpoints().size(); i++) {
			Checkpoint checkpoint = frame.getCheckpoints().get(i);
			boolean tracked = false;
			for (TrackedCheckpoint trackedCheckpoint : ProfilerModel.trackedCheckpoints) {
				if (trackedCheckpoint.getTag().contentEquals(checkpoint.getTag()) && trackedCheckpoint.getSectionTag().contentEquals(ProfilerInactiveMainModel.selectedSection.getTag())) {
					tracked = true;
					break;
				}
			}
			renderStatistic((tracked ? "# " : "") + checkpoint.getTag(), formatNSTime(checkpoint.getDurationNS()), i);
			if (ProfilerInactiveMainModel.hoveredCheckpoint != null && ProfilerInactiveMainModel.hoveredCheckpoint.getTag().equals(checkpoint.getTag())) {
				 drawLineHover(i);
			}
		}
		renderStatistic("Total", formatNSTime(frame.getDurationNS()), frame.getCheckpoints().size() + 1);
	}
	
	private String formatNSTime(long timeNS) {
		return StringUtils.formatNanoTime(timeNS, 2);
	}
	
	private List<Checkpoint> getCheckpoints() {
		switch (ProfilerInactiveMainModel.mode) {
		case MAX:
			return ProfilerInactiveMainModel.selectedSection.getMaxFrame().getCheckpoints();
		case MIN:
			return ProfilerInactiveMainModel.selectedSection.getMinFrame().getCheckpoints();
		case AVG:
			return ProfilerInactiveMainModel.selectedSection.getAvgFrame().getCheckpoints();
		}
		return null;
	}

}
