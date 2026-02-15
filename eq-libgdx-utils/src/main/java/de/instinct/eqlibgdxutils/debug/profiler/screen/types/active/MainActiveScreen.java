package de.instinct.eqlibgdxutils.debug.profiler.screen.types.active;

import java.util.List;

import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.profiler.ProfilerModel;
import de.instinct.eqlibgdxutils.debug.profiler.model.Checkpoint;
import de.instinct.eqlibgdxutils.debug.profiler.model.Frame;
import de.instinct.eqlibgdxutils.debug.profiler.model.TrackedCheckpoint;
import de.instinct.eqlibgdxutils.debug.profiler.screen.Screen;

public class MainActiveScreen extends Screen {

	@Override
	public void init() {
		
	}

	@Override
	protected void update() {
		
	}

	@Override
	protected void renderButtons() {
		
	}

	@Override
	protected void renderContent() {
		renderHeadline("ACTIVE CAPTURE");
		renderStatistic("Frames", "" + (ProfilerModel.currentCapture.getSections().isEmpty() ? 0 : ProfilerModel.currentCapture.getSections().get(0).getFrames().size()), 0);
		renderStatistic("Sections", "" + ProfilerModel.currentCapture.getSections().size(), 1);
		
		if (ProfilerModel.trackedCheckpoints.isEmpty()) {
			renderStatistic("(none)", "", 4);
			
		} else {
			renderExtendedStatistic("Tracked Checkpoints", "MIN", "MAX", "AVG", 3);
			for (int i = 0; i < ProfilerModel.trackedCheckpoints.size(); i++) {
				TrackedCheckpoint trackedCheckpoint = ProfilerModel.trackedCheckpoints.get(i);
				renderTrackedCheckpoint(trackedCheckpoint, i + 4);
			}
		}
	}

	private void renderTrackedCheckpoint(TrackedCheckpoint trackedCheckpoint, int i) {
		if (ProfilerModel.currentCapture.getSection(trackedCheckpoint.getSectionTag()) != null) {
			List<Frame> frames = ProfilerModel.currentCapture.getSection(trackedCheckpoint.getSectionTag()).getFrames(); 
			if (!frames.isEmpty()) {
				for (Checkpoint checkpoint : frames.get(frames.size() - 1).getCheckpoints()) {
					if (trackedCheckpoint.getTag().contentEquals(checkpoint.getTag())) {
						renderExtendedStatistic("# " + trackedCheckpoint.getSectionTag() + " - " + trackedCheckpoint.getTag(),
								StringUtils.formatNanoTime(checkpoint.getMinDurationNS(), 1),
								StringUtils.formatNanoTime(checkpoint.getMaxDurationNS(), 1),
								StringUtils.formatNanoTime(checkpoint.getAvgDurationNS(), 1)
								, i);
					}
				}
			}
		}
	}

}
