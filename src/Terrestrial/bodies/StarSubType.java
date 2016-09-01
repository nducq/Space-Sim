package Terrestrial.bodies;

public enum StarSubType implements SpobSubType{
	BLUE,
	BLUE_WHITE,
	WHITE,
	YELLOW,
	ORANGE,
	RED,
	BLUE_SUB_GIANT,
	BLUE_WHITE_SUB_GIANT,
	WHITE_SUB_GIANT,
	YELLOW_SUB_GIANT,
	ORANGE_SUB_GIANT,
	BLUE_WHITE_GIANT,
	BLUE_GIANT,
	WHITE_GIANT,
	YELLOW_GIANT,
	ORANGE_GIANT,
	RED_GIANT,
	WHITE_DWARF,
	BROWN_DWARF,
	SPECIAL;
	
	public static final int LUMINOSITY_INDEX = 0;
	public static final int MASS_INDEX = 1;
	public static final int TEMPERATURE_INDEX = 2;
	public static final int RADIUS_INDEX = 3;
	
	public static final double[][][] massLuminosityChart = {
		//Main Sequence Stars:
		{{13000, 17.5, 28000, 4.9}, {7800, 15.1, 25000, 4.8}, {4700, 13.0, 22000, 4.8}, {2800, 11.1, 19000, 4.8}, {1700, 9.5, 17000, 4.8}, {1000, 8.2, 15000, 4.7}, {600, 7.0, 14000, 4.2}, {370, 6.0, 13000, 3.8}, {220, 5.0, 12,000, 3.5}, {130, 4.0, 11000, 3.2}},
		{{80, 3.0, 10000, 3.0}, {62, 2.8, 9750, 2.8}, {48, 2.6, 9500, 2.6}, {38, 2.5, 9250, 2.4}, {29, 2.3, 9000, 2.2}, {23, 2.2, 8750, 2.1}, {18, 2.0, 8500, 2.0}, {14, 1.9, 8250, 1.8}, {11, 1.8, 8000, 1.7}, {8.2, 1.7, 7750, 1.6}},
		{{6.4, 1.6, 7500, 1.5}, {5.5, 1.53, 7350, 1.5}, {4.7, 1.47, 7200, 1.4}, {4.0, 1.42, 7050, 1.4}, {3.4, 1.36, 6900, 1.3}, {2.9, 1.31, 6750, 1.3}, {2.5, 1.26, 6600, 1.2}, {2.16, 1.21, 6450, 1.2}, {1.85, 1.17, 6300, 1.2}, {1.58, 1.12, 6150, 1.1}},
		{{1.36, 1.08, 6000, 1.1}, {1.21, 1.05, 5900, 1.1}, {1.09, 1.02, 5800, 1.0}, {0.98, 0.99, 5700, 1.0}, {0.88, 0.96, 5600, 1.0}, {0.79, 0.94, 5500, 1.0}, {0.71, 0.92, 5400, 1.0}, {0.64, 0.89, 5300, 1.0}, {0.57, 0.87, 5200, 0.9}, {0.51, 0.85, 5100, 0.9}},
		{{0.46, 0.82, 5000, 0.9}, {0.39, 0.79, 4850,0.9}, {0.32, 0.75, 4700, 0.9}, {0.27, 0.72, 4550, 0.8}, {0.23, 0.69, 4400, 0.8}, {0.19, 0.66, 4250, 0.8}, {0.16, 0.63, 4100, 0.8}, {0.14, 0.61, 3950, 0.8}, {0.11, 0.56, 3800, 0.8}, {0.10, 0.49, 3650, 0.8}},
		{{0.08, 0.46, 3500, 0.8}, {0.04, 0.38, 3350, 0.6}, {0.02, 0.32, 3200, 0.5}, {0.012, 0.26, 3050, 0.4}, {0.006, 0.21, 2900, 0.3}, {0.003, 0.18, 2750, 0.25}, {0.0017, 0.15, 2600, 0.2}, {0.0009, 0.12, 2450, 0.17}, {0.0005, 0.10, 2300, 0.14}, {0.0002, 0.08, 2200, 0.11}},
		//Sub Giant Stars:
		{{60000, 20, 27000, 11.3}, {30000, 18, 24000, 10.1}, {15000, 16, 21500, 8.9}, {8000, 14, 19600, 7.8}, {4000, 12, 16700, 7.6}, {2000, 10, 14800, 6.9}, {1500, 9.4, 13800, 6.8}, {1000, 8.6, 12800, 6.5}, {500, 7.8, 11800, 5.4}, {250, 7.0, 10800, 4.6}},
		{{156, 6.0, 9700, 4.5}, {127, 5.1, 9450, 4.2}, {102, 4.6, 9200, 4.0}, {83, 4.3, 8950, 3.8}, {67, 4.0, 8700, 3.6}, {54, 3.7, 8450, 3.5}, {44, 3.4, 8200, 3.3}, {36, 3.1, 7950, 3.2}, {29, 2.9, 7700, 3.1}, {23, 2.7, 7500, 2.9}},
		{{19.0, 2.5, 7300, 2.7}, {16.9, 2.4, 7200, 2.7}, {15.1, 2.3, 7100, 2.6}, {13.4, 2.2, 6950, 2.6}, {12.0, 2.1, 6800, 2.5}, {10.7, 2.0, 6650, 2.5}, {9.5, 1.95, 6500, 2.5}, {8.5, 1.90, 6350, 2.5}, {7.6, 1.85, 6200, 2.4}, {6.7, 1.80, 6050, 2.4}},
		{{6, 1.75, 5900, 2.4}, {5.8, 1.70, 5750, 2.4}, {5.6, 1.65, 5600, 2.5}, {5.4, 0.60, 5450, 2.6}, {5.2, 0.55, 5300, 2.7}, {5.0, 0.50, 5200, 2.8}, {4.8, 0.48, 5100, 2.8}, {4.6, 0.46, 5000, 2.9}, {4.4, 0.44, 4900, 2.9}, {4.2, 0.42, 4800, 3.0}},
		{{4, 1.4, 4700, 3.0}},
		//Giant Stars:
		{{100000, 25, 26000, 15.7}, {55000, 23, 23000, 14.9}, {30000, 21, 21000, 13.2}, {18000, 19, 19200, 12.2}, {10000, 17, 16400, 12.5}, {6500, 15, 14600, 12.7}, {3700, 14, 13600, 11.1}, {1900, 13.5, 12600, 9.2}, {800, 13.0, 11600, 7.1}, {360, 12.5, 10600, 5.7}},
		{{280, 12.0, 9500, 6.2}, {240, 11.5, 9250, 6.1}, {200, 11.0, 9000, 5.9}, {170, 10.5, 8750, 5.7}, {140, 10.0, 8500, 5.6}, {120, 9.6, 8250, 5.5}, {100, 9.2, 8000, 5.3}, {87, 8.9, 7750, 5.2}, {74, 8.6, 7500, 5.1}, {63, 8.3, 7350, 4.9}},
		{{53, 8.0, 7200, 4.7}, {51, 7.0, 7050, 4.8}, {49, 6.0, 6900, 4.9}, {47, 5.2, 6750, 5.1}, {46, 4.7, 6600, 5.2}, {45, 4.3, 6450, 5.4}, {46, 3.9, 6300, 5.7}, {47, 3.5, 6150, 6.1}, {48, 3.1, 6000, 6.5}, {49, 2.8, 5900, 6.8}},
		{{50, 2.5, 5800, 7.1}, {55, 2.4, 5700, 7.7}, {60, 2.5, 5600, 8.3}, {65, 2.5, 5500, 9.0}, {70, 2.6, 5400, 9.7}, {77, 2.7, 5250, 10.7}, {85, 2.7, 5100, 11.9}, {92, 2.8, 4950, 13.2}, {101, 2.8, 4800, 14.7}, {110, 2.9, 4650, 16.3}},
		{{120, 3.0, 4500, 18.2}, {140, 3.3, 4400, 20.4}, {160, 3.6, 4300, 22.8}, {180, 3.9, 4200, 25.6}, {210, 4.2, 4100, 28.8}, {240, 4.5, 4000, 32.4}, {270, 4.8, 3900, 36.5}, {310, 5.1, 3800, 41.2}, {360, 5.4, 3700, 46.5}, {410, 5.8, 3550, 54.0}},
		{{470, 6.2, 3400, 63}, {600, 6.4, 3200, 80}, {900, 6.6, 3100, 105}, {1300, 6.8, 3000, 135}, {1800, 7.2, 2800, 180}, {2300, 7.4, 2650, 230}, {2400, 7.8, 2500, 260}, {2500, 8.3, 2400, 290}, {2600, 8.8, 2300, 325}, {2700, 9.3, 2200, 360}}
	};
	
