package de.instinct.eqlibgdxutils.debug.logging;

public enum ConsoleColor {
	
	DEFAULT("\u001B[0m"),
	BLACK("\u001B[30m"),
	RED("\u001B[31m"),
	GREEN("\u001B[32m"),
	YELLOW("\u001B[33m"),
	BLUE("\u001B[34m"),
	MAGENTA("\u001B[35m"),
	CYAN("\u001B[36m"),
	PINK("\u001B[38;2;255;105;180m"),
	GRAY("\u001b[38;2;150;150;150m");
	
	private String code;
	
	ConsoleColor(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}

}
