package de.instinct.eqlibgdxutils.rendering.ui.component.active.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreview;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.model.ModelPreviewConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LabeledModelButton extends Button {
	
	private Texture hoverTexture;
	private Texture modelPreviewBackgroundTexture;
	private ModelPreview modelPreview;
	private Label label;
	private Label noteLabel;
	
	private float labelHeight = 20f;
	
	public LabeledModelButton(ModelPreviewConfiguration modelConfig, String labelText, Action action) {
		super();
		super.setAction(action);
		hoverTexture = TextureManager.createTexture(new Color(SkinManager.skinColor));
		modelPreviewBackgroundTexture = TextureManager.createTexture(new Color(Color.BLACK));
		label = new Label(labelText);
		label.setFixedHeight(labelHeight);
		label.setType(FontType.TINY);
		label.setColor(new Color(SkinManager.skinColor));
		
		modelPreview = new ModelPreview(modelConfig);
		Border modelBorder = new Border();
		modelBorder.setColor(new Color(SkinManager.skinColor));
		modelBorder.setSize(2f);
		modelPreview.setBorder(modelBorder);
	}
	
	@Override
	protected float calculateWidth() {
		return getBounds().width;
	}

	@Override
	protected float calculateHeight() {
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
	protected void renderElement() {
		Rectangle modelBounds = new Rectangle(getBounds().x, getBounds().y + labelHeight, getBounds().width, getBounds().height - labelHeight);
		TextureManager.draw(modelPreviewBackgroundTexture, modelBounds, getAlpha());
		modelPreview.setBounds(modelBounds);
		modelPreview.setAlpha(getAlpha());
		modelPreview.render();
		label.setFixedWidth(getBounds().width);
		label.setPosition(getBounds().x, getBounds().y);
		label.setAlpha(getAlpha());
		label.render();
		if (noteLabel != null) {
			noteLabel.setPosition(modelBounds.x + 4, modelBounds.y + 2);
			noteLabel.setAlpha(getAlpha());
			noteLabel.render();
		}
		if (isHovered() || isDown()) TextureManager.draw(hoverTexture, modelBounds, 0.3f);
	}

	@Override
	public void dispose() {
		modelPreview.dispose();
		TextureManager.dispose(hoverTexture);
		label.dispose();
	}

}