	public static final double[][] giantMultipliers = {
			{0.3, 0.3},
			{0.4, 0.4},
			{0.5, 0.5},
			{0.6, 0.6},
			{0.7, 0.7},
			{0.8, 0.8},
			{0.9, 0.9},
			{1.0, 1.0},
			{1.25, 1.5}, 
			{1.5, 2.0} 
	};

	public static final double[][] subGiantMultipliers = {
			{0.6, 0.2},	 
			{0.7, 0.4},	 
			{0.8, 0.6},	 
			{0.9, 0.8},	 
			{1.0, 1.0},
			{1.0, 1.0},
			{1.1, 1.2},	 
			{1.2, 1.4},	 
			{1.3, 1.6},	 
			{1.4, 1.8}
	};	
	
	public static final double[][] whiteDwarfMassRadius = {
			{1.3, 0.004},
			{1.1, 0.007},
			{0.9, 0.009},
			{0.7, 0.010},
			{0.6, 0.011},
			{0.55, 0.012},
			{0.50, 0.013},
			{0.45, 0.014},
			{0.40, 0.015},
			{0.35, 0.016}
	};
	
	public static final double[] whiteDwarfTemperature = {
			30000,
			25000,
			20000,
			16000,
			14000,
			12000,
			10000,
			8000,
			6000,
			4000
	};
	
