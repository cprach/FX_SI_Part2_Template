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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;


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
    private SoundManager sounds;
    private int countdown = 50;
    private AnimationTimer timer;
    
    final BooleanProperty spacePressed = new SimpleBooleanProperty(false);
    final BooleanProperty rightPressed = new SimpleBooleanProperty(false);
    final BooleanProperty leftPressed = new SimpleBooleanProperty(false);
    final BooleanBinding spaceAndRightPressed = spacePressed.and(rightPressed);
    final BooleanBinding spaceAndLeftPressed = spacePressed.and(leftPressed);
    
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
        sounds = new SoundManager();
        addEventHandlers();
    }
    public void gameLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gm.doGameLoop();
                draw();
                
                if (rightPressed.getValue()) {
                    gm.moveSpaceShip("east");
                }
                if (leftPressed.getValue()) {
                    gm.moveSpaceShip("west");
                }
                if (spacePressed.getValue()) {
                    gm.Shoot(System.currentTimeMillis());
                    //shootClip.play();
                }
                
                
                if (countdown == 0){
                    countdown = (int) (100 + (Math.random() * 50));
                    gm.dropProjectile();
                    sounds.playSound(SoundManager.soundNames.alienProjectile);
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
        drawScore();
        drawMenu();
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
    public void drawScore() {
		graphicsObject.setFill(Color.FUCHSIA);
		graphicsObject.fillText(Integer.toString(gm.getScore()), gm.getCanvasEndX() - 100, gm.getCanvasEndY() - 20);
	}

	public void drawMenu() {
		graphicsObject.setFill(Color.ORANGE);
		graphicsObject.fillText("Exit = x", gm.getCanvasStartX() + 100, gm.getCanvasEndY() - 20);
	}
    public void addEventHandlers() {
        gm.oScore.addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                sounds.playSound(SoundManager.soundNames.explosion);
            }
        });
        gm.oGameOver.addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue) {
                if (newValue == true) {
                    gm.oGameOver.removeListener(this);
                    gameOver();
                }
            }
        });
        gm.oShot.addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                    Boolean oldValue, Boolean newValue) {
                if (newValue == true) {
                    sounds.playSound(SoundManager.soundNames.shoot);
                }
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SPACE) {
                    spacePressed.set(true);
                } else if (ke.getCode() == KeyCode.RIGHT) {
                    rightPressed.set(true);
                } else if (ke.getCode() == KeyCode.LEFT) {
                    leftPressed.set(true);
                } else if (ke.getCode() == KeyCode.X) {
                    gameOver();
                }
            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SPACE) {
                    spacePressed.set(false);
                } else if (ke.getCode() == KeyCode.RIGHT) {
                    rightPressed.set(false);
                } else if (ke.getCode() == KeyCode.LEFT) {
                    leftPressed.set(false);
                }
            }
        });
//        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent ke) {
//                if (ke.getCode() == KeyCode.SPACE) {
//                    System.out.println("Shoot from here");
//                    fireProjectile();
//                } else if (ke.getCode() == KeyCode.RIGHT) {
//                    System.out.println("Move east from here");
//                    gm.updateSpaceShipPosition("east");
//                } else if (ke.getCode() == KeyCode.LEFT) {
//                    System.out.println("Move west from here");
//                    gm.updateSpaceShipPosition("west");
//                }
//            }
//        });
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
        timer.stop();
        sounds.stopAllSounds();
        sounds.playSound(SoundManager.soundNames.endGame);
        //Platform.exit();
        final Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setOpacity(.9);
        dialog.initModality(Modality.WINDOW_MODAL);
        
        VBox vb = new VBox();
        vb.setPadding(new Insets(10));
        vb.setSpacing(8);
        vb.setAlignment(Pos.CENTER);
        
        Text title = new Text("Game Over FOOL!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setFill(Color.CHARTREUSE);
        vb.getChildren().add(title);
        
        Label lblYourScoreLabel = new Label("Your score: " + gm.getScore());
        lblYourScoreLabel.setFont(new Font("Arial", 40));
        lblYourScoreLabel.setTextFill(Color.CHARTREUSE);
        
        VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
        vb.getChildren().add(lblYourScoreLabel);
        
        Button btnPlayAgain = new Button();
        btnPlayAgain.setText("Play again");
        btnPlayAgain.setFont(new Font("Arial", 25));
        
        btnPlayAgain.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                restart();
                dialog.close();
            }
        });
        
        VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
        vb.getChildren().add(btnPlayAgain);
        
        Button btnExit = new Button();
        btnExit.setText("Exit");
        btnExit.setFont(new Font("Arial", 25));
        
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
        
        VBox.setMargin(lblYourScoreLabel, new Insets(0, 0, 0, 8));
        vb.getChildren().add(btnExit);
        
        Scene dialogScene = new Scene(vb,gm.getCanvasWidth() / 3,gm.getCanvasHeight() / 3);
        
        // Capture space keypress as it is happening to prevent
        // default behaviour.
        dialogScene.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                
                if (event.getEventType() == KeyEvent.KEY_PRESSED){
                    // Consume Event before Bubbling Phase
                    if ( event.getCode() == KeyCode.SPACE ){
                        event.consume();
                    }
                }
            }
        });
        
        dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            
            @Override
            public void handle(KeyEvent arg0) {
                if (arg0.getCode() == KeyCode.X) {
                    Platform.exit();
                }
                if (arg0.getCode() == KeyCode.P) {
                    restart();
                    dialog.close();
                }
            }
            
        });
        
        dialogScene.setFill(Color.rgb(0, 0, 0, 1));
        dialog.setScene(dialogScene);
        dialog.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
    public void restart() {
        sounds.stopAllSounds();
        rightPressed.set(false);
        leftPressed.set(false);
        
        gm = new GameManager(CANVAS_WIDTH,CANVAS_HEIGHT,CANVAS_BUFFER);
        draw();
        addEventHandlers();
        gameLoop();
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
