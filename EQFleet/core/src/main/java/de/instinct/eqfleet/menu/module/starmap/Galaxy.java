package de.instinct.eqfleet.menu.module.starmap;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

import de.instinct.api.starmap.dto.GalaxyData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Galaxy {
	
	private GalaxyData data;
	private Decal decal;

}
