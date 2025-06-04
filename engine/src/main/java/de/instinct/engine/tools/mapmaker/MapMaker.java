package de.instinct.engine.tools.mapmaker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.instinct.engine.tools.mapmaker.panel.MapEditorPanel;
import de.instinct.engine.tools.mapmaker.panel.MapViewPanel;

public class MapMaker {
	
private static final int TICK_RATE_MS = 20;
	
	private MapViewPanel viewPanel;
	private MapEditorPanel editorPanel;
	private JFrame frame;
	private ScheduledExecutorService executor;
	
	public MapMaker() {
        frame = new JFrame("Map Maker");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        viewPanel = new MapViewPanel();
        editorPanel = new MapEditorPanel();
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.add(viewPanel);
        container.add(editorPanel);

        frame.setContentPane(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }
	
	public void start() {
		if (executor != null) {
			executor.shutdownNow();
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		frame.setVisible(true);
		executor.scheduleAtFixedRate(() -> {
            render();
        }, 0, TICK_RATE_MS, TimeUnit.MILLISECONDS);
	}

	public void render() {
		viewPanel.repaint();
		editorPanel.repaint();
	}
	
}
