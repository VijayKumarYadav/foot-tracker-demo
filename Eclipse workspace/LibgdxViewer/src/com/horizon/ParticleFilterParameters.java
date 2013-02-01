package com.horizon;

public class ParticleFilterParameters {
	// PROJECTION PARAMETERS
	public static double 	projection_latitude;
	public static double 	projection_longitude;
	
	// BUILDING AND FLOOR
	public static int		buildingId = -1;
	public static int		floor;
	
	// INTIAL POSITION
	public static Point 	startPt;
	public static float 	heading;
	
	// INITIALISATION PARAMETERS
	public static float		positionStd = 1;
	public static float		headingStd = 20;
	public static int		particlesNumber = 250;
	
	// COMPUTATION PARAMETERS
	public static float		stepLengthNoiseStd = 0.1f;
	public static float		stepCOGNoiseStd = 1;
	
	// DISPLAY PARAMETERS
	public static boolean	displayCloudAsMean = true;
	
}