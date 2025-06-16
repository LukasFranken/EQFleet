package de.instinct.eqfleet.menu.main;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.modules.ModuleData;
import de.instinct.api.meta.dto.modules.ModuleInfoResponse;

public class MenuModel {
	
	public static volatile boolean active;
	public static volatile Rectangle moduleBounds;
	public static volatile MenuModule activeModule;
	public static volatile ModuleData unlockedModules;
	public static volatile ModuleInfoResponse lockedModules;
	public static volatile boolean moduleChanged;

}
