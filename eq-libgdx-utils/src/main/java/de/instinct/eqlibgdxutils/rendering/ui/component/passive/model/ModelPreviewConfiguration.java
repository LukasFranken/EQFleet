package de.instinct.eqlibgdxutils.rendering.ui.component.passive.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class ModelPreviewConfiguration {
	
	private ModelInstance model;
	
	private GridConfiguration gridConfig;
	
	@Default
	private float rotationSpeed = 15f;
	
	@Default
	private float baseRotationAngle = 0;
	
	@Default
	private Vector3 baseRotationAxis = new Vector3(0, 0, 0);
	
	@Default
	private float scale = 1f;

}
