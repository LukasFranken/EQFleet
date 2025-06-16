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

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

@SuppressWarnings("rawtypes")
public class MetricUtil {

	private static SpriteBatch batch;

	private static boolean active;

	private static Queue<Metric> metricsAddQueue;
	private static Queue<MetricUpdate> metricsUpdateQueue;
	private static Queue<String> metricsRemoveQueue;

	private static List<Metric> metrics;

	private static final float elementMargin = 4f;
	private static Texture backgroundColorTexture;
	private static float topsidePanelOffset = 30f;
	private static float horizontalPanelOffset = 20f;
	
	private static boolean spanHorizontal;
	
	private static int fixedHeight;
	private static int scrollButtonButtonHeight;
	private static int itemOffset;
	private static FontType panelFontType;
	
	private static ColorButton scrollUpButton;
	private static ColorButton scrollDownButton;

	public static void init() {
		batch = new SpriteBatch();
		metrics = new ArrayList<>();
		metricsAddQueue = new ConcurrentLinkedQueue<>();
		metricsUpdateQueue = new ConcurrentLinkedQueue<>();
		metricsRemoveQueue = new ConcurrentLinkedQueue<>();
		backgroundColorTexture = createBackgroundColorTexture();
		spanHorizontal = true;
		fixedHeight = Gdx.graphics.getHeight();
		scrollButtonButtonHeight = 20;
		itemOffset = 0;
		panelFontType = FontType.SMALL;
		
		scrollUpButton = new ColorButton("up");
		scrollUpButton.setConsoleBypass(true);
		scrollUpButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (itemOffset > 0) {
					itemOffset--;
				}
			}
			
		});
		
		scrollDownButton = new ColorButton("down");
		scrollDownButton.setConsoleBypass(true);
		scrollDownButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (itemOffset + 1 < metrics.size()) {
					itemOffset++;
				}
			}
			
		});
	}

	private static Texture createBackgroundColorTexture() {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
		return texture;
	}
	
	public static void setFixedHeight(int height) {
		fixedHeight = height;
	}

	public static void render() {
		updateMetrics();
		if (active) {
			batch.begin();
			float panelWidth = calculatePanelWidth();
			float panelHeight = fixedHeight == 0 ? elementMargin + (metrics.size() * (FontUtil.getFontHeightPx() + elementMargin)) : fixedHeight;
			Rectangle panelBounds = new Rectangle(0, Gdx.graphics.getHeight() - panelHeight, panelWidth, panelHeight);
			batch.setColor(1f, 1f, 1f, 0.5f);
			batch.draw(backgroundColorTexture, panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
			batch.end();
			for (int i = 0; i < metrics.size(); i++) {
				if (i + itemOffset < metrics.size()) {
					float labelHeight = FontUtil.getFontHeightPx(panelFontType) + elementMargin;
					Rectangle metricBounds = new Rectangle(horizontalPanelOffset, Gdx.graphics.getHeight() - topsidePanelOffset - ((i + 1) * (labelHeight + elementMargin)), calculatePanelWidth() - (horizontalPanelOffset * 2), labelHeight);
					if (metricBounds.y > panelBounds.y) {
						render(metrics.get(i + itemOffset), metricBounds, i + itemOffset);
					}
				}
			}
			renderScrollButtons(panelBounds);
 		}
	}
	
	private static void render(Metric metric, Rectangle metricBounds, int i) {
		Label tagLabel = new Label((i < 10 ? " " : "") + i + ". " + metric.getTag() + ":");
		tagLabel.setColor(Color.WHITE);
		tagLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		tagLabel.setType(panelFontType);
		tagLabel.setBounds(metricBounds);
		tagLabel.render();
		Label valueLabel = new Label(metric.getValueString());
		valueLabel.setColor(Color.WHITE);
		valueLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		valueLabel.setType(panelFontType);
		valueLabel.setBounds(metricBounds);
		valueLabel.render();
	}

	private static void renderScrollButtons(Rectangle panelBounds) {
		scrollUpButton.setPosition(panelBounds.x + horizontalPanelOffset, panelBounds.y);
		scrollUpButton.setFixedWidth((panelBounds.width / 2) - (horizontalPanelOffset * 2));
		scrollUpButton.setFixedHeight(scrollButtonButtonHeight);
		scrollUpButton.render();
		
		scrollDownButton.setPosition(panelBounds.x + (panelBounds.width / 2) + horizontalPanelOffset, panelBounds.y);
		scrollDownButton.setFixedWidth((panelBounds.width / 2) - (horizontalPanelOffset * 2));
		scrollDownButton.setFixedHeight(scrollButtonButtonHeight);
		scrollDownButton.render();
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
		if (spanHorizontal) {
			return Gdx.graphics.getWidth();
		} else {
			float panelWidth = 0;
			for (Metric metric : metrics) {
				panelWidth = Math.max(panelWidth, FontUtil.getFontTextWidthPx(metric.getTag() + ": " + metric.getValueString()));
			}
			return panelWidth + (elementMargin * 2);
		}
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
			Metric metricToRemove = null;
			for (Metric currentMetric : metrics) {
				if (currentMetric.getTag().contentEquals(tag)) {
					metricToRemove = currentMetric;
					break;
				}
			}
			if (metricToRemove != null) metrics.remove(metricToRemove);
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