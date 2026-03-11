package de.instinct.eqfleet.menu.module.starmap.message.types;

import de.instinct.eqfleet.menu.module.starmap.message.StarmapMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ReloadStarmapMessage extends StarmapMessage {

}