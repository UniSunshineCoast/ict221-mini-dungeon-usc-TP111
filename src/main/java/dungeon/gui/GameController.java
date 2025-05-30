package dungeon.gui;

import dungeon.engine.Cell;
import dungeon.engine.GameEngine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GameController {

    // UI Elements
    @FXML
    private GridPane gridPane;

    @FXML
    private Button btUp;

    @FXML
    private Button btDown;

    @FXML
    private Button btRight;

    @FXML
    private Button btLeft;

    @FXML
    private Label level;

    @FXML
    private Button btHelp;

    @FXML
    private Button btSave;

    @FXML
    private Button btLoad;

    @FXML
    private Label steps;

    @FXML
    private Label score;

    @FXML
    private ProgressBar healthBar;

    @FXML
    private Label healthBarText;

    @FXML
    private TextArea eventLog;

    @FXML
    private Label endTitle;

    private GameEngine engine;
    private Cell[][] map;
    private static final int SIZE = 10; // Size of the dungeon

    // Sprites
    private final ArrayList<Image> floorTileImages = new ArrayList<>();
    private final Image topWall = loadImage("images/topWall.png");
    private final Image sideWall = loadImage("images/sideWall.png");
    private final Image bottomWall = loadImage("images/bottomWall.png");
    private final Image leftCornerWall = loadImage("images/leftCornerWall.png");
    private final Image rightCornerWall = loadImage("images/rightCornerWall.png");

    private final Image player = loadImage("images/player.png");
    private final Image melee = loadImage("images/melee.png");
    private final Image ranged = loadImage("images/ranged.png");
    private final Image entrance = loadImage("images/entrance.png");
    private final Image ladder = loadImage("images/ladder.png");
    private final Image trap = loadImage("images/trap.png");
    private final Image gold = loadImage("images/gold.png");
    private final Image potion = loadImage("images/potion.png");

    /**
     * Loads an image from the specified filename.
     * The image is set to a fixed size of 48x48 pixels.
     *
     * @param filename the name of the image file to load
     * @return the loaded Image object
     */
    private Image loadImage(String filename) {
        return new Image(filename, 48, 48, false, false);
    }

    /**
     * Gets the list of floor tile images used in the game.
     *
     * @return an ArrayList of Image objects representing floor tiles
     */
    public ArrayList<Image> getFloorTileImages() {
        return floorTileImages;
    }

    /**
     * Gets the event log TextArea where game events are displayed.
     *
     * @return the TextArea used for logging game events
     */
    public TextArea getEventLog() {
        return eventLog;
    }

    /**
     * Initializes the game controller and sets up the initial game state.
     * This method is called when the game starts, setting up the UI and loading necessary resources.
     *
     * @param difficulty the difficulty level of the game
     */
    public void startGame(int difficulty) {
        floorTileImages.add(loadImage("images/floor1.png"));
        floorTileImages.add(loadImage("images/floor2.png"));
        floorTileImages.add(loadImage("images/floor3.png"));
        floorTileImages.add(loadImage("images/floor4.png"));

        engine = new GameEngine(SIZE, difficulty, this);
        map = engine.getMap();

        btUp.setOnMouseClicked(e -> move(true, true));

        btDown.setOnMouseClicked(e -> move(true, false));

        btRight.setOnMouseClicked(e -> move(false, true));

        btLeft.setOnMouseClicked(e -> move(false, false));

        btHelp.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("help_gui.fxml"));
                Parent helpRoot = loader.load();
                Stage helpStage = new Stage();
                helpStage.setScene(new Scene(helpRoot));
                helpStage.setTitle("Help");
                helpStage.setResizable(false);
                helpStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btSave.setOnMouseClicked(event -> {
            if (!engine.isGameOver()) {
                engine.saveGame();
            }
        });

        btLoad.setOnMouseClicked(event -> {
            if(!engine.isGameOver()) {
                engine.loadGame();
                updateGUI();
            }
        });

        updateGUI();

    }

    /**
     * Handles player movement based on the direction and type of movement (vertical or horizontal).
     * This method is called when the respective buttons are clicked.
     *
     * @param vertical true if the movement is vertical, false for horizontal
     * @param upRight true if the movement is up or right, false for down or left
     */
    private void move(boolean vertical, boolean upRight) {
        if (!engine.isGameOver()) {
            engine.movePlayer(vertical, upRight);
            updateGUI();
        }
    }

    /**
     * Updates the GUI to reflect the current state of the game.
     * This method is called after every move to refresh the map and game objects.
     */
    private void updateGUI() {
        //Clear old GUI grid pane
        gridPane.getChildren().clear();
        map = engine.getMap();

        // Regenerate map background and game objects
        generateMapBackground();
        generateGameObjects();
        updateText();
    }

    /**
     * Generates the background of the map with walls and floor tiles.
     * The grid is expanded by one cell on each side to accommodate walls.
     */
    private void generateMapBackground() {
        for (int y = -1; y < engine.getSize() + 1; y++) {
            for (int x = -1; x < engine.getSize() + 1; x++) {

                Cell cell;
                ImageView tile;

                boolean inBounds = x >= 0 && x < engine.getSize() && y >= 0 && y < engine.getSize();
                if (inBounds) {
                    cell = engine.getMap()[x][y];
                } else {
                    cell = new Cell();
                }

                cell.setMinSize(48, 48);

                if (y < 0 && x >= 0 && x < engine.getSize()) {
                    tile = new ImageView(topWall);
                } else if (x < 0 && y < engine.getSize()) {
                    tile = new ImageView(sideWall);
                } else if (x >= engine.getSize() && y < engine.getSize()) {
                    tile = new ImageView(sideWall);
                    tile.setRotate(180);
                } else if (y >= engine.getSize()) {
                    if (x < 0) {
                        tile = new ImageView(leftCornerWall);
                    } else if (x >= engine.getSize()) {
                        tile = new ImageView(rightCornerWall);
                    } else {
                        tile = new ImageView(bottomWall);
                    }
                } else {
                    tile = new ImageView(cell.getFloorTile());
                }

                cell.getChildren().add(tile);
                gridPane.add(cell, x + 1, y + 1);
            }
        }
    }

    /**
     * Generates game objects on the map based on the current state of the game engine.
     * It places images representing different game objects in their respective cells.
     */
    private void generateGameObjects () {
        for (int y = 0; y < engine.getSize(); y++) {
            for (int x = 0; x < engine.getSize(); x++) {
                Cell cell = map[x][y];
                if (cell.getGameObject() != null) {
                    ImageView gameObjectImage = switch (cell.getGameObject().getSymbol()) {
                        case 'M' -> new ImageView(melee);
                        case 'R' -> new ImageView(ranged);
                        case 'E' -> new ImageView(entrance);
                        case 'L' -> new ImageView(ladder);
                        case 'T' -> new ImageView(trap);
                        case 'G' -> new ImageView(gold);
                        case 'H' -> new ImageView(potion);
                        default -> null;
                    };
                    if (gameObjectImage != null) {
                        gridPane.add(gameObjectImage, y + 1, x + 1);
                    }
                }
            }
        }

        // Add player image
        int playerRow = engine.getPlayer().getRow();
        int playerCol = engine.getPlayer().getCol();
        ImageView playerImage = new ImageView(player);
        gridPane.add(playerImage, playerCol + 1, playerRow + 1);
    }

    /**
     * Updates the text labels for steps, score, and health bar.
     * This method is called after every move to reflect the current game state.
     */
    private void updateText () {
        level.setText(engine.getLevel() + "");
        steps.setText(engine.getCurrSteps() + " / " + engine.getMaxSteps());
        score.setText(engine.getScore() + "");
        healthBar.setProgress((float) engine.getPlayerHP() / engine.getPlayerMaxHP());
        healthBarText.setText(engine.getPlayerHP() + " / " + engine.getPlayerMaxHP());
        healthBarText.setStyle("-fx-accent: " + (engine.getPlayerHP() > 5 ? "green" : "red") + ";");
    }

    /**
     * Ends the game with a message and disables all movement buttons.
     * This method is called when the game is over, either by winning or losing.
     *
     * @param message the message to display at the end of the game
     * @param color   the color of the text for the end message
     */
    public void endGame(String message, String color) {
        endTitle.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 72px; -fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);");
        endTitle.setText(message);
        btUp.setDisable(true);
        btDown.setDisable(true);
        btRight.setDisable(true);
        btLeft.setDisable(true);
        btHelp.setDisable(true);
        btSave.setDisable(true);
        btLoad.setDisable(true);
    }
}