	public static final double[][] brownDwarfMassRadius = {
			{0.070, 0.07},
			{0.064, 0.08},
			{0.058, 0.09},
			{0.052, 0.10},
			{0.046, 0.11},
			{0.040, 0.12},
			{0.034, 0.12},
			{0.026, 0.12},
			{0.020, 0.12},
			{0.014, 0.12}
	};
	
	public static final double[] brownDwarfTemperature = {
			2200,
			2000,
			1800,
			1600,
			1400,
			1200,
			1000,
			900,
			800,
			700
	};	
	public static double getLuminosity(double radius, double temperature){
		return Math.pow(radius, 2) * (Math.pow(temperature, 4) / Math.pow(5800, 4));
	}
	
	/*RED,
	ORANGE,
	YELLOW,
	YELLOW_WHITE,
	WHITE,
	BLUE_WHITE,
	BLUE,
	RED_GIANT,
	SUPER_GIANT;*/
	
	public final static int[] minTemp = {
		2400,
		3700,
		5200,
		6000,
		7500,
		10000,
		30000,
		3000,
		2000
	};
	
	public final static int[] maxTemp = {
		3700,
		5200,
		6000,
		7500,
		10000,
		30000,
		40000,
		5200,
		4100
	};
	
	public final static double radialJitter = 0.2;
	
	public final static double[] minRadii = {
		0.5,
		0.7,
		0.96,
		1.15,
		1.4,
		1.8,
		6.6,
		20.0,
		450.0
	};
	
	public final static double[] maxRadii = {
		0.7,
		0.96,
		1.15,
		1.4,
		1.8,
		6.6,
		12.5,
		45.0,
		1500.0
	};
	
	public final static int[] minPlanets = {0, 2, 6, 4, 3, 4, 3, 3, 2};	
	public final static int[] maxPlanets = {4, 6, 14, 8, 9, 8, 6, 6, 4};
}