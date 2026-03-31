package de.instinct.eqfleet.background;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.AccelerometerUtil;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class BackgroundRenderer {
	
	public static float PARALLAX_FACTOR = 0.6f;
	private static float BG_DARKENING_FACTOR = 0.5f;
	
	private static List<Image> layerImages;
	private static EQRectangle bgDarkeningShape;
	
	public static void init() {
		layerImages = new ArrayList<>();
		layerImages.add(new Image(TextureManager.getTexture("ui/image/background", "bg_0")));
		layerImages.add(new Image(TextureManager.getTexture("ui/image/background", "bg_1")));
		layerImages.add(new Image(TextureManager.getTexture("ui/image/background", "bg_2")));
		layerImages.add(new Image(TextureManager.getTexture("ui/image/background", "bg_3")));
		layerImages.add(new Image(TextureManager.getTexture("ui/image/background", "bg_4")));
		
		bgDarkeningShape = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color(0, 0, 0, BG_DARKENING_FACTOR))
				.filled(true)
				.build();
	}
	
	private static void update() {
		float screenWidth = GraphicsUtil.screenBounds().width;
        float screenHeight = GraphicsUtil.screenBounds().height;

        for (int i = 0; i < layerImages.size(); i++) {
            Image layer = layerImages.get(i);
            float parallaxOffsetX = PARALLAX_FACTOR * i;
            float parallaxOffsetY = PARALLAX_FACTOR * i;
            
            if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
            	parallaxOffsetX *= -calculateMouseXParllaxFactor();
                parallaxOffsetY *= -calculateMouseYParllaxFactor();
            } else {
            	parallaxOffsetX *= AccelerometerUtil.getAcceleration().x;
                parallaxOffsetY *= AccelerometerUtil.getAcceleration().y;
            }

            layer.setBounds(
                parallaxOffsetX,
                parallaxOffsetY,
                screenWidth,
                screenHeight
            );
        }
        
        bgDarkeningShape.getBounds().set(0, 0, screenWidth, screenHeight);
	}

	private static float calculateMouseXParllaxFactor() {
		return ((InputUtil.getVirtualMousePosition().x - (GraphicsUtil.screenBounds().width / 2)) / (GraphicsUtil.screenBounds().width / 2)) * 5f;
	}
	
	private static float calculateMouseYParllaxFactor() {
		return ((InputUtil.getVirtualMousePosition().y - (GraphicsUtil.screenBounds().height / 2)) / (GraphicsUtil.screenBounds().height / 2)) * 5f;
	}

	public static void render() {
		update();
		for (Image layer : layerImages) {
			layer.render();
		}
		Shapes.draw(bgDarkeningShape);
    }
	
	public static void dispose() {
		for (Image layer : layerImages) {
			layer.dispose();
		}
	}

}
