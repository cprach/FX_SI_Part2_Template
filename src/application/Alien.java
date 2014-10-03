// Rachael Colley 2014
// Space Invaders Assignment Part 1.
// Provided implementation.

package application;

//Class Alien.
//Provides specialised state and behaviour 
//for Alien.

////////////////////////////////////////////
//Implementation provided. No need to alter.
////////////////////////////////////////////

public class Alien extends GameAsset {
	
	
	
	// Constant attribute.
	private final double TRAVEL_RATE = 0.001;

	
	
	
	public Alien(double width, double height, double currentX,
			double currentY) {
		super(width, height, currentX, currentY);
	}

	
	
	@Override
	public double getTravelRate() {
		return TRAVEL_RATE;
	}
	
	

}
