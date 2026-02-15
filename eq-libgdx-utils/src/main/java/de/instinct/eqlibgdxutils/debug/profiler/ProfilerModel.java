package de.instinct.eqlibgdxutils.debug.profiler;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.debug.profiler.model.Capture;
import de.instinct.eqlibgdxutils.debug.profiler.model.TrackedCheckpoint;

public class ProfilerModel {
	
	public static float buttonHeight = 30f;
	
	public static Rectangle bounds;
	public static Rectangle screenBounds;
	
	public static List<Capture> captures;
	public static Capture currentCapture;
	public static List<TrackedCheckpoint> trackedCheckpoints;

}
