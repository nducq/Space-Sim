package Terrestrial.bodies;

public enum PlanetSubType implements SpobSubType{
	ASTEROID_BELT,
	SUPER_TERRESTRIAL,
	TERRESTRIAL,
	DWARF,
	CHUNK,
	JOVIAN,
	SUPER_JOVIAN,
	EMPTY_ORBIT,
	INTERLOPER,
	TROJAN,
	DOUBLE_PLANET,
	CAPTURED_BODY;
	
	public final static double[] minRadii = {
		0,
		1.5,
		0.8,
		0.425,
		0.2,
		3.0,
		10.0,
		0.0,
		0.0,
		0.0,
		0.0
	};
	
	public final static double[] maxRadii = {
		0.0,
		2.5,
		1.2,
		0.65,
		0.4,
		16.5,
		30,
		0.0,
		0.0,
		0.0,
		0.0,
		0.0
	};
	
	public final static double[] odds = {
		0.18,
		0.11,
		0.22,
		0.11,
		0.09,
		0.11,
		0.05,
		0.13,
		0,
		0,
		0,
		0
	};
}
