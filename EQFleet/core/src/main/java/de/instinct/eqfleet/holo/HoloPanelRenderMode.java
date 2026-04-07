package de.instinct.eqfleet.holo;

public enum HoloPanelRenderMode {
	
	BODY(0),
	GLOW(1);
	
	private int shaderUniformValue;
	
	HoloPanelRenderMode(int shaderUniformValue) {
		this.shaderUniformValue = shaderUniformValue;
	}
	
	public int getShaderUniformValue() {
		return shaderUniformValue;
	}

}
