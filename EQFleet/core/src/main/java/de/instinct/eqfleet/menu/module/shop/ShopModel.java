package de.instinct.eqfleet.menu.module.shop;

import java.util.Queue;

import de.instinct.api.shop.dto.BuyResponse;
import de.instinct.api.shop.dto.PlayerShopData;
import de.instinct.api.shop.dto.ShopData;
import de.instinct.eqfleet.menu.module.shop.message.ShopMessage;

public class ShopModel {
	
	public static volatile Queue<ShopMessage> messageQueue;
	
	public static volatile boolean dataUpdated;
	public static volatile ShopData shopData;
	public static volatile PlayerShopData playerShopData;
	public static volatile BuyResponse buyResponse;

}
