/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package application;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author rcolley
 */
public class GameManager {
    
    private int score;
    IntegerProperty oScore = new SimpleIntegerProperty();
    BooleanProperty oGameOver = new SimpleBooleanProperty();
    BooleanProperty oShot = new SimpleBooleanProperty();
    private int finishScore;
    private long lastShot;
    
    private final int GAME_ASSET_WIDTH_DIVISOR = 20;
    private final double GAME_ASSET_HEIGHT_PERCENTAGE = 0.75;
    private double canvasWidth;
    private double canvasHeight;
    private double canvasBuffer;
    
    private SpaceShip spaceShip;
    private Alien[] alienList;
    private ArrayList<Projectile> spaceShipProjectiles;
    private ArrayList<Projectile> alienProjectiles;
    private int playerScore;
    
    private double gameAssetWidth;
    private double gameAssetHeight;
    
    private final int TOTAL_NUM_OF_ALIENS = 15;
    private final int NUM_OF_ALIENS_ON_ROW = 5;
    private double assetSpacerWidth; // The space between each alien.
    private double assetSpacerHeight; // The space between each row of aliens.
    private double fleetTravelRate;
    private String fleetDirection;
    private double fleetProjectileTravelRate;
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
        initialiseGameManager();
    }
    // For use by UI
    public void moveSpaceShip(String direction) {
        updateSpaceShipPosition(direction);
    }
    public void initialiseGameManager() {
        playerScore = 0;
        gameAssetWidth = 0;
        gameAssetHeight = 0;
        assetSpacerWidth = 0;
        assetSpacerHeight = 0;
        fleetTravelRate = 0;
        fleetDirection = "east";
        gameOver = false;
        finishScore = TOTAL_NUM_OF_ALIENS;
        makeCanvasDimensions();
        initialiseGameAssetDimensions();
        initialiseSpaceShipPosition();
        initialiseAlienFleetPosition();
        initialiseProjectileLists();
    }
    public void doGameLoop() {
        updateAlienFleetPosition();
        updateProjectiles();
        updateAlienProjectilePostion();
        detectAlienCollisionWithSpaceship();
        detectProjectileCollisionWithSpaceship();
    }
    public void updateScore() {
        score ++;
        oScore.setValue(score);
        if(score == finishScore) {
            oGameOver.set(true);
        }
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
    public void initialiseProjectileLists() {
        spaceShipProjectiles = new ArrayList<Projectile>();
        alienProjectiles = new ArrayList<Projectile>();
    }
    public void moveFleetEast(double travelIncrement) {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedX = alienList[x].getCurrentX() + travelIncrement;
                alienList[x].setCurrentX(updatedX);
                if (detectProjectileCollisionWithAlien(alienList[x])) {
                    alienList[x] = null;
                    updateScore();
                }
            }
        }
    }
    public void moveFleetWest(double travelIncrement) {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedX = alienList[x].getCurrentX() - travelIncrement;
                alienList[x].setCurrentX(updatedX);
                if (detectProjectileCollisionWithAlien(alienList[x])) {
                    alienList[x] = null;
                    updateScore();
                }
            }
        }
    }
    public void moveFleetSouth() {
        for (int x = 0; x < alienList.length; x++) {
            if (alienList[x] != null) {
                double updatedY = alienList[x].getCurrentY() + (gameAssetHeight / 2);
                alienList[x].setCurrentY(updatedY);
                if (detectProjectileCollisionWithAlien(alienList[x])) {
                    alienList[x] = null;
                    updateScore();
                }
            }
        }
    }
    public void updateAlienFleetPosition() {
        double axisLength = makeAxisLength(canvasStartX, canvasEndX);
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
        Iterator<Projectile> i = spaceShipProjectiles.iterator();
        while (i.hasNext()) {
            Projectile currentProjectile = i.next();
            double axisLength = canvasHeight;
            double travelIncrement = currentProjectile.getTravelRate() * axisLength;
            currentProjectile.setCurrentY(currentProjectile.getCurrentY() - travelIncrement);
            if (currentProjectile.getCurrentY() <= 0) {
                i.remove();
                System.out.println("projectileList size: " + spaceShipProjectiles.size());
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
    public void Shoot(Long now) {
        if(now > lastShot + 300) {
            oShot.setValue(true);
//			addNewProjectile(spaceShip.getCurrentX() + (gameAssetWidth / 2),spaceShip.getCurrentY());
            addNewProjectile();
            
            lastShot = now;
        }
        oShot.setValue(false);
    }
    private void updateSpaceShipPosition(String direction) {
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
        spaceShipProjectiles.add(new Projectile(spaceShip.getCurrentX() + (gameAssetWidth / 2), spaceShip.getCurrentY()));
        System.out.println("projectileList size: " + spaceShipProjectiles.size());
    }
    public void dropProjectile() {
        int numOfCols = NUM_OF_ALIENS_ON_ROW - 1;
        Alien ra = null;
        int index = 0;
        int randomCol = (int) (Math.random() * (numOfCols + 1));
        index = (10 + randomCol);
        while (ra == null && index >= 0) {
            ra = alienList[index];
            index -= 5;
        }
        if (ra != null) {
            alienProjectiles.add(new Projectile(ra.getCurrentX() + (gameAssetWidth / 2), ra.getCurrentY() + (gameAssetHeight + 1)));
            fleetProjectileTravelRate = ra.getTravelRate();
        }
    }
    public boolean detectProjectileCollisionWithSpaceship() {
        Iterator<Projectile> itr = alienProjectiles.iterator();
        while(itr.hasNext()) {
            Projectile p = itr.next();
            if (p.getCurrentX() + p.getWidth() >= spaceShip.getCurrentX()
                    && p.getCurrentX() <= spaceShip.getCurrentX() + spaceShip.getWidth()
                    &&
                    p.getCurrentY() + p.getHeight() >= spaceShip.getCurrentY() && p.getCurrentY() <= spaceShip.getCurrentX() + spaceShip.getHeight()) {
                itr.remove();
                oGameOver.set(true);
                return true;
            }
        }
        return false;
    }
    public void updateAlienProjectilePostion() {
        double axisLength = makeAxisLength(canvasStartY, canvasEndY);
        double travelIncrement = fleetProjectileTravelRate * axisLength;
        Iterator<Projectile> itr = alienProjectiles.iterator();
        while(itr.hasNext()) {
            Projectile p = itr.next();
            double projectileY = p.getCurrentY();
            if (projectileY + travelIncrement > canvasEndY) {
                itr.remove();
            } else {
                p.setCurrentY(projectileY + travelIncrement);
            }
        }
    }
//    public void updateProjectilePostion() {
////        double axisLength = makeAxisLength(canvasStartY, canvasEndY);
////        double travelIncrement = projectileTravelRate * axisLength;
////        Iterator<Projectile> itr = spaceShipProjectiles.iterator();
////        while(itr.hasNext()) {
////            Projectile p = itr.next();
////            double projectileY = p.getY();
////            if (projectileY - travelIncrement < canvasStartY) {
////                itr.remove();
////            } else {
////                p.setY(projectileY - travelIncrement);
////            }
////        }
//    }
    public boolean detectProjectileCollisionWithAlien(Alien a) {
        Iterator<Projectile> itr = spaceShipProjectiles.iterator();
        while(itr.hasNext()) {
            Projectile p = itr.next();
            if (p.getCurrentX() + p.getWidth() >= a.getCurrentX()
                    && p.getCurrentX() <= a.getCurrentX() + a.getWidth()
                    &&
                    p.getCurrentY() + p.getHeight() >= a.getCurrentY() && p.getCurrentY() <= a.getCurrentY() + a.getHeight()) {
                itr.remove();
                return true;
            }
        }
        return false;
//        for (int x = 0; x < alienList.length; x ++ ) {
//            Alien currentAlien = alienList[x];
//            if (currentAlien != null) {
//                Iterator<Projectile> itr = spaceShipProjectiles.iterator();
//                while(itr.hasNext()) {
//                    Projectile p = itr.next();
//                    if (p.getCurrentX() + p.getWidth() >= currentAlien.getCurrentX()
//                            && p.getCurrentX() <= currentAlien.getCurrentX() + currentAlien.getWidth()
//                            &&
//                            p.getCurrentY() + p.getHeight() >= currentAlien.getCurrentY()
//                            && p.getCurrentY() <= currentAlien.getCurrentY() + currentAlien.getHeight()) {
//
//                        itr.remove();
//                        alienList[x] = null;
//                        updatePlayerScore();
//                        break;
//                    }
//                }
//            }
//        }
    }
    public void updatePlayerScore() {
        playerScore ++;
        System.out.println("Player score: " + playerScore);
        if (playerScore == TOTAL_NUM_OF_ALIENS) {
            gameOver = true;
        }
    }
    public boolean detectAlienCollisionWithSpaceship() {
        Iterator<Projectile> itr = alienProjectiles.iterator();
        while(itr.hasNext()) {
            Projectile p = itr.next();
            if (p.getCurrentX() + p.getWidth() >= spaceShip.getCurrentX()
                    && p.getCurrentX() <= spaceShip.getCurrentX() + spaceShip.getWidth()
                    &&
                    p.getCurrentY() + p.getHeight() >= spaceShip.getCurrentY() && p.getCurrentY() <= spaceShip.getCurrentY() + spaceShip.getHeight()) {
                itr.remove();
                oGameOver.set(true);
                return true;
            }
        }
        return false;
//        for (int x = 0; x < alienList.length; x ++) {
//            Alien currentAlien = alienList[x];
//            if (currentAlien != null) {
//                if (currentAlien.getCurrentX() + currentAlien.getWidth() >= spaceShip.getCurrentX()
//                        && currentAlien.getCurrentX() <= spaceShip.getCurrentX() +  spaceShip.getWidth()
//                        && currentAlien.getCurrentY() + currentAlien.getHeight() >= spaceShip.getCurrentY()
//                        && currentAlien.getCurrentY() <= spaceShip.getCurrentY() + spaceShip.getHeight()) {
//
//                    gameOver = true;
//                }
//            }
//        }
    }
    public SpaceShip getSpaceShip() {
        return spaceShip;
    }
    public Alien[] getAlienList() {
        return alienList;
    }
    public ArrayList<Projectile> getProjectileList() {
        return spaceShipProjectiles;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    
    public ArrayList<Projectile> getAlienProjectiles() {
        return alienProjectiles;
    }
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public IntegerProperty getoScore() {
        return oScore;
    }
    
    public void setoScore(IntegerProperty oScore) {
        this.oScore = oScore;
    }
    
    public BooleanProperty getoGameOver() {
        return oGameOver;
    }
    
    public void setoGameOver(BooleanProperty oGameOver) {
        this.oGameOver = oGameOver;
    }
    
    public int getFinishScore() {
        return finishScore;
    }
    
    public void setFinishScore(int finishScore) {
        this.finishScore = finishScore;
    }

    public double getCanvasWidth() {
        return canvasWidth;
    }

    public double getCanvasHeight() {
        return canvasHeight;
    }

    public double getCanvasStartX() {
        return canvasStartX;
    }

    public double getCanvasStartY() {
        return canvasStartY;
    }

    public double getCanvasEndX() {
        return canvasEndX;
    }

    public double getCanvasEndY() {
        return canvasEndY;
    }
    
    
    
    
}
