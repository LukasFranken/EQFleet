package de.instinct.eqfleet.menu.module.mining.message.types;

import de.instinct.eqfleet.menu.module.mining.message.MiningMenuMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class LoadMiningMenuMessage extends MiningMenuMessage {

}
