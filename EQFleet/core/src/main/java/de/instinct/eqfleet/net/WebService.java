package de.instinct.eqfleet.net;

public enum WebService {
	
	DISCOVERY("discovery"),
	AUTHENTICATION("auth"),
	META("meta");
	
	private String tag;
	
	WebService(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}

}
