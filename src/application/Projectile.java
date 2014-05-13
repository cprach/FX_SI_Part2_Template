// Rachael Colley 2014
// Space Invaders Assignment Part 1.
// Provided implementation.

package application;

//Class Projectile.
//Provides specialised state and behaviour 
//for Projectile.

////////////////////////////////////////////
//Implementation provided. No need to alter.
////////////////////////////////////////////

public class Projectile extends GameAsset {
	
	// Constant attributes.
	private final double TRAVEL_RATE = 0.02;
	private final static int PROJECTILE_WIDTH = 3;
	private final static int PROJECTILE_HEIGHT = 3;

	
	
	
	Projectile(double currentX, double currentY) {
		super(PROJECTILE_WIDTH, PROJECTILE_HEIGHT, currentX, currentY);
	}

	
	
	@Override
	public double getTravelRate() {
		return TRAVEL_RATE;
	}

}
