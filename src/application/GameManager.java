/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package application;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author rcolley
 */
public class GameManager {
    
    private final int GAME_ASSET_WIDTH_DIVISOR = 20;
    private final double GAME_ASSET_HEIGHT_PERCENTAGE = 0.75;
    private double canvasWidth;
    private double canvasHeight;
    private double canvasBuffer;
    
    private SpaceShip spaceShip;
    private Alien[] alienList;
    private ArrayList<Projectile> projectileList;
    private int playerScore;
    
    private double gameAssetWidth;
    private double gameAssetHeight;
    
    private final int TOTAL_NUM_OF_ALIENS = 15;
    private final int NUM_OF_ALIENS_ON_ROW = 5;
    private double assetSpacerWidth; // The space between each alien.
    private double assetSpacerHeight; // The space between each row of aliens.
    private double fleetTravelRate;
    private String fleetDirection;
    private boolean gameOver;
    
    // Calculated
    private double canvasStartX;
    private double canvasStartY;
    private double canvasEndX;
    private double canvasEndY;
    
    public GameManager(double canvasWidth, double canvasHeight, double canvasBuffer) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasBuffer = canvasBuffer;
        initialise();
    }
    public void initialise() {
        playerScore = 0;
        gameAssetWidth = 0;
        gameAssetHeight = 0;
        assetSpacerWidth = 0;
        assetSpacerHeight = 0;
        fleetTravelRate = 0;
        fleetDirection = "east";
        gameOver = false;
        makeCanvasDimensions();
        initialiseGameAssetDimensions();
        initialiseSpaceShipPosition();
        initialiseAlienFleetPosition();
        initialiseProjectileList();
    }
    public void makeCanvasDimensions() {
        canvasStartX = canvasBuffer;
        canvasStartY = canvasBuffer;
        canvasEndX = canvasWidth - canvasBuffer;
        canvasEndY = canvasHeight - canvasBuffer;
    }
    public void initialiseGameAssetDimensions() {
        gameAssetWidth = canvasWidth / GAME_ASSET_WIDTH_DIVISOR;
        gameAssetHeight = GAME_ASSET_HEIGHT_PERCENTAGE * gameAssetWidth;
    }
    public void initialiseSpaceShipPosition() {
        double middleCoordinate = makeMiddleXCoord();
        double x = middleCoordinate - (gameAssetWidth / 2);
        double y = canvasEndY - (gameAssetHeight * 2);
        spaceShip = new SpaceShip(gameAssetWidth, gameAssetHeight, x,y);
    }
    public double makeMiddleXCoord() {
        double middleCoordinate = (canvasEndX - canvasStartX) / 2;
        return middleCoordinate;
    }
    public double makeAxisLength(double startCoord, double endCoord) {
        double axisLength = endCoord - startCoord;
        return axisLength;
    }
    public void initialiseAlienFleetPosition() {
        assetSpacerHeight = gameAssetHeight;
        alienList = new Alien[TOTAL_NUM_OF_ALIENS];
        int alienCount = 0;
        int aliensOnRowCount = 0;
        double currentX = makeFleetStartX();
        double currentY = makeFleetStartY();
        while (alienCount < TOTAL_NUM_OF_ALIENS) {
            Alien a = new Alien(gameAssetWidth, gameAssetHeight, currentX, currentY);
            alienList[alienCount] = a;
            currentX += gameAssetWidth + assetSpacerWidth;
            aliensOnRowCount ++;
            alienCount ++;
            if (aliensOnRowCount > NUM_OF_ALIENS_ON_ROW - 1) {
                aliensOnRowCount = 0;
                currentX = makeFleetStartX();
                currentY += assetSpacerHeight * 2;
            }
        }
        fleetTravelRate = alienList[0].getTravelRate();
    }
    public double makeFleetStartX() {
        double axisLength = makeAxisLength(canvasStartX, canvasEndX);
        double remainingWidthOnX = axisLength - (gameAssetWidth * NUM_OF_ALIENS_ON_ROW);
        assetSpacerWidth = remainingWidthOnX / (NUM_OF_ALIENS_ON_ROW + 1);
        double fleetStartX = assetSpacerWidth;
        return fleetStartX;
    }
    public double makeFleetStartY() {
        double fleetStartY = canvasStartX + gameAssetHeight;
        return fleetStartY;
    }
    public void initialiseProjectileList() {
        projectileList = new ArrayList<Projectile>();
    }
    public void moveFleetEast(double travelIncrement) {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedX = alienList[x].getCurrentX() + travelIncrement;
                alienList[x].setCurrentX(updatedX);
            }
        }
    }
    public void moveFleetWest(double travelIncrement) {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedX = alienList[x].getCurrentX() - travelIncrement;
                alienList[x].setCurrentX(updatedX);
            }
        }
    }
    public void moveFleetSouth() {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedY = alienList[x].getCurrentY() + (gameAssetHeight / 2);
                alienList[x].setCurrentY(updatedY);
            }
        }
    }
    public void updateAlienFleetPosition() {
        double axisLength = canvasWidth;
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
            if ((mostEast + gameAssetWidth) + travelIncrement > canvasWidth) {
                travelIncrement = distanceEast - gameAssetWidth;
                fleetDirection = "west";
                moveFleetSouth();
            }
            moveFleetEast(travelIncrement);
        }
        //// West
        if (fleetDirection.equals("west")) {
            double mostWest = canvasWidth;
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
        Iterator<Projectile> i = projectileList.iterator();
        while (i.hasNext()) {
            Projectile currentProjectile = i.next();
            double axisLength = canvasHeight;
            double travelIncrement = currentProjectile.getTravelRate() * axisLength;
            currentProjectile.setCurrentY(currentProjectile.getCurrentY() - travelIncrement);
            if (currentProjectile.getCurrentY() <= 0) {
                i.remove();
                System.out.println("projectileList size: " + projectileList.size());
            }
        }
    }
    public boolean checkForWestEdge(double currentXCoord) {
        boolean detected = false;
        if (currentXCoord <= 0) {
            detected = true;
        }
        return detected;
    }
    public boolean checkForEastEdge(double currentXCoord, double widthToCheck) {
        boolean detected = false;
        if (currentXCoord  + widthToCheck >= canvasWidth) {
            detected = true;
        }
        return detected;
    }
    public void updateSpaceShipPosition(String direction) {
        double axisLength = canvasWidth;
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
                spaceShip.setCurrentX(canvasWidth - spaceShip.getWidth());
            } else {
                spaceShip.setCurrentX(updatedX);
            }
        }
    }
    public void addNewProjectile() {
        projectileList.add(new Projectile(spaceShip.getCurrentX() + (gameAssetWidth / 2), spaceShip.getCurrentY()));
        System.out.println("projectileList size: " + projectileList.size());
    }
    public void detectProjectileCollisionWithAlien() {
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
    public void updatePlayerScore() {
        playerScore ++;
        System.out.println("Player score: " + playerScore);
        if (playerScore == TOTAL_NUM_OF_ALIENS) {
            gameOver = true;
        }
    }
    public void detectAlienCollisionWithSpaceship() {
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
    public SpaceShip getSpaceShip() {
        return spaceShip;
    }
    public Alien[] getAlienList() {
        return alienList;
    }
    public ArrayList<Projectile> getProjectileList() {
        return projectileList;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    
    
    
    
}
