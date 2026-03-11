package de.instinct.eqfleet.menu.module.shop.message.types;

import de.instinct.eqfleet.menu.module.shop.message.ShopMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ReloadShopMessage extends ShopMessage {

}
