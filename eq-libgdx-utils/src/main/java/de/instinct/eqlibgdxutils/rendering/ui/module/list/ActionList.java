package de.instinct.eqlibgdxutils.rendering.ui.module.list;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.module.BaseModule;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.draw.TextureDrawMode;
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

	public ActionList() {
		setElements(new ArrayList<>());
		setScrollable(true);
		setElementHeight(40);
		setHoverColor(new Color(0.1f, 0.1f, 0.1f, 1));
		setActiveElementColor(SkinManager.darkerSkinColor);
		setElementBorderColor(SkinManager.darkestSkinColor);
		setDecorated(false);
	}

	@Override
	public void updatePosition() {
		
	}

	@Override
	public void updateContent() {
		float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
		for (ActionListElement element : getElements()) {
			int position = getElements().indexOf(element) + 1;
			if (getDenyHandler() != null) {
	    		Rectangle denyRectangle = new Rectangle(getBounds().x + getBounds().width - 40, getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	if (denyRectangle.contains(mouseX, mouseY)) {
	        		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
	        			getDenyHandler().triggered(element);
	    			}
	        	}
	        }
	        if (getConfirmHandler() != null) {
	        	Rectangle confirmRectangle = new Rectangle(getBounds().x + getBounds().width - (getDenyHandler() == null ? 40 : 80), getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	if (confirmRectangle.contains(mouseX, mouseY)) {
	        		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
	        			getConfirmHandler().triggered(element);
	    			}
	        	}
	        }
	        if (getDenyHandler() == null && getConfirmHandler() == null) {
	        	if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
	        		setActiveElement(element.getValue());
	        		if (getElementClickHandler() != null) {
	        			getElementClickHandler().triggered(element);
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
			
			Rectangle elementRectangle = new Rectangle(getBounds().x, getBounds().y + getBounds().height - (position * getElementHeight()), getBounds().width, getElementHeight());
			
			if (!getBounds().contains(elementRectangle.getX(), elementRectangle.getY())) {
				break;
			}
			
			float mouseX = Gdx.input.getX();
	        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
			
	        if (getActiveElement() != null && element.getValue().contentEquals(getActiveElement())) {
	        	TextureManager.draw(TextureManager.createTexture(getActiveElementColor()), elementRectangle, TextureDrawMode.NORMAL);
	        } else {
	        	if (elementRectangle.contains(mouseX, mouseY)) {
		        	if (getHoverColor() != null) {
		        		TextureManager.draw(TextureManager.createTexture(getHoverColor()), elementRectangle, TextureDrawMode.NORMAL);
		        		renderBorder(elementRectangle);
					}
				}
	        }
	        
	        if (getDenyHandler() != null) {
	    		Rectangle denyRectangle = new Rectangle(getBounds().x + getBounds().width - 40, getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	    		TextureManager.draw(TextureManager.getTexture("ui/image", "x_red"), denyRectangle);
	        	if (denyRectangle.contains(mouseX, mouseY)) {
	        		renderBorder(denyRectangle);
	        	}
	        }
	        if (getConfirmHandler() != null) {
	        	Rectangle confirmRectangle = new Rectangle(getBounds().x + getBounds().width - (getDenyHandler() == null ? 40 : 80), getBounds().y + getBounds().height - (position * getElementHeight()) + (getElementHeight() / 2) - 15, 30, 30);
	        	TextureManager.draw(TextureManager.getTexture("ui/image", "check_red"), confirmRectangle);
	        	if (confirmRectangle.contains(mouseX, mouseY)) {
	        		renderBorder(confirmRectangle);
	        	}
	        }
			Label label = new Label(element.getLabel());
			label.setColor(Color.LIGHT_GRAY);
			label.setBounds(new Rectangle(getBounds().x + 10, getBounds().y + getBounds().height - (position * getElementHeight()), getBounds().width / 2, getElementHeight()));
			label.render();
		}
	}

	private void renderBorder(Rectangle elementRectangle) {
		TextureManager.draw(TextureManager.createTexture(elementBorderColor), new Rectangle(elementRectangle.x, elementRectangle.y, elementRectangle.width, 2));
		TextureManager.draw(TextureManager.createTexture(elementBorderColor), new Rectangle(elementRectangle.x, elementRectangle.y, 2, elementRectangle.height));
		TextureManager.draw(TextureManager.createTexture(elementBorderColor), new Rectangle(elementRectangle.x + elementRectangle.width - 2, elementRectangle.y, 2, elementRectangle.height));
		TextureManager.draw(TextureManager.createTexture(elementBorderColor), new Rectangle(elementRectangle.x, elementRectangle.y + elementRectangle.height - 2, elementRectangle.width, 2));
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
