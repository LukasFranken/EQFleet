package de.instinct.eqlibgdxutils.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

@SuppressWarnings("rawtypes")
public class DebugUtil {

	private static SpriteBatch batch;

	private static boolean active;

	private static Queue<Metric> metricsAddQueue;
	private static Queue<MetricUpdate> metricsUpdateQueue;
	private static Queue<String> metricsRemoveQueue;

	private static List<Metric> metrics;

	private static final float elementMargin = 5f;
	private static Texture backgroundColorTexture;
	private static float topsidePanelOffset = 0f;

	public static void init() {
		batch = new SpriteBatch();
		metrics = new ArrayList<>();
		metricsAddQueue = new ConcurrentLinkedQueue<>();
		metricsUpdateQueue = new ConcurrentLinkedQueue<>();
		metricsRemoveQueue = new ConcurrentLinkedQueue<>();
		backgroundColorTexture = createBackgroundColorTexture();
	}

	private static Texture createBackgroundColorTexture() {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
		return texture;
	}

	public static void render() {
		updateMetrics();
		if (active) {
			batch.begin();
			float panelWidth = calculatePanelWidth();
			float panelHeight = elementMargin + (metrics.size() * (FontUtil.getFontHeightPx() + elementMargin));
			Rectangle panelBounds = new Rectangle(0, Gdx.graphics.getHeight() - topsidePanelOffset - panelHeight, panelWidth, panelHeight);
			batch.setColor(1f, 1f, 1f, 0.2f);
			batch.draw(backgroundColorTexture, panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
			batch.end();
			int i = 1;
			for (Metric metric : metrics) {
				render(metric, i);
				i++;
			}
 		}
	}

	public static void toggle() {
		active = !active;
	}

	public static boolean isActive() {
		return active;
	}

	public static void setTopsidePanelOffset(float offset) {
		topsidePanelOffset = offset;
	}

	private static float calculatePanelWidth() {
		float panelWidth = 0;
		for (Metric metric : metrics) {
			panelWidth = Math.max(panelWidth, FontUtil.getFontTextWidthPx(metric.getTag() + ": " + metric.getValueString()));
		}
		return panelWidth + (elementMargin * 2);
	}

	private static void render(Metric metric, int i) {
		float labelHeight = FontUtil.getFontHeightPx() + elementMargin;
		Label label = new Label(metric.getTag() + ": " + metric.getValueString());
		label.setColor(Color.WHITE);
		label.setBounds(new Rectangle(elementMargin, Gdx.graphics.getHeight() - topsidePanelOffset - (i * labelHeight) - elementMargin, calculatePanelWidth(), labelHeight));
		label.render();
	}

	@SuppressWarnings("unchecked")
	private static void updateMetrics() {
		Metric metric = null;
		while ((metric = metricsAddQueue.poll()) != null) {
			if (!exists(metric)) {
				metrics.add(metric);
			}
		}

		MetricUpdate metricUpdate = null;
		while ((metricUpdate = metricsUpdateQueue.poll()) != null) {
			for (Metric currentMetric : metrics) {
				if (currentMetric.getTag().contentEquals(metricUpdate.getTag())) {
					currentMetric.setValue(metricUpdate.getValue());
				}
			}
		}

		String tag = null;
		while ((tag = metricsRemoveQueue.poll()) != null) {
			for (Metric currentMetric : metrics) {
				if (currentMetric.getTag().contentEquals(tag)) {
					metrics.remove(currentMetric);
				}
			}
		}
	}

	private static boolean exists(Metric metric) {
		for (Metric currentMetric : metrics) {
			if (currentMetric.getTag().contentEquals(metric.getTag())) {
				return true;
			}
		}
		return false;
	}

	public static void register(Metric metric) {
		if (metric != null)
		metricsAddQueue.add(metric);
	}

	public static void update(Metric metric) {
		update(metric.getTag(), metric.getValue());
	}

	public static void update(String tag, Object value) {
		metricsUpdateQueue.add(MetricUpdate.builder().tag(tag).value(value).build());
	}

	public static void remove(String tag) {
		if (tag != null)
		metricsRemoveQueue.add(tag);
	}

	public static void dispose() {
		batch.dispose();
	}

}