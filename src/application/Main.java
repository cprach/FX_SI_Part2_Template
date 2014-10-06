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
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;


public class Main extends Application {
    
    private final String MY_NAME_AND_STUDENT_NUMBER = "Rachael Colley 007"; // <-- change this to you <-- <-- <--
    private FlowPane rootLayout;
    private Canvas drawingCanvas;
    private GraphicsContext graphicsObject;
    private Scene scene;
    private final double CANVAS_WIDTH = 500;
    private final double CANVAS_HEIGHT = 500;
    private final double CANVAS_BUFFER = 100;
    private GameManager gm;
    int countdown = 50;
    
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
        gm = new GameManager(CANVAS_WIDTH,CANVAS_HEIGHT,CANVAS_BUFFER);
        addEventHandlers();
    }
    public void gameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
                update();
                checkForCollision();
                if (countdown == 0){
                    countdown = (int) (100 + (Math.random() * 50));
                    gm.dropProjectile();
                    //alienProjectileSoundClip.play();
                }
                countdown --;
                isGameOver();
                //placeHolderAnimation(); // <-- REMOVE THIS LINE <-- <--
            }
        };
        timer.start();
    }
    public void draw() {
        graphicsObject.setFill(Color.BLACK);
        graphicsObject.fillRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        drawSpaceShip();
        drawAliens();
        drawProjectiles();
        drawAlienProjectiles();
    }
    public void update() {
        gm.updateAlienFleetPosition();
        gm.updateProjectiles();
        gm.updateAlienProjectilePostion();
    }
    public void checkForCollision() {
        gm.detectProjectileCollisionWithAlien();
        gm.detectAlienCollisionWithSpaceship();
    }
    public void drawSpaceShip() {
        graphicsObject.setFill(Color.WHITE);
        graphicsObject.fillRect(gm.getSpaceShip().getCurrentX(), gm.getSpaceShip().getCurrentY(),
                gm.getSpaceShip().getWidth(), gm.getSpaceShip().getHeight());
    }
    public void drawAliens() {
        graphicsObject.setFill(Color.CHARTREUSE);
        Alien[] alienList = gm.getAlienList();
        for (int x = 0; x < alienList.length; x ++ ) {
            Alien currentAlien = alienList[x];
            if (currentAlien != null) {
                graphicsObject.fillRect(currentAlien.getCurrentX(),
                        currentAlien.getCurrentY(),
                        currentAlien.getWidth(),
                        currentAlien.getHeight());
            }
        }
//        fleetTravelRate = alienList[0].getTravelRate();
    }
    public void drawProjectiles() {
        graphicsObject.setFill(Color.RED);
        ArrayList<Projectile> projectileList = gm.getProjectileList();
        for (int x = 0; x < projectileList.size(); x ++ ) {
            Projectile currentProjectile = projectileList.get(x);
            graphicsObject.fillRect(currentProjectile.getCurrentX(),
                    currentProjectile.getCurrentY(),
                    currentProjectile.getWidth(),
                    currentProjectile.getHeight());
        }
    }
    public void drawAlienProjectiles() {
		graphicsObject.setFill(Color.CHARTREUSE);
		List<Projectile> projectiles = gm.getAlienProjectiles();
		for (Projectile p : projectiles) {
			graphicsObject.fillRect(p.getCurrentX(), p.getCurrentY(), p.getWidth(), p.getHeight());
		}
	}
    public void addEventHandlers() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SPACE) {
                    System.out.println("Shoot from here");
                    fireProjectile();
                } else if (ke.getCode() == KeyCode.RIGHT) {
                    System.out.println("Move east from here");
                    gm.updateSpaceShipPosition("east");
                } else if (ke.getCode() == KeyCode.LEFT) {
                    System.out.println("Move west from here");
                    gm.updateSpaceShipPosition("west");
                }
            }
        });
    }
    public void fireProjectile() {
        gm.addNewProjectile();
    }
    public boolean isGameOver() {
        if(gm.isGameOver() == true){
            gameOver();
        }
        return false;
    }
    public void gameOver() {
        Platform.exit();
    }
    public static void main(String[] args) {
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
