package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.AccelerometerUtil;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.engine.cursor.CursorUtil;
import de.instinct.eqlibgdxutils.engine.cursor.Hotspot;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontTypeConfiguration;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;

public class LibraryManager {
	
	public static void init() {
		Console.init();
    	Console.addCommands(new EQFleetCommandLoader().getCommands());
    	PreferenceManager.init();
    	GraphicsUtil.init(new Vector2(400, 900));
    	TextureManager.init();
    	SkinManager.init();
    	loadFonts();
    	CursorUtil.setCursor("cursor", Hotspot.TOPLEFT);
    	ParticleRenderer.init();
        ModelRenderer.init();
        PopupRenderer.init();
        Console.build();
        AccelerometerUtil.init();
        String mode = PreferenceManager.load("mode");
        if (!mode.contentEquals("")) {
        	GlobalStaticData.mode = ApplicationMode.valueOf(mode);
        }
	}
	
	public static void update() {
		InputUtil.update();
		AccelerometerUtil.update();
	}
	
	private static void loadFonts() {
		List<FontTypeConfiguration> fontTypes = new ArrayList<>();
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.GIANT)
				.name("larabie")
				.size(36)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.LARGE)
				.name("larabie")
				.size(24)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.NORMAL)
				.name("larabie")
				.size(16)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.BOLD)
				.name("source_bold")
				.size(16)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MEDIUM)
				.name("larabie")
				.size(14)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MEDIUM_BOLD)
				.name("source_bold")
				.size(14)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.SMALL)
				.name("larabie")
				.size(11)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.SMALL_BOLD)
				.name("source_bold")
				.size(11)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.TINY)
				.name("source_bold")
				.size(10)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MICRO)
				.name("source")
				.size(7)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MICRO_BOLD)
				.name("source_bold")
				.size(7)
				.build());

		FontUtil.init(FontConfiguration.builder()
				.fontTypes(fontTypes)
				.build());
	}
	
	public static void dispose() {
		TextureManager.dispose();
        ModelRenderer.dispose();
        Console.dispose();
        Shapes.dispose();
	}

}
