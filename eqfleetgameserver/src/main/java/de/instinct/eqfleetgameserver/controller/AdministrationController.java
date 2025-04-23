package de.instinct.eqfleetgameserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.instinct.eqfleetgameserver.config.ApplicationConfig;
import de.instinct.eqfleetgameserver.service.GameManager;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eqfleet")
@RequiredArgsConstructor
public class AdministrationController {
	
	private final ApplicationConfig applicationConfig;
	
	private final GameManager gameManager;
	
	@GetMapping()
	public ResponseEntity<String> getVersion() {
		return ResponseEntity.ok("v" + applicationConfig.getVersion());
	}
	
	@GetMapping("/start")
	public ResponseEntity<String> start() {
		gameManager.start();
		return ResponseEntity.ok("started");
	}
	
	@GetMapping("/stop")
	public ResponseEntity<String> stop() {
		gameManager.stop();
		return ResponseEntity.ok("stopped");
	}

}
