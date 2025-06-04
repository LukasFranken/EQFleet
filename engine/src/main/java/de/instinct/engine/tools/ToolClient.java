package de.instinct.engine.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.instinct.engine.tools.devrunner.DevWindow;
import de.instinct.engine.tools.mapmaker.MapMaker;

public class ToolClient {
	
	private static DevWindow devWindow;
	private static MapMaker mapMakerWindow;
	
	public static void main(String[] args) {
		devWindow = new DevWindow();
		mapMakerWindow = new MapMaker();
		createClient();
	}

	private static void createClient() {
		JFrame frame = new JFrame("Dev Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(getClientPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
	}

	private static JPanel getClientPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.DARK_GRAY);
		panel.setPreferredSize(new Dimension(200, 200));
		
		JButton devWindowButton = new JButton("Engine Debugger");
		devWindowButton.setBounds(20, 20, 160, 30);
		devWindowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				devWindow.start();
			}
        	
        });
        panel.add(devWindowButton);
        
        JButton mapMakerButton = new JButton("Map Maker");
        mapMakerButton.setBounds(20, 70, 160, 30);
        mapMakerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMakerWindow.start();
			}
        	
        });
        panel.add(mapMakerButton);
		return panel;
	}

}