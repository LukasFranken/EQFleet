package de.instinct.eqlibgdxutils.rendering.ui.component.active.button.labeled;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.Button;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class LabeledButton extends Button {
	
	private Label label;
	private Label noteLabel;
	
	private String noteText;
	private Color noteColor;
	
	private Rectangle contentBounds;
	private Border contentBorder;
	
	private EQRectangle backgroundShape;
	private Color backgroundColor;
	
	private EQRectangle hoverShape;
	private Color hoverColor;
	
	private EQRectangle downShape;
	private Color downColor;
	
	public LabeledButton() {
		super();
		contentBounds = new Rectangle();
		label = new Label("");
		label.setType(FontType.TINY);
		label.setColor(new Color(SkinManager.skinColor));
		noteLabel = new Label("");
		noteLabel.setType(FontType.TINY);
		noteLabel.setFixedHeight(12f);
		noteLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		noteLabel.setFixedWidth(getBounds().width);
		
		contentBorder = new Border();
		contentBorder.setColor(new Color(SkinManager.skinColor));
		contentBorder.setSize(2f);
		
		backgroundColor = new Color(Color.BLACK);
		backgroundShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.filled(true)
				.build();
		hoverShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.filled(true)
				.build();
		downShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
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
	
	@Override
	protected void updateButton() {
		setContentBorder(contentBorder);
		label.setBounds(getBounds().x, getBounds().y, getBounds().width, 20f);
		label.setAlpha(getAlpha());
		contentBounds.set(getBounds().x, getBounds().y + label.getBounds().height, getBounds().width, getBounds().height - label.getBounds().height);
		updateContent(contentBounds);
		if (noteText != null) noteLabel.setText(noteText);
		if (noteColor != null) noteLabel.setColor(noteColor);
		if (noteLabel != null) {
			noteLabel.setPosition(contentBounds.x + 4, contentBounds.y + 2);
			noteLabel.setAlpha(getAlpha());
		}
		if (backgroundColor != null) {
			backgroundShape.setBounds(contentBounds);
			backgroundShape.getColor().set(backgroundColor);
			backgroundShape.getColor().a = getAlpha();
		}
		if (hoverColor != null) {
			hoverShape.setBounds(contentBounds);
			hoverShape.getColor().set(hoverColor);
			hoverShape.getColor().a = getAlpha() * 0.2f;
		}
		if (downColor != null) {
			downShape.setBounds(contentBounds);
			downShape.getColor().set(downColor);
			downShape.getColor().a = getAlpha() * 0.5f;
		}
	}

	protected abstract void setContentBorder(Border modelBorder);

	protected abstract void updateContent(Rectangle contentBounds);

	@Override
	protected void renderComponent() {
		if (backgroundColor != null) Shapes.draw(backgroundShape);
		renderContent();
		label.render();
		if (noteLabel != null) {
			noteLabel.render();
		}
		if (isHovered() && hoverColor != null) {
			Shapes.draw(hoverShape);
		}
		if (isDown() && downColor != null) {
			Shapes.draw(downShape);
		}
	}

	protected abstract void renderContent();

	@Override
	public void dispose() {
		label.dispose();
		disposeContent();
	}

	protected abstract void disposeContent();

}
