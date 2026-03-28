package de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.types;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled.LabeledButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledModelButton extends LabeledButton {
	
	private ModelPreview modelPreview;
	
	public LabeledModelButton(ModelPreviewConfiguration modelConfig, String labelText, Action action) {
		super();
		super.setAction(action);
		super.getLabel().setText(labelText);
		modelPreview = new ModelPreview(modelConfig);
		
	}
	
	@Override
	protected void updateContent(Rectangle contentBounds) {
		modelPreview.setBounds(contentBounds);
		modelPreview.setAlpha(getAlpha());
	}
	
	@Override
	protected void setContentBorder(Border modelBorder) {
		modelPreview.setBorder(modelBorder);
	}

	@Override
	protected void renderContent() {
		modelPreview.render();
	}

	@Override
	public void disposeContent() {
		modelPreview.dispose();
	}

}