package de.instinct.eqfleet.background;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.AccelerometerUtil;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class BackgroundRenderer {
	
	public static float PARALLAX_FACTOR = 0.7f;
	
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
				.color(new Color(0, 0, 0, 0.4f))
				.filled(true)
				.build();
	}
	
	private static void update() {
		float screenWidth = GraphicsUtil.screenBounds().width;
        float screenHeight = GraphicsUtil.screenBounds().height;

        if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
            for (Image layer : layerImages) {
                layer.setBounds(0, 0, screenWidth, screenHeight);
            }
        } else {
            for (int i = 0; i < layerImages.size(); i++) {
                Image layer = layerImages.get(i);
                float parallaxOffsetX = AccelerometerUtil.getAcceleration().x * PARALLAX_FACTOR * i;
                float parallaxOffsetY = AccelerometerUtil.getAcceleration().y * PARALLAX_FACTOR * i;

                layer.setBounds(
                    parallaxOffsetX,
                    parallaxOffsetY,
                    screenWidth,
                    screenHeight
                );
            }
        }
        
        bgDarkeningShape.getBounds().set(0, 0, screenWidth, screenHeight);
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
