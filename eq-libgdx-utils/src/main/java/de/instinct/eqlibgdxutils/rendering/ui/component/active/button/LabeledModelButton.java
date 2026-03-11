package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledModelButton extends Button {
	
	private ModelPreview modelPreview;
	private Label label;
	private Label noteLabel;
	
	private float labelHeight = 20f;
	private Rectangle modelBounds;
	
	private EQRectangle backgroundShape;
	private EQRectangle hoverShape;
	
	public LabeledModelButton(ModelPreviewConfiguration modelConfig, String labelText, Action action) {
		super();
		super.setAction(action);

		label = new Label(labelText);
		label.setFixedHeight(labelHeight);
		label.setType(FontType.TINY);
		label.setColor(new Color(SkinManager.skinColor));
		
		modelPreview = new ModelPreview(modelConfig);
		Border modelBorder = new Border();
		modelBorder.setColor(new Color(SkinManager.skinColor));
		modelBorder.setSize(2f);
		modelPreview.setBorder(modelBorder);
		modelBounds = new Rectangle();
		
		backgroundShape = EQRectangle.builder()
				.bounds(modelBounds)
				.color(new Color(Color.BLACK))
				.filled(true)
				.build();
		hoverShape = EQRectangle.builder()
				.bounds(modelBounds)
				.color(new Color(SkinManager.skinColor))
				.filled(true)
				.build();
	}
	
	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}
	
	public void setNoteLabel(String noteText, Color color) {
		noteLabel = new Label(noteText);
		noteLabel.setType(FontType.TINY);
		noteLabel.setColor(color);
		noteLabel.setFixedHeight(12f);
		noteLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		noteLabel.setFixedWidth(getBounds().width);
	}
	
	@Override
	protected void updateButton() {
		modelBounds.set(getBounds().x, getBounds().y + labelHeight, getBounds().width, getBounds().height - labelHeight);
		modelPreview.setBounds(modelBounds);
		modelPreview.setAlpha(getAlpha());
		label.setFixedWidth(getBounds().width);
		label.setPosition(getBounds().x, getBounds().y);
		label.setAlpha(getAlpha());
		if (noteLabel != null) {
			noteLabel.setPosition(modelBounds.x + 4, modelBounds.y + 2);
			noteLabel.setAlpha(getAlpha());
		}
		backgroundShape.getColor().a = getAlpha();
		hoverShape.getColor().a = getAlpha() * 0.3f;
	}

	@Override
	protected void renderComponent() {
		Shapes.draw(backgroundShape);
		modelPreview.render();
		label.render();
		if (noteLabel != null) {
			noteLabel.render();
		}
		if (isHovered() || isDown()) {
			Shapes.draw(hoverShape);
		}
	}

	@Override
	public void dispose() {
		modelPreview.dispose();
		label.dispose();
	}

}