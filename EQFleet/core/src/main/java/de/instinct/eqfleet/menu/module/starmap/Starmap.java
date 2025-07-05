package de.instinct.eqfleet.menu.module.starmap;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.Resource;
import de.instinct.api.meta.dto.ResourceAmount;
import de.instinct.api.starmap.dto.GalaxyData;
import de.instinct.api.starmap.dto.SectorData;
import de.instinct.api.starmap.dto.StarsystemData;
import de.instinct.eqfleet.menu.common.architecture.BaseModule;
import de.instinct.eqfleet.menu.main.ModuleMessage;

public class Starmap extends BaseModule {

	@Override
	public MenuModule getMenuModule() {
		return MenuModule.STARMAP;
	}

	@Override
	public void init() {
		List<ResourceAmount> testResourceRewards = new ArrayList<>();
		testResourceRewards.add(ResourceAmount.builder()
				.type(Resource.CREDITS)
				.amount(500)
				.build());
		testResourceRewards.add(ResourceAmount.builder()
				.type(Resource.METAL)
				.amount(500)
				.build());
		testResourceRewards.add(ResourceAmount.builder()
				.type(Resource.CRYSTALS)
				.amount(500)
				.build());
		
		StarsystemData testStarSystem = StarsystemData.builder()
				.id(0)
				.name("Teststarsystem")
				.mapPosX(-20)
				.mapPosY(-20)
				.planets(6)
				.ancientPoints(50)
				.threatLevel(1)
				.conquered(false)
				.resourceRewards(testResourceRewards)
				.build();
		
		GalaxyData testGalaxy = GalaxyData.builder()
				.id(0)
				.name("Testgalaxy")
				.mapPosX(0)
				.mapPosY(0)
				.starsystems(new ArrayList<>())
				.build();
		testGalaxy.getStarsystems().add(testStarSystem);
		
		GalaxyData testGalaxy2 = GalaxyData.builder()
				.id(1)
				.name("Testgalaxy2")
				.mapPosX(20)
				.mapPosY(20)
				.starsystems(new ArrayList<>())
				.build();
		testGalaxy2.getStarsystems().add(testStarSystem);

		StarmapModel.sector = SectorData.builder()
				.galaxies(new ArrayList<>())
				.build();
		StarmapModel.sector.getGalaxies().add(testGalaxy);
		StarmapModel.sector.getGalaxies().add(testGalaxy2);
		
		StarmapModel.selectedGalaxyId = -1;
		StarmapModel.selectedStarsystemId = -1;
	}

	@Override
	public void open() {

	}

	@Override
	public void update() {

	}

	@Override
	public boolean process(ModuleMessage message) {
		return false;
	}

}
