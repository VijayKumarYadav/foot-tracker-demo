package com.horizon;

public class CoordinateTransformation {

	private double ellipsoid_f = 0.003352810664;
	private double ellipsoid_a = 6378137;
	public double lat;
	public double lon;
	public double RN;
	public double RE;
	
	public void init(double longitude, double latitude){
		this.lat = Math.toRadians(latitude);
		this.lon = Math.toRadians(longitude);
		this.radius(latitude);
	}
	
	private void radius(double latitude){
		double ee2 = fn_ee(ellipsoid_f);
		double sinL = Math.sin(latitude);
		double factor = 1.0 - ee2 * sinL * sinL;
		double sqrt_factor = Math.sqrt(factor);

		//Meridian radius of curvature (confusingly often termed RM)
		this.RN = ( ellipsoid_a * ( 1. - ee2 ) ) / ( factor * sqrt_factor );

		//Transverse radius of curvature (confusingly often termed RN)
		this.RE = ellipsoid_a / sqrt_factor;
	}
	
	private double fn_ee(double f){
	    return ( 2.*f - ( f*f ) );
	}

	public float longitudeToX(double longitude){
		return (float) ((longitude - this.lon) * (this.RE * Math.cos(this.lat)));
	}
	
	public float latitudeToY(double latitude){
		return (float) ((latitude - this.lat) * this.RN);
	}
	
	public double xToLongitude(float x){
		return x / (this.RE * Math.cos(this.lat)) + this.lon;
	}
	
	public double yToLatitude(float y){
		return (double)(y / this.RN + this.lat);
	}
}
