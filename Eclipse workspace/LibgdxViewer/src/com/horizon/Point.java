package com.horizon;

public class Point {
	
	public static CoordinateTransformation transformer;

	public float x;
	public float y;
	public float z;
	
	public Point(double lat, double lon, double height){
		x = transformer.longitudeToX(Math.toRadians(lon));
		y = transformer.latitudeToY(Math.toRadians(lat));
		z = (float) height;
	}
	
	public Point(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Double getLongitude(){
		return Math.toDegrees(transformer.xToLongitude(x));
	}
	
	public Double getLatitude(){
		return Math.toDegrees(transformer.yToLatitude(y));
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(Double.toString(x));
		builder.append(",");
		builder.append(Double.toString(y));
		builder.append(",");
		builder.append(Double.toString(z));
		builder.append(")");
		return builder.toString();
	}

}
