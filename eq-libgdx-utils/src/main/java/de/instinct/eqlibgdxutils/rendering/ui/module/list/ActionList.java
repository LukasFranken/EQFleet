package de.instinct.eqlibgdxutils.rendering.ui.module.list;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.BaseModule;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureDrawMode;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ActionList extends BaseModule {

	private List<ActionListElement> elements;
	private String activeElement;
	private boolean scrollable;
	private ListActionHandler elementClickHandler;
	private ListActionHandler confirmHandler;
	private ListActionHandler denyHandler;
	private Color elementBorderColor;
	private Color hoverColor;
	private Color activeElementColor;
	private int elementHeight;
	
	private EQRectangle elementBorderShape;
	private Rectangle workingRectangle;
	
	private Label elementLabel;

	public ActionList() {
		setElements(new ArrayList<>());
		setScrollable(true);
		setElementHeight(30);
		setHoverColor(new Color(0.1f, 0.1f, 0.1f, 1));
		setActiveElementColor(SkinManager.darkestSkinColor);
		setElementBorderColor(SkinManager.darkestSkinColor);
		setDecorated(false);
		
		elementBorderShape = EQRectangle.builder()
				.color(getElementBorderColor())
				.thickness(1f)
				.bounds(new Rectangle())
				.build();
		
		workingRectangle = new Rectangle();
		
		elementLabel = new Label("");
		elementLabel.setColor(Color.LIGHT_GRAY);
		elementLabel.setBounds(new Rectangle());
	}

	@Override
	public void updatePosition() {
		
	}

	@Override
	public void updateContent() {
		float mouseX = InputUtil.getVirtualMousePosition().x;
        float mouseY = InputUtil.getVirtualMousePosition().y;
		for (ActionListElement element : getElements()) {
			int position = getElements().indexOf(element) + 1;
			if (getDenyHandler() != null) {
				workingRectangle.set(getBounds().x + getBounds().width - 40, getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	if (workingRectangle.contains(mouseX, mouseY)) {
	        		if (InputUtil.isClicked()) {
	        			getDenyHandler().triggered(element);
	    			}
	        	}
	        }
	        if (getConfirmHandler() != null) {
	        	workingRectangle.set(getBounds().x + getBounds().width - (getDenyHandler() == null ? 40 : 80), getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	if (workingRectangle.contains(mouseX, mouseY)) {
	        		if (InputUtil.isClicked()) {
	        			getConfirmHandler().triggered(element);
	    			}
	        	}
	        }
	        if (getDenyHandler() == null && getConfirmHandler() == null) {
	        	workingRectangle.set(getBounds().x, getBounds().y + getBounds().height - (position * getElementHeight()), getBounds().width, getElementHeight());
	        	if (workingRectangle.contains(mouseX, mouseY)) {
	        		if (InputUtil.isClicked()) {
		        		setActiveElement(element.getValue());
		        		if (getElementClickHandler() != null) {
		        			getElementClickHandler().triggered(element);
		        		}
		        	}
	        	}
	        }
		}
		
	}

	@Override
	public void updateContentPosition() {
		
	}

	@Override
	protected void renderContent() {
		renderListElements();
	}
	
	private void renderListElements() {
		for (ActionListElement element : getElements()) {
			int position = getElements().indexOf(element) + 1;
			
			workingRectangle.set(getBounds().x, getBounds().y + getBounds().height - (position * getElementHeight()), getBounds().width, getElementHeight());
			
			if (!getBounds().contains(workingRectangle.getX(), workingRectangle.getY())) {
				break;
			}
			
			float mouseX = InputUtil.getVirtualMousePosition().x;
	        float mouseY = InputUtil.getVirtualMousePosition().y; 
			
	        if (getActiveElement() != null && element.getValue().contentEquals(getActiveElement())) {
	        	TextureManager.draw(TextureManager.createTexture(getActiveElementColor()), workingRectangle, TextureDrawMode.NORMAL);
	        } else {
	        	if (workingRectangle.contains(mouseX, mouseY)) {
		        	if (getHoverColor() != null) {
		        		TextureManager.draw(TextureManager.createTexture(getHoverColor()), workingRectangle, TextureDrawMode.NORMAL);
		        		renderBorder(workingRectangle);
					}
				}
	        }
	        
	        if (getDenyHandler() != null) {
	        	workingRectangle.set(getBounds().x + getBounds().width - 40, getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	    		TextureManager.draw(TextureManager.getTexture("ui/image", "x_red"), workingRectangle);
	        	if (workingRectangle.contains(mouseX, mouseY)) {
	        		renderBorder(workingRectangle);
	        	}
	        }
	        if (getConfirmHandler() != null) {
	        	workingRectangle.set(getBounds().x + getBounds().width - (getDenyHandler() == null ? 40 : 80), getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	TextureManager.draw(TextureManager.getTexture("ui/image", "check_red"), workingRectangle);
	        	if (workingRectangle.contains(mouseX, mouseY)) {
	        		renderBorder(workingRectangle);
	        	}
	        }
	        elementLabel.setText(element.getLabel());
	        elementLabel.setBounds(getBounds().x + 10, getBounds().y + getBounds().height - (position * getElementHeight()), getBounds().width / 2, getElementHeight());
	        elementLabel.render();
		}
	}

	private void renderBorder(Rectangle elementRectangle) {
		elementBorderShape.getBounds().set(elementRectangle);
		elementBorderShape.getColor().set(getElementBorderColor());
		Shapes.draw(elementBorderShape);
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
	public void dispose() {
		
	}

}
