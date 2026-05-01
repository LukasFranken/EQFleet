package de.instinct.eqfleet.menu.main;

import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs {
	
	private static volatile List<String> breadcrumbs;
	
	public static void init() {
		breadcrumbs = new ArrayList<>();
	}
	
	public static void add(String breadcrumb) {
		breadcrumbs.add(breadcrumb);
	}
	
	public static void remove() {
		if (!breadcrumbs.isEmpty()) {
			breadcrumbs.remove(breadcrumbs.size() - 1);
		}
	}
	
	public static void change(String breadcrumb) {
		if (!breadcrumbs.isEmpty()) {
			breadcrumbs.set(breadcrumbs.size() - 1, breadcrumb);
		}
	}
	
	public static List<String> getBreadcrumbs() {
		return breadcrumbs;
	}

}
