package de.instinct.engine.test;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestWindow {
	
	private GameVisualisationPanel mainPanel;
	private ActionPanel actionPanel;
	
	public TestWindow() {
        JFrame frame = new JFrame("Test Renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        frame.setVisible(true);
    }
	
	public void render() {
		mainPanel.repaint();
		actionPanel.repaint();
	}
	
}