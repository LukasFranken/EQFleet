
package de.instinct.eqlibgdxutils.rendering.ui.component.passive.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqlibgdxutils.ViewportUtil;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ModelPreview extends Component {

	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 100f);
	private PerspectiveCamera camera;
	
	private GridRenderer gridRenderer;
	
	private float currentRotation = 0f;
    
    private ModelPreviewConfiguration config;

	public ModelPreview(ModelPreviewConfiguration config) {
		super();
		this.config = config;

		camera = new PerspectiveCamera(30, 1, 1);
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 1000f;
		
		if (config.isGrid()) gridRenderer = new GridRenderer(GridConfiguration.builder()
				.step(2)
				.build());
	}

	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	protected float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected void updateElement() {
		camera.viewportWidth = getBounds().width;
		camera.viewportHeight = getBounds().height;
		camera.update();
	}

	@Override
	protected void renderElement() {
		Rectangle bounds = getBounds();
		if (bounds.width <= 0 || bounds.height <= 0) return;

		ViewportUtil.apply(bounds);
		currentRotation += config.getRotationSpeed() * Gdx.graphics.getDeltaTime();
	    Matrix4 modelTransform = new Matrix4();
	    if (config.getBaseRotationAxis().len2() > 0) {
	        modelTransform.rotate(config.getBaseRotationAxis(), config.getBaseRotationAngle());
	    }
	    modelTransform.scale(config.getScale(), config.getScale(), config.getScale());
	    Matrix4 worldRotation = new Matrix4().rotate(Vector3.Y, currentRotation);
	    Matrix4 finalTransform = new Matrix4(worldRotation).mul(modelTransform);
	    config.getModel().transform.set(finalTransform);
	    if (gridRenderer != null) {
	        gridRenderer.drawGrid(camera);
	    }
	    ModelRenderer.render(camera, config.getModel(), getAlpha());
		ViewportUtil.restore();
	}

	@Override
	public void dispose() {
		
	}
}
