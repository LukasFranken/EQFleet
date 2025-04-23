package de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.EnableConditionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.InputFieldProcessor;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.DefaultTextfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LimitedInputField extends Component {

	private String content;
	private String unfocusedContent;
	private String disabledContent;
	private boolean focused;
	private TextfieldActionHandler action;
	private int maxChars;
	private InputFieldProcessor inputProcessor;
	private TextfieldInputFilter inputFilter;
	private boolean enabled;
	private EnableConditionHandler enableConditionHandler;
	private boolean renderMaxCharsLabel;
	private int maxCharsLabelWidth;
	private float backspaceDownDeleteDelay;
	private float backspaceDownDeletePerSec;

	private float backspaceDownElapsed;
	
	private List<Character> forbiddenCharsMobile;
	private TextInputListener mobileTextInputListener;
	private String popupMessage;
	private String popupHint;

	public LimitedInputField() {
		super();
		Border border = new Border();
		border.setSize(2);
		setBorder(border);

		content = "";
		maxChars = 20;
		enabled = true;
		inputFilter = new DefaultTextfieldInputFilter();
		inputProcessor = new InputFieldProcessor(this);
		renderMaxCharsLabel = true;
		maxCharsLabelWidth = 40;
		backspaceDownDeleteDelay = 0.3f;
		backspaceDownDeletePerSec = 10f;
		popupMessage = "";
		popupHint = "";
		
		createMobileInputListener();
	}

	private void createMobileInputListener() {
		mobileTextInputListener = new TextInputListener() {
			
			@Override
			public void input(String textInput) {
				forbiddenCharsMobile = new ArrayList<>();
				for (char c : textInput.toCharArray()) {
					if (!inputFilter.accept(c)) {
						forbiddenCharsMobile.add(c);
					}
				}
				
				if (forbiddenCharsMobile.isEmpty()) {
					content = textInput;
				}
				
				if (action != null) {
					unfocus();
					action.confirmed();
				}
			}

			@Override
			public void canceled() {
				unfocus();
			}
			
		};
	}

	@Override
	protected float calculateWidth() {
		return (10 * 2) + FontUtil.getFontTextWidthPx(maxChars) + maxCharsLabelWidth;
	}

	@Override
	protected float calculateHeight() {
		return 40;
	}

	@Override
	protected void renderElement() {
		TextureManager.draw(TextureManager.createTexture(Color.BLACK), new Rectangle(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight()), getAlpha());

	    if (enableConditionHandler != null) {
	    	enabled = enableConditionHandler.enabled();
	    	if (!enabled) {
	    		focused = false;
	    	}
	    }
	    FontUtil.draw(getFontColor(), getRenderText(), getBounds().getX() + 10, getBounds().getY() + ((getBounds().getHeight() + FontUtil.getFontHeightPx()) / 2));

	    updateBorderColor();
	    if (renderMaxCharsLabel) {
	    	drawLimit();
	    }
	    if (enabled) {
	    	handleInputFocus();
		    handleTextInput();
    	}
	}

	public void focus() {
		if (!focused && enabled) {
			focused = true;
			((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(inputProcessor);

			if (Gdx.app.getType() == Application.ApplicationType.Android ||
				    Gdx.app.getType() == Application.ApplicationType.iOS) {
					Gdx.input.getTextInput(mobileTextInputListener, popupMessage, content, popupHint);
			}
		}
	}

	public void unfocus() {
		if (focused) {
			focused = false;
			((InputMultiplexer)Gdx.input.getInputProcessor()).removeProcessor(inputProcessor);
		}
	}

	private Color getFontColor() {
		Color fontColor = Color.LIGHT_GRAY;
		if (!focused) {
    		return Color.GRAY;
    	}
    	if (!enabled) {
    		return Color.DARK_GRAY;
    	}
    	fontColor.a = getAlpha();
		return fontColor;
	}

	private String getRenderText() {
		String renderText = content;
	    if (unfocusedContent != null && !focused) {
	    	renderText = unfocusedContent;
	    }
	    if (disabledContent != null && !enabled) {
	    	renderText = disabledContent;
	    }
	    return renderText;
	}

	private void updateBorderColor() {
		 getBorder().setColor(enabled ? (focused ? new Color(DefaultUIValues.skinColor) : Color.GRAY) : Color.DARK_GRAY);
	}

	private void drawLimit() {
		String remainingChars = (maxChars - content.length()) + "";
		TextureManager.draw(TextureManager.createTexture(getBorder().getColor()), new Rectangle(getBounds().x + getBounds().width - maxCharsLabelWidth, getBounds().y, 2,getBounds().height), getAlpha());
		Color fontColor = getBorder().getColor();
		fontColor.a = getAlpha();
	    FontUtil.draw(fontColor, remainingChars, getBounds().x + getBounds().width - (maxCharsLabelWidth - 20) - (FontUtil.getFontTextWidthPx(remainingChars) / 2), getBounds().y + ((getBounds().height + FontUtil.getFontHeightPx()) / 2));
	}

	private void handleInputFocus() {
	    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
	        float mouseX = Gdx.input.getX();
	        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
	        if (getBounds().contains(mouseX, mouseY)) {
	        	focus();
	        } else {
	        	unfocus();
	        }
	    }
	}

	private void handleTextInput() {
		if (focused) {
			if (content.length() > 0 && Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			    	content = content.substring(0, content.length() - 1);
			    }
				backspaceDownElapsed += Gdx.graphics.getDeltaTime();
			    if (backspaceDownElapsed >= backspaceDownDeleteDelay) {
			    	backspaceDownElapsed -= 1f / backspaceDownDeletePerSec;
			    	content = content.substring(0, content.length() - 1);
			    }
			} else {
		    	backspaceDownElapsed = 0f;
		    }

		    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
		    	if (action != null) {
		    		action.confirmed();
		    	}
		    }
		}
	}

	@Override
	public void dispose() {

	}

}
