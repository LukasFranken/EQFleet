package de.instinct.devtools.enginerunner.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.instinct.devtools.enginerunner.TestEngineManager;
import de.instinct.engine.model.Player;
import de.instinct.engine.model.planet.Planet;
import de.instinct.engine.model.ship.ShipData;
import de.instinct.engine.order.types.BuildTurretOrder;
import de.instinct.engine.order.types.GamePauseOrder;
import de.instinct.engine.order.types.ShipMovementOrder;

public class ActionPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private final int PANEL_WIDTH = 200;
    private final int PANEL_HEIGHT = 800;
    
    private JComboBox<String> originDropdown;
    private JComboBox<String> targetDropdown;
    private JComboBox<String> shipDropdown;
    private JButton sendButton;
    private JButton buildButton;
    private JButton pauseButton;
    private JButton startButton;
    
    public ActionPanel() {
    	setLayout(null);
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        
        JLabel originLabel = new JLabel("origin:");
        originLabel.setForeground(Color.WHITE);
        originLabel.setBounds(10, 10, 50, 30);
        add(originLabel);
        
        originDropdown = new JComboBox<>();
        originDropdown.setBounds(70, 10, 120, 30);
        add(originDropdown);
        
        JLabel targetLabel = new JLabel("target:");
        targetLabel.setForeground(Color.WHITE);
        targetLabel.setBounds(10, 55, 50, 30);
        add(targetLabel);
        
        targetDropdown = new JComboBox<>();
        targetDropdown.setBounds(70, 55, 120, 30);
        add(targetDropdown);
        
        JLabel shipLabel = new JLabel("ship:");
        shipLabel.setForeground(Color.WHITE);
        shipLabel.setBounds(10, 100, 50, 30);
        add(shipLabel);
        
        shipDropdown = new JComboBox<>();
        shipDropdown.setBounds(70, 100, 120, 30);
        add(shipDropdown);
        
        sendButton = new JButton("send");
        sendButton.setBounds(10, 140, 180, 20);
        sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ShipMovementOrder order = new ShipMovementOrder();
				order.playerId = 1;
				order.playerShipId = Integer.parseInt(((String) shipDropdown.getSelectedItem()).substring(0, 1));
				order.fromPlanetId = Integer.parseInt((String) originDropdown.getSelectedItem());
				order.toPlanetId = Integer.parseInt((String) targetDropdown.getSelectedItem());
				TestEngineManager.queue(order);
			}
        	
        });
        add(sendButton);
        
        buildButton = new JButton("build");
        buildButton.setBounds(10, 180, 180, 20);
        buildButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BuildTurretOrder order = new BuildTurretOrder();
				order.playerId = 1;
				order.planetId = Integer.parseInt((String) originDropdown.getSelectedItem());
				TestEngineManager.queue(order);
			}
        	
        });
        add(buildButton);
        
        pauseButton = new JButton("pause");
        pauseButton.setBounds(10, 745, 180, 20);
        pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GamePauseOrder order = new GamePauseOrder();
				order.playerId = 1;
				order.reason = "Manual pause";
				if (TestEngineManager.state.pauseData.teamPause == 0) {
					order.pause = true;
					pauseButton.setText("resume");
				} else {
					pauseButton.setText("pause");
				}
				TestEngineManager.queue(order);
			}
        	
        });
        add(pauseButton);
        
        startButton = new JButton("start");
        startButton.setBounds(10, 775, 180, 20);
        startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (TestEngineManager.state.started) {
					TestEngineManager.state.started = false;
					startButton.setText("resume");
				} else {
					TestEngineManager.state.started = true;
					startButton.setText("halt");
				}
			}
        	
        });
        add(startButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updatePanel();
    }

	private void updatePanel() {
		List<String> ownPlanets = new ArrayList<>();
		List<String> targetPlanets = new ArrayList<>();
		List<String> ships = new ArrayList<>();
		if (TestEngineManager.state != null) {
			for (Planet planet : TestEngineManager.state.planets) {
				if (planet.ownerId == 1) {
					ownPlanets.add(planet.id + "");
				}
				targetPlanets.add(planet.id + "");
			}
			for (Player player : TestEngineManager.state.players) {
				if (player.id == 1) {
					int i = 0;
					for (ShipData ship : player.ships) {
						ships.add(i + "-" + ship.model);
						i++;
					}
				}
			}
		}
		
		for (int i = 0; i < originDropdown.getItemCount(); i++) {
			if (ownPlanets.contains(originDropdown.getItemAt(i))) {
				ownPlanets.remove(originDropdown.getItemAt(i));
			} else {
				originDropdown.removeItemAt(i);
			}
		}
		for (String item : ownPlanets) originDropdown.addItem(item);
		
		for (int i = 0; i < targetDropdown.getItemCount(); i++) {
			if (targetPlanets.contains(targetDropdown.getItemAt(i))) {
				targetPlanets.remove(targetDropdown.getItemAt(i));
			} else {
				targetDropdown.removeItemAt(i);
			}
		}
		for (String item : targetPlanets) targetDropdown.addItem(item);
		
		for (int i = 0; i < shipDropdown.getItemCount(); i++) {
			if (ships.contains(shipDropdown.getItemAt(i))) {
				ships.remove(shipDropdown.getItemAt(i));
			} else {
				shipDropdown.removeItemAt(i);
			}
		}
		for (String item : ships) shipDropdown.addItem(item);
	}
    
}
