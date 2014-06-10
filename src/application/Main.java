// Rachael Colley 2014
// Space Invaders Assignment Part 1.
// Provided implementation.

// Your name and student number here <-- <-- <--

package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;


public class Main extends Application {


	// Attributes of this class.
	private final String MY_NAME_AND_STUDENT_NUMBER = "Rachael Colley 007"; // <-- change this to you <-- <-- <--
	private FlowPane rootLayout;
	private Canvas drawingCanvas;
	private GraphicsContext graphicsObject;
	private Scene scene;

	private final double CANVAS_WIDTH = 500;
	private final double CANVAS_HEIGHT = 500;

	private final int GAME_ASSET_WIDTH_DIVISOR = 20;
	private final double GAME_ASSET_HEIGHT_PERCENTAGE = 0.75;

	private SpaceShip spaceShip;
	private Alien[] alienList;
	private ArrayList<Projectile> projectileList;
	private int playerScore;

	// used by aliens and spaceship.
	private double gameAssetWidth;
	private double gameAssetHeight;

	// new
	private final int TOTAL_NUM_OF_ALIENS = 15;
	private final int NUM_OF_ALIENS_ON_ROW = 5;
	private double assetSpacerWidth; // The space between each alien.
	private double assetSpacerHeight; // The space between each row of aliens.
	private double fleetTravelRate; 
	private String fleetDirection;
	private boolean gameOver;



