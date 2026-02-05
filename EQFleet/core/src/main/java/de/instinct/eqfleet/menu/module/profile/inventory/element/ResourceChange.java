package de.instinct.eqfleet.menu.module.profile.inventory.element;

import de.instinct.api.meta.dto.ResourceAmount;
import lombok.Data;

@Data
public class ResourceChange {
	
	private ResourceAmount currentResource;
	private long changeAmount;

}
