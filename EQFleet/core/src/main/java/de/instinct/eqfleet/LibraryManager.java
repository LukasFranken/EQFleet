package de.instinct.eqfleet;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.CursorUtil;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
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
    	CursorUtil.createCursor();
    	ParticleRenderer.init();
        ModelRenderer.init();
        PopupRenderer.init();
        Console.build();
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
				.name("larabie")
				.size(16)
				.build());
		fontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.SMALL)
				.name("larabie")
				.size(12)
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