	@Override
	public void start(Stage primaryStage) {
		// Implementation provided. No need to alter.
		try {
			rootLayout = new FlowPane();
			drawingCanvas = new Canvas(CANVAS_WIDTH,CANVAS_HEIGHT);
			graphicsObject = drawingCanvas.getGraphicsContext2D();
			rootLayout.getChildren().add(drawingCanvas);
			scene = new Scene(rootLayout,CANVAS_WIDTH,CANVAS_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			initialise(); 
			gameLoop(); 

			primaryStage.setTitle(MY_NAME_AND_STUDENT_NUMBER);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}




	public void initialise() {
		// Implementation provided. No need to alter.
		playerScore = 0;
		gameAssetWidth = 0;
		gameAssetHeight = 0;
		assetSpacerWidth = 0;
		assetSpacerHeight = 0;
		fleetTravelRate = 0;
		fleetDirection = "east"; 
		gameOver = false; 
		initialiseGameAssetDimensions();
		initialiseSpaceShipPosition();
		initialiseAlienFleetPosition();
		initialiseProjectileList();
		addEventHandlers();
	}




	public void initialiseGameAssetDimensions() {
		// Part 1.
		gameAssetWidth = CANVAS_WIDTH / GAME_ASSET_WIDTH_DIVISOR;
		gameAssetHeight = GAME_ASSET_HEIGHT_PERCENTAGE * gameAssetWidth;
	}




	public void initialiseSpaceShipPosition() {
		// Part 1.
		double middleCoordinate = makeMiddleXCoord();
		double x = middleCoordinate - (gameAssetWidth / 2);
		double y = CANVAS_HEIGHT - (gameAssetHeight * 2);
		spaceShip = new SpaceShip(gameAssetWidth, gameAssetHeight, x,y);
	}




	public double makeMiddleXCoord() {
		// Part 1.
		return CANVAS_WIDTH / 2;
	}





	public void initialiseAlienFleetPosition() {
		// Required.

		double axisLength = CANVAS_WIDTH;
		double remainingWidthOnX = axisLength - (gameAssetWidth * NUM_OF_ALIENS_ON_ROW);
		assetSpacerWidth = remainingWidthOnX / (NUM_OF_ALIENS_ON_ROW + 1);
		assetSpacerHeight = gameAssetHeight;
		double fleetStartX = assetSpacerWidth;
		double fleetStartY = gameAssetHeight;
		alienList = new Alien[TOTAL_NUM_OF_ALIENS];

		int alienCount = 0;
		int aliensOnRowCount = 0;
		double currentX = fleetStartX;
		double currentY = fleetStartY;

		while (alienCount < TOTAL_NUM_OF_ALIENS) {

			Alien a = new Alien(gameAssetWidth, gameAssetHeight, currentX, currentY);
			alienList[alienCount] = a;
			currentX += gameAssetWidth + assetSpacerWidth;
			aliensOnRowCount ++;
			alienCount ++;

			if (aliensOnRowCount > NUM_OF_ALIENS_ON_ROW - 1) {

				aliensOnRowCount = 0;
				currentX = fleetStartX;
				currentY += assetSpacerHeight * 2;

			}

		}

	}




	public void initialiseProjectileList() { 
		// Implementation provided. No need to alter.
		projectileList = new ArrayList<Projectile>();
	}




	public void gameLoop() {
		// Implementation provided. No need to alter.
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {

				draw();
				update();
				checkForCollision();
				isGameOver();
				//placeHolderAnimation(); // <-- REMOVE THIS LINE <-- <--
			}

		};

		timer.start();
	}




	public void draw() {
		// Implementation provided. No need to alter.
		graphicsObject.setFill(Color.BLACK);
		graphicsObject.fillRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
		drawSpaceShip();
		drawAliens();
		drawProjectiles();
	}




	public void update() {
		// Implementation provided. No need to alter.
		updateAlienFleetPosition();
		updateProjectiles();
	}




	public void checkForCollision() {
		// Implementation provided. No need to alter.
		detectProjectileCollisionWithAlien();
		detectAlienCollisionWithSpaceship();
	}




	public void drawSpaceShip() {
		// Part 1.
		graphicsObject.setFill(Color.WHITE);
		graphicsObject.fillRect(spaceShip.getCurrentX(), spaceShip.getCurrentY(), spaceShip.getWidth(), spaceShip.getHeight());
	}




	public void drawAliens() {
		// Required.

		graphicsObject.setFill(Color.CHARTREUSE);
		for (int x = 0; x < alienList.length; x ++ ) {
			Alien currentAlien = alienList[x];
			if (currentAlien != null) {
				graphicsObject.fillRect(currentAlien.getCurrentX(), 
						currentAlien.getCurrentY(), 
						currentAlien.getWidth(), 
						currentAlien.getHeight());
			}
		}
		fleetTravelRate = alienList[0].getTravelRate();
	}




	public void drawProjectiles() {
		// Implementation provided. No need to alter.
		graphicsObject.setFill(Color.RED);
		for (int x = 0; x < projectileList.size(); x ++ ) {
			Projectile currentProjectile = projectileList.get(x);
			graphicsObject.fillRect(currentProjectile.getCurrentX(), 
					currentProjectile.getCurrentY(), 
					currentProjectile.getWidth(), 
					currentProjectile.getHeight());
		}
	}





	public void moveFleetEast(double travelIncrement) {
		// Required.
		for (int x = 0; x < alienList.length; x++) {
			if (alienList[x] != null) {
				double updatedX = alienList[x].getCurrentX() + travelIncrement;
				alienList[x].setCurrentX(updatedX);
			}
		}
	}





	public void moveFleetWest(double travelIncrement) {
		// Required.
		for (int x = 0; x < alienList.length; x++) {
			if (alienList[x] != null) {
				double updatedX = alienList[x].getCurrentX() - travelIncrement;
				alienList[x].setCurrentX(updatedX);
			}
		}
	}





	public void moveFleetSouth() {
		// Required.
		for (int x = 0; x < alienList.length; x++) {
			if (alienList[x] != null) {
				double updatedY = alienList[x].getCurrentY() + (gameAssetHeight / 2);
				alienList[x].setCurrentY(updatedY);
			}
		}
	}





	public void updateAlienFleetPosition() {
		// Required.
		double axisLength = CANVAS_WIDTH;
		double travelIncrement = fleetTravelRate * axisLength;

		//// East
		if (fleetDirection.equals("east")) {
			double mostEast = 0;

			for (int x = 0; x < alienList.length; x ++ ) {
				Alien currentAlien = alienList[x];

				if (currentAlien != null) {

					if (currentAlien.getCurrentX() > mostEast) {
						mostEast = currentAlien.getCurrentX();
					}

				}

			}



			double distanceEast = axisLength - mostEast;
			if ((mostEast + gameAssetWidth) + travelIncrement > CANVAS_WIDTH) {

				travelIncrement = distanceEast - gameAssetWidth;
				fleetDirection = "west";
				moveFleetSouth();

			}

			moveFleetEast(travelIncrement);
		}

		//// West
		if (fleetDirection.equals("west")) {
			double mostWest = CANVAS_WIDTH;

			for (int x = 0; x < alienList.length; x ++ ) {
				Alien currentAlien = alienList[x];

				if (currentAlien != null) {

					if (currentAlien.getCurrentX() < mostWest) {
						mostWest = currentAlien.getCurrentX();
					}

				}

			}


			double distanceWest = mostWest;
			if (mostWest - travelIncrement < 0) {

				travelIncrement = distanceWest;
				fleetDirection = "east";
				moveFleetSouth();

			}

			moveFleetWest(travelIncrement);

		}

	}




	public void updateProjectiles() {
		// No need to alter.
		Iterator<Projectile> i = projectileList.iterator();
		while (i.hasNext()) {
			Projectile currentProjectile = i.next();
			double axisLength = CANVAS_HEIGHT;
			double travelIncrement = currentProjectile.getTravelRate() * axisLength;
			currentProjectile.setCurrentY(currentProjectile.getCurrentY() - travelIncrement);
			if (currentProjectile.getCurrentY() <= 0) {
				i.remove();
				System.out.println("projectileList size: " + projectileList.size());
			} 
		}
	}




	public void addEventHandlers() {
		// Part 1.
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent ke) {

				if (ke.getCode() == KeyCode.SPACE) {
					System.out.println("Shoot from here");
					// Here...
					fireProjectile();
				} else if (ke.getCode() == KeyCode.RIGHT) {
					System.out.println("Move east from here");
					updateSpaceShipPosition("east");
					// Here...
				} else if (ke.getCode() == KeyCode.LEFT) {
					System.out.println("Move west from here");
					updateSpaceShipPosition("west");
					// Here...
				} 

			}

		});

	}





	public boolean checkForWestEdge(double currentXCoord) {
		// Part 1.
		boolean detected = false;
		if (currentXCoord <= 0) {
			detected = true;
		}
		return detected;
	}





	public boolean checkForEastEdge(double currentXCoord, double widthToCheck) {
		// Part 1.
		boolean detected = false;
		if (currentXCoord  + widthToCheck >= CANVAS_WIDTH) {
			detected = true;
		}
		return detected;
	}





	public void updateSpaceShipPosition(String direction) {
		// Part 1.

		double axisLength = CANVAS_WIDTH;
		double travelIncrement = spaceShip.getTravelRate() * axisLength;
		double updatedX = 0;

		if (direction.equals("west")) {
			updatedX = spaceShip.getCurrentX() - travelIncrement;
			if (checkForWestEdge(updatedX)) {
				spaceShip.setCurrentX(0);
			} else {
				spaceShip.setCurrentX(updatedX);
			}
		}

		if (direction.equals("east")) {
			updatedX = spaceShip.getCurrentX() + travelIncrement;
			if (checkForEastEdge(updatedX, spaceShip.getWidth())) {
				spaceShip.setCurrentX(CANVAS_WIDTH - spaceShip.getWidth());
			} else {
				spaceShip.setCurrentX(updatedX);
			}
		}
	}





	public void fireProjectile() {
		// Part 1.
		addNewProjectile(spaceShip.getCurrentX() + (gameAssetWidth / 2), spaceShip.getCurrentY());
	}




	public void addNewProjectile(double x, double y) {
		// Implementation provided. No need to alter.
		projectileList.add(new Projectile(x,y));
		System.out.println("projectileList size: " + projectileList.size());
	}




	public void updatePlayerScore() {
		// Required.
		playerScore ++;

		System.out.println("Player score: " + playerScore);

		if (playerScore == TOTAL_NUM_OF_ALIENS) {
			gameOver = true;
		}

	}





	public void detectProjectileCollisionWithAlien() {
		// Required.
		for (int x = 0; x < alienList.length; x ++ ) {
			Alien currentAlien = alienList[x];

			if (currentAlien != null) {

				Iterator<Projectile> itr = projectileList.iterator();
				while(itr.hasNext()) {
					Projectile p = itr.next();

					if (p.getCurrentX() + p.getWidth() >= currentAlien.getCurrentX() 
							&& p.getCurrentX() <= currentAlien.getCurrentX() + currentAlien.getWidth()
							&&
							p.getCurrentY() + p.getHeight() >= currentAlien.getCurrentY() 
							&& p.getCurrentY() <= currentAlien.getCurrentY() + currentAlien.getHeight()) {

						itr.remove();
						alienList[x] = null;
						updatePlayerScore();
						break;
					}

				}

			}

		}

	}




	public void detectAlienCollisionWithSpaceship() {
		// Required.
		for (int x = 0; x < alienList.length; x ++) {
			Alien currentAlien = alienList[x];

			if (currentAlien != null) {

				if (currentAlien.getCurrentX() + currentAlien.getWidth() >= spaceShip.getCurrentX() 
						&& currentAlien.getCurrentX() <= spaceShip.getCurrentX() +  spaceShip.getWidth()
						&& currentAlien.getCurrentY() + currentAlien.getHeight() >= spaceShip.getCurrentY()
						&& currentAlien.getCurrentY() <= spaceShip.getCurrentY() + spaceShip.getHeight()) {

					gameOver = true;
				}

			}

		}

	}






	public boolean isGameOver() {
		// Required.

		if(gameOver == true){
			gameOver();
		}
		return false;
	}





	public void gameOver() {
		// Required.
		Platform.exit();
	}




	public static void main(String[] args) {
		// Implementation provided. No need to alter.
		launch(args);
	}



	// Placeholder animation. Locate the statement in gameloop() to remove it.
	private List<Projectile> northAnimList = new ArrayList<Projectile>();
	private List<Projectile> southAnimList = new ArrayList<Projectile>();
	public void placeHolderAnimation() {
		graphicsObject.setFill(Color.BLACK);
		graphicsObject.fillRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
		int offset = (int) (CANVAS_WIDTH / 20);
		if (northAnimList.size() == 0) {
			double xCoord = offset;
			double yCoord = CANVAS_HEIGHT;
			for (int x = 0; x < 10; x ++ ) {
				Projectile p = new Projectile(xCoord,yCoord);
				northAnimList.add(p);
				xCoord += (CANVAS_WIDTH / 10);
			}
		}
		if (southAnimList.size() == 0) {
			double xCoord = offset;
			double yCoord = 0;
			for (int x = 0; x < 10; x ++ ) {
				Projectile p = new Projectile(xCoord,yCoord);
				southAnimList.add(p);
				xCoord += (CANVAS_WIDTH / 10);
			}
		}
		for (int x = 0; x < northAnimList.size(); x ++ ) {
			Projectile p = northAnimList.get(x);
			graphicsObject.setFill(Color.CHARTREUSE);
			graphicsObject.fillOval(p.getCurrentX(), p.getCurrentY(), p.getWidth(), p.getHeight());
		}
		for (int x = 0; x < southAnimList.size(); x ++ ) {
			Projectile p = southAnimList.get(x);
			graphicsObject.setFill(Color.CHARTREUSE);
			graphicsObject.fillOval(p.getCurrentX(), p.getCurrentY(), p.getWidth(), p.getHeight());
		}
		Iterator<Projectile> northIterator = northAnimList.iterator();
		while (northIterator.hasNext()) {
			Projectile p = northIterator.next();
			p.setCurrentY(p.getCurrentY() - ((p.getTravelRate() * CANVAS_HEIGHT)/2));
			if (p.getCurrentY() <= 0) {
				northIterator.remove();
			} 
		}
		Iterator<Projectile> southIterator = southAnimList.iterator();
		while (southIterator.hasNext()) {
			Projectile p = southIterator.next();
			p.setCurrentY(p.getCurrentY() + ((p.getTravelRate() * CANVAS_HEIGHT)/2));
			if (p.getCurrentY() >= CANVAS_HEIGHT) {
				southIterator.remove();
			} 
		}
	}


} // End class.
