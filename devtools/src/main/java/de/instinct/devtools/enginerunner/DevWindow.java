package de.instinct.devtools.enginerunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.instinct.devtools.enginerunner.panel.ActionPanel;
import de.instinct.devtools.enginerunner.panel.GameVisualisationPanel;

public class DevWindow {
	
	private static final int TICK_RATE_MS = 20;
	
	private JFrame frame;
	private GameVisualisationPanel mainPanel;
	private ActionPanel actionPanel;
	private ScheduledExecutorService executor;
	
	public DevWindow() {
        frame = new JFrame("Engine Debugger");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        mainPanel = new GameVisualisationPanel();
        actionPanel = new ActionPanel();
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.add(mainPanel);
        container.add(actionPanel);

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
		TestEngineManager.init();
		frame.setVisible(true);
		executor.scheduleAtFixedRate(() -> {
            TestEngineManager.update(TICK_RATE_MS);
            render();
        }, 0, TICK_RATE_MS, TimeUnit.MILLISECONDS);
	}

	public void render() {
		mainPanel.repaint();
		actionPanel.repaint();
	}
	
}