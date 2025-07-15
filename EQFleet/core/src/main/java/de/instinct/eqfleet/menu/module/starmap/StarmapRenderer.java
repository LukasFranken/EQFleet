package de.instinct.eqfleet.menu.module.starmap;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.api.starmap.dto.GalaxyData;
import de.instinct.api.starmap.dto.StarsystemData;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.ViewportUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.grid.GridConfiguration;
import de.instinct.eqlibgdxutils.rendering.grid.GridRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.popup.Popup;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class StarmapRenderer extends BaseModuleRenderer {

	private final Rectangle MAP_BOUNDS = new Rectangle(-50f, -50f, 100f, 100f);
	private final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 100f);

	private GridRenderer gridRenderer;

	private List<Galaxy> galaxies;

	private float zoomDuration = 1f;

	private float galaxyZoomFactor;
	private float galaxyZoomElapsed;
	private Galaxy selectedGalaxy;
	private boolean zoomIn;
	
	private float darkeningDuration = 0.5f;

	private PerspectiveCamera camera;

	private DecalBatch decalBatch;
	
	private ColorButton backButton;

	@Override
	public void render() {
		if (StarmapModel.sector != null) {
			ViewportUtil.apply(MenuModel.moduleBounds);
			gridRenderer.drawGrid(camera);
			updateZoom();
			renderSectorMap();
			ViewportUtil.restore();
			if (StarmapModel.selectedGalaxyId != -1) {
				renderGalaxyMap();
			}
			handleInput();
		}
	}

	private void updateZoom() {
		if (selectedGalaxy != null) {
			if (zoomIn) {
				galaxyZoomElapsed += Gdx.graphics.getDeltaTime();
			} else {
				galaxyZoomElapsed -= Gdx.graphics.getDeltaTime();
			}
			galaxyZoomElapsed = MathUtil.clamp(galaxyZoomElapsed, 0, zoomDuration);
			
			galaxyZoomFactor = MathUtil.easeInOut(0, 1, galaxyZoomElapsed / zoomDuration);
		    Vector3 targetPos = new Vector3(BASE_CAM_POS);

		    targetPos.lerp(
		      new Vector3(selectedGalaxy.getData().getMapPosX(), 
		                  selectedGalaxy.getData().getMapPosY(), 
		                  15f),
		      galaxyZoomFactor);
			camera.position.set(targetPos);
			camera.update();
			if (galaxyZoomFactor >= 1f) {
				StarmapModel.selectedGalaxyId = selectedGalaxy.getData().getId();
			}
			if (galaxyZoomFactor <= 0f) {
				StarmapModel.selectedGalaxyId = -1;
				selectedGalaxy = null;
			}
		}
	}

	private void handleInput() {
		if (InputUtil.isClicked() && !PopupRenderer.isActive()) {
			if (StarmapModel.selectedGalaxyId == -1) {
				Galaxy galaxyAtMouse = getClickedGalaxy();
				if (galaxyAtMouse != null) {
					selectedGalaxy = galaxyAtMouse;
					zoomIn = true;
				}
			} else {
				if (zoomIn) {
					StarsystemData starsystemAtMouse = getClickedStarsystem();
					if (starsystemAtMouse != null) {
						createCombatInfoPopup(starsystemAtMouse);
					}
				}
			}
		}
	}

	private void renderSectorMap() {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		for (Galaxy galaxy : galaxies) {
			renderGalaxy(galaxy);
		}
		decalBatch.flush();
		
		if (galaxyZoomElapsed > zoomDuration - darkeningDuration) 
			TextureManager.draw(TextureManager.createTexture(Color.BLACK),
				GraphicsUtil.screenBounds(),
				MathUtil.linear(0, 0.5f, (galaxyZoomElapsed - (zoomDuration - darkeningDuration)) / darkeningDuration));
	}

	private void renderGalaxy(Galaxy galaxy) {
		if (galaxyZoomFactor < 0.05f) {
			Vector3 screenPos = camera.project(new Vector3(galaxy.getData().getMapPosX(), galaxy.getData().getMapPosY(), 0));
			
			Label galaxyNameLabel = new Label(galaxy.getData().getName().toUpperCase());
			galaxyNameLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
			galaxyNameLabel.setType(FontType.SMALL);
			galaxyNameLabel.setFixedWidth(100f);
			galaxyNameLabel.setFixedHeight(20f);
			galaxyNameLabel.setColor(Color.LIGHT_GRAY);
			galaxyNameLabel.setPosition(screenPos.x - (galaxyNameLabel.getFixedWidth() / 2), screenPos.y + 30f);
			galaxyNameLabel.render();
			
			Label galaxyLevelLabel = new Label("Threat: " + getMinThreatLevel(galaxy) + "-" + getMaxThreatLevel(galaxy));
			galaxyLevelLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
			galaxyLevelLabel.setType(FontType.TINY);
			galaxyLevelLabel.setFixedWidth(100f);
			galaxyLevelLabel.setFixedHeight(20f);
			galaxyLevelLabel.setColor(Color.LIGHT_GRAY);
			galaxyLevelLabel.setPosition(screenPos.x - (galaxyNameLabel.getFixedWidth() / 2), screenPos.y - 50f);
			galaxyLevelLabel.render();
		}
		
		Decal decal = galaxy.getDecal();
		decal.lookAt(camera.position, camera.up);
		decalBatch.add(decal);
	}

	private int getMaxThreatLevel(Galaxy galaxy) {
		int maxThreat = Integer.MIN_VALUE;
		for (StarsystemData starsystem : galaxy.getData().getStarsystems()) {
			if (starsystem.getThreatLevel() > maxThreat) {
				maxThreat = starsystem.getThreatLevel();
			}
		}
		return maxThreat;
	}

	private int getMinThreatLevel(Galaxy galaxy) {
		int minThreat = Integer.MAX_VALUE;
		for (StarsystemData starsystem : galaxy.getData().getStarsystems()) {
			if (starsystem.getThreatLevel() < minThreat) {
				minThreat = starsystem.getThreatLevel();
			}
		}
		return minThreat;
	}
	
	private void createCombatInfoPopup(StarsystemData starsystem) {
		float popupWidth = 200;
		ColorButton travelButton = DefaultButtonFactory.colorButton("Combat", new Action() {

			@Override
			public void execute() {
				Starmap.createLobby(selectedGalaxy.getData().getId(), starsystem.getId());
				PopupRenderer.close();
			}

		});
		travelButton.setFixedHeight(30);
		travelButton.setFixedWidth(popupWidth);
		ElementList galaxyInfoElements = new ElementList();
		galaxyInfoElements.setMargin(10);
		galaxyInfoElements.getElements().add(DefaultLabelFactory.createLabelStack("Threat Level:", starsystem.getThreatLevel() + "", popupWidth));
		galaxyInfoElements.getElements().add(travelButton);
		Popup galaxyInfoPopup = Popup.builder()
				.closeOnClickOutside(true)
				.title(starsystem.getName())
				.contentContainer(galaxyInfoElements)
				.build();
		PopupRenderer.create(galaxyInfoPopup);
	}

	private void renderGalaxyMap() {
		if (galaxyZoomFactor == 1f) {
			for (StarsystemData starsystem : selectedGalaxy.getData().getStarsystems()) {
				Image image = new Image(TextureManager.getTexture("ui/image", "starsystem"));
				image.setFixedWidth(20f);
				image.setFixedHeight(20f);
				Vector2 imagePos = getStarsystemScreenPosition(starsystem);
				image.setPosition(imagePos.x - 10f, imagePos.y - 10f);
				image.render();
			}
			backButton.render();
		}
	}

	private Vector2 getStarsystemScreenPosition(StarsystemData starsystem) {
		return MathUtil.translate(new Vector2(starsystem.getMapPosX(), starsystem.getMapPosY()),
				MAP_BOUNDS, 
				new Rectangle(
						MenuModel.moduleBounds.x, 
						MenuModel.moduleBounds.y + (MenuModel.moduleBounds.height / 2) - (MenuModel.moduleBounds.width / 2), 
						MenuModel.moduleBounds.width, 
						MenuModel.moduleBounds.width));
	}

	@Override
	public void reload() {
		backButton = DefaultButtonFactory.colorButton("Back", new Action() {

			@Override
			public void execute() {
				zoomIn = false;
			}
			
		});
		backButton.setFixedHeight(30);
		backButton.setFixedWidth(100);
		backButton.setPosition(
				MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) - (backButton.getFixedWidth() / 2),
				MenuModel.moduleBounds.y + 20);
		
		camera = new PerspectiveCamera(60, MenuModel.moduleBounds.width, MenuModel.moduleBounds.height);
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 1f, 0f);
		camera.near = 1f;
		camera.far = 1000f;
		camera.update();

		gridRenderer = new GridRenderer(GridConfiguration.builder().step(2).build());
		
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

		StarmapModel.selectedGalaxyId = -1;
		galaxyZoomElapsed = 0f;
		galaxyZoomFactor = 0f;
		galaxies = new ArrayList<>();
		if (StarmapModel.sector != null) {
			for (GalaxyData galaxy : StarmapModel.sector.getGalaxies()) {
				galaxies.add(createGalaxy(galaxy));
			}
		}
	}

	private Galaxy createGalaxy(GalaxyData galaxy) {
		Texture galaxyTexture = TextureManager.getTexture("ui/image", "galaxy");
		Decal decal = Decal.newDecal(10, 10, new TextureRegion(galaxyTexture), true);
		decal.setPosition(galaxy.getMapPosX(), galaxy.getMapPosY(), 0);
		return Galaxy.builder().data(galaxy).decal(decal).build();
	}

	private Galaxy getClickedGalaxy() {
		Ray ray = camera.getPickRay(InputUtil.getMouseX(), Gdx.input.getY(), MenuModel.moduleBounds.x, MenuModel.moduleBounds.y,
				MenuModel.moduleBounds.width, MenuModel.moduleBounds.height);

		float t = -ray.origin.z / ray.direction.z;

		Vector3 intersection = new Vector3(ray.origin.x + t * ray.direction.x, ray.origin.y + t * ray.direction.y, 0f);
		Galaxy closest = null;
		float closestDistSq = Float.MAX_VALUE;
		float selectionRadius = 6f;
		float selectionRadiusSq = selectionRadius * selectionRadius;

		for (Galaxy galaxy : galaxies) {
			float dx = galaxy.getData().getMapPosX() - intersection.x;
			float dy = galaxy.getData().getMapPosY() - intersection.y;
			float distSq = dx * dx + dy * dy;

			if (distSq <= selectionRadiusSq && distSq < closestDistSq) {
				closest = galaxy;
				closestDistSq = distSq;
			}
		}

		return closest;
	}
	
	private StarsystemData getClickedStarsystem() {
		float selectionRadius = 20f;
		for (StarsystemData starsystem : selectedGalaxy.getData().getStarsystems()) {
			if (getStarsystemScreenPosition(starsystem).dst(InputUtil.getMouseX(), InputUtil.getMouseY()) < selectionRadius) {
				return starsystem;
			}
		}

		return null;
	}

	@Override
	public void dispose() {
		if (decalBatch != null) {
			decalBatch.dispose();
		}
	}

}
