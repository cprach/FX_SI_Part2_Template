// Rachael Colley 2014
// Space Invaders Assignment Part 1.
// Provided implementation.

package application;

// Abstract class GameAsset.
// Provides common state and behaviour 
// for Spaceship, Alien and Projectile.

////////////////////////////////////////////
//Implementation provided. No need to alter.
////////////////////////////////////////////

public abstract class GameAsset {
	
	private double width;
	private double height;
	private double currentX;
	private double currentY;
	
	public GameAsset(double width, double height, double currentX,
			double currentY) {
		this.width = width;
		this.height = height;
		this.currentX = currentX;
		this.currentY = currentY;
	}
	
	public abstract double getTravelRate();
	
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getCurrentX() {
		return currentX;
	}
	public void setCurrentX(double currentX) {
		this.currentX = currentX;
	}
	public double getCurrentY() {
		return currentY;
	}
	public void setCurrentY(double currentY) {
		this.currentY = currentY;
	}
	
	

}
