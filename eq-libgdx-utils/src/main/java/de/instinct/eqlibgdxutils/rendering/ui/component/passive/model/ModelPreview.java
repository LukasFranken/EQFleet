
package de.instinct.eqlibgdxutils.rendering.ui.component.passive.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.ViewportUtil;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ModelPreview extends Component {

	private GridRenderer gridRenderer;
	
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 100f);
	private PerspectiveCamera camera;
	private float currentRotation = 0f;
    private ModelPreviewConfiguration config;
    
    private Rectangle physicalBounds;
    private Matrix4 modelTransform;
    private Matrix4 worldRotation;
    

	public ModelPreview(ModelPreviewConfiguration config) {
		super();
		this.config = config;

		camera = new PerspectiveCamera(30, 1, 1);
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 1000f;
		
		physicalBounds = new Rectangle();
		modelTransform = new Matrix4();
		worldRotation = new Matrix4();
		
		if (config.getGridConfig() != null) gridRenderer = new GridRenderer(config.getGridConfig());
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	protected void updateComponent() {
		camera.viewportWidth = getBounds().width * GraphicsUtil.getHorizontalDisplayScaleFactor();
		camera.viewportHeight = getBounds().height * GraphicsUtil.getVerticalDisplayScaleFactor();
		camera.update();
		
		if (config.getModel() != null) {
			currentRotation += config.getRotationSpeed() * Gdx.graphics.getDeltaTime();
		    modelTransform.idt();
		    if (config.getBaseRotationAxis().len2() > 0) {
		        modelTransform.rotate(config.getBaseRotationAxis(), config.getBaseRotationAngle());
		    }
		    modelTransform.scale(config.getScale(), config.getScale(), config.getScale());
		    worldRotation.idt();
		    worldRotation.rotate(Vector3.Y, currentRotation);
		    worldRotation.mul(modelTransform);
		    config.getModel().transform.set(worldRotation);
		}
	}

	@Override
	protected void renderComponent() {
		physicalBounds.set(getBounds());
		GraphicsUtil.translateToPhysical(physicalBounds);
		if (physicalBounds.width <= 0 || physicalBounds.height <= 0) return;

		ViewportUtil.apply(physicalBounds);
		if (gridRenderer != null) {
	        gridRenderer.drawGrid(camera);
	    }
		if (config.getModel() != null) {
		    ModelRenderer.render(camera, config.getModel(), getAlpha());
		}
		ViewportUtil.restore();
	}

	@Override
	public void dispose() {
		
	}
}
