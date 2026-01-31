package de.instinct.devtools.mapmaker.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MapEditorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final int PANEL_WIDTH = 200;
    private final int PANEL_HEIGHT = 800;
    
    public MapEditorPanel() {
		setLayout(null);
		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
	}

}
