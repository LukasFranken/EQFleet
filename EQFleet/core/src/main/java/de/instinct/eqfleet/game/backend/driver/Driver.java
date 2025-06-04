package de.instinct.eqfleet.game.backend.driver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import de.instinct.engine.FleetEngine;
import de.instinct.eqfleet.game.GameModel;
import de.instinct.eqfleet.menu.main.Menu;

public abstract class Driver {
	
	protected FleetEngine engine;
	
	private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> logicUpdateTask;
    private long BACKEND_UPDATE_CLOCK_MS = 20;
	
	public Driver() {
		engine = new FleetEngine();
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		    @Override
		    public Thread newThread(Runnable r) {
		        Thread thread = new Thread(r);
		        thread.setUncaughtExceptionHandler((t, e) -> {
		            System.err.println("Unhandled exception in thread " + t.getName() + ": " + e);
		            e.printStackTrace();
		        });
		        return thread;
		    }
		});
	}

	public void start() {
		engine.initialize();
		setup();
		logicUpdateTask = scheduler.scheduleAtFixedRate(() -> {
			preEngineUpdate();
			updateEngine();
			postEngineUpdate();
		}, BACKEND_UPDATE_CLOCK_MS, BACKEND_UPDATE_CLOCK_MS, TimeUnit.MILLISECONDS);
	}

	public abstract void setup();
	
	protected abstract void preEngineUpdate();
	
	protected void updateEngine() {
		if (GameModel.activeGameState != null && GameModel.activeGameState.started) {
    		long currentTime = System.currentTimeMillis();
        	engine.update(GameModel.activeGameState, currentTime - GameModel.lastUpdateTimestampMS);
        	GameModel.lastUpdateTimestampMS = currentTime;
    	}
	}
	
	protected abstract void postEngineUpdate();
	
	public void stop() {
		finish();
		if (logicUpdateTask != null && !logicUpdateTask.isCancelled()) {
			logicUpdateTask.cancel(true);
		}
		scheduler.schedule(() -> {
			
			GameModel.activeGameState = null;
			Menu.open();
			GameModel.active = false;
			
		}, 5000, TimeUnit.MILLISECONDS);
	}
	
	public abstract void finish();
	
	public void dispose() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		cleanup();
	}
	
	public abstract void cleanup();
	
}
