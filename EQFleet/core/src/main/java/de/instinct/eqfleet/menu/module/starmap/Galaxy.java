package de.instinct.eqfleet.menu.module.starmap;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;

import de.instinct.api.starmap.dto.GalaxyData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Galaxy {
	
	private GalaxyData data;
	private Decal decal;
	private Vector2 screenPos;

}
