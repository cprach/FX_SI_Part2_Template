// Rachael Colley 2014
// Space Invaders Assignment Part 1.
// Provided implementation.

package application;

//Class SpaceShip.
//Provides specialised state and behaviour 
//for Spaceship.

////////////////////////////////////////////
//Implementation provided. No need to alter.
////////////////////////////////////////////

public class SpaceShip extends GameAsset {


	// Constant attribute.
	private final double TRAVEL_RATE = 0.01;



	public SpaceShip(double width, double height, double currentX,
			double currentY) {
		super(width, height, currentX, currentY);
	}



	@Override
	public double getTravelRate() {
		return TRAVEL_RATE;
	}





}
