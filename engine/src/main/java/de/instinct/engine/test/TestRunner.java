package de.instinct.engine.test;

public class TestRunner {
	
	private static final long TICK_RATE_MS = 20;
	
	public static void main(String[] args) {
		TestWindow window = new TestWindow();
		TestEngineManager.init();
		
		long startTime = System.currentTimeMillis();
		while (true) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTime >= TICK_RATE_MS) {
				TestEngineManager.update(currentTime - startTime);
				startTime = currentTime;
				window.render();
			}
		}
	}

}
