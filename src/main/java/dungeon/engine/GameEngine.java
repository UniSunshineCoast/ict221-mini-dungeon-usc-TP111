package dungeon.engine;

import dungeon.gui.GameController;
import javafx.scene.image.Image;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.time.LocalDate;

public class GameEngine {

    /**
     * ANSI escape codes for colored console output.
     * These codes are used to format the text color in the console.
     */

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";

    private static final int SIZE = 10; // Default size of the game board
    public static boolean GUI; // true = GUI, false = console

    private int level = 1; // Current level of the game, starts at 1
    private final int maxLevel = 2; // Maximum level, currently only 2 levels are supported
    private final Cell[][][] maps = new Cell[maxLevel][][]; // Array to hold maps for each level

    private int currSteps = 0; // Current number of steps taken by the player
    private final int maxSteps = 100; // Maximum number of steps allowed in the game, can be adjusted for difficulty
    private int playerHP; // Current HP of the player, starts at 10
    private final int playerMaxHP = 10; // Maximum HP for the player
    private int score = 0; // Current score of the player, starts at 0
    private int difficulty = 3; // Difficulty level of the game, can be adjusted between 0 and 10 (default 3)
    private boolean gameOver = false; // Flag to indicate if the game is over

    private final Player player; // The player object representing the player in the game
    private GameController gameController; // The GameController for GUI mode, can be null for console mode
    private int eventOrder = 0; // Used to order events in the event log
    private final Scanner scanner = new Scanner(System.in); // Scanner for console input

    /**
     * Creates a square game board with a default size of 10x10 and no GameController.
     * This constructor is used for console mode.
     */
    public GameEngine (int size, int difficulty) {
        this(size, difficulty, null);
    }

    /**
     * Creates a square game board with a specified size and an optional GameController.
     * If a GameController is provided, the game will run in GUI mode; otherwise, it will run in console mode.
     *
     * @param size           the width and height of the game board.
     * @param difficulty     the difficulty level of the game, capped between 0 and 10.
     * @param gameController the GameController for GUI mode, or null for console mode.
     */
    public GameEngine(int size, int difficulty, GameController gameController) {
        if (gameController != null) {
            this.gameController = gameController;
            GUI = true; // If a GameController is provided, we are in GUI mode
        } else {
            GUI = false; // If no GameController is provided, we are in console mode
        }

        setDifficulty(difficulty);

        for (int i = 0; i < maps.length; i++) {
            maps[i] = new Cell[size][size];
        }

        for (int k = 0; k < maps.length; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    maps[k][i][j] = new Cell();
                }
            }
        }

        player = new Player(getSize() - 1, 0);
        playerHP = playerMaxHP;

        generateMap();

        if (!GUI)
            printMap();
    }

    /**
     * Runs the console loop for the game.
     * This method continuously prompts the player for input and processes their moves.
     * It displays the current steps, player HP, and score after each move.
     */
    public void runConsoleLoop() {
        while (!isGameOver()) {
            System.out.println("Current Steps: " + currSteps + "/" + maxSteps);
            System.out.println("Player HP: " + playerHP + "/" + playerMaxHP);
            System.out.println("Score: " + score);
            System.out.print("Move (u/d/l/r): ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "u": movePlayer(true, true); break;
                case "d": movePlayer(true, false); break;
                case "r": movePlayer(false, true); break;
                case "l": movePlayer(false, false); break;
                default: System.out.println("Invalid input!");
            }
        }
    }

    /**
     * Moves the player in the specified direction.
     * If the move is valid, it updates the player's position and handles any game objects in the new cell.
     * If the player is stepping off a game object, it removes that game object from the cell (unless it is an entrance or a trap).
     *
     * @param vertical true if the movement is vertical (up/down), false for horizontal (left/right)
     * @param upRight  true if moving up or right, false for down or left
     */
    public void movePlayer(boolean vertical, boolean upRight) {
        int currentRow = player.getRow();
        int currentCol = player.getCol();
        int newRow = vertical ? (upRight ? currentRow - 1 : currentRow + 1) : currentRow;
        int newCol = !vertical ? (upRight ? currentCol + 1 : currentCol - 1) : currentCol;
        Cell currentCell = getMap()[currentRow][currentCol];
        Cell newCell;

        if (isValidMove(newRow, newCol)) {
            newCell = getMap()[newRow][newCol];
            GameObject steppedOnObject = newCell.getGameObject();

            if (!(currentCell.getGameObject() instanceof Entrance) && !(currentCell.getGameObject() instanceof Trap)) {
                //Delete the current cell's game object unless it's the entrance or a trap
                currentCell.setGameObject(null);
            }

            player.moveTo(newRow, newCol);

            if (!(steppedOnObject instanceof Ladder) && !GUI) {
                // If the player is on the ladder, don't print the map as it will still show the first level
                System.out.println("--------------------------------------");
                printMap();
            }

            if (steppedOnObject != null) {
                // If there's a game object in the new cell, call its onPlayerEnter method
                steppedOnObject.onPlayerEnter(this);
            }

            checkMutantAttacks();
            addStep();
        } else {
            if (!GUI) {
                System.out.println("--------------------------------------");
                printMap();
            }

            printEvent("Can't move there!", RED);
        }
    }

    /**
     * Checks if the given row and column are valid indices for the map.
     *
     * @param row the row index to check.
     * @param col the column index to check.
     * @return true if the move is valid, false otherwise.
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < getMap().length && col >= 0 && col < getMap()[0].length;
    }

    /**
     * Returns the player object.
     *
     * @return the player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Checks if there are any ranged mutants on the map and attempts to attack the player.
     * This method is called after every player move to ensure that mutants can attack the player.
     */
    private void checkMutantAttacks() {
        if (!isGameOver()){
            for (int row = 0; row < getMap().length; row++) {
                for (int col = 0; col < getMap().length; col++) {
                    GameObject obj = getMap()[row][col].getGameObject();
                    if (obj instanceof RangedMutant) {
                        ((RangedMutant) obj).tryAttackPlayer(this, row, col);
                    }
                }
            }
        }
    }

    /**
     * Returns the size of the map.
     * The size is the same for both levels but for future expansion we check the current level.
     *
     * @return the size of the map.
     */
    public int getSize() {
        // The size of the map is the same for both levels but for future expansion we check the current level.
        return getMap().length;
    }

    /**
     * Returns the map of the current level of the game.
     *
     * @return the current level map.
     */
    public Cell[][] getMap() {
        return maps[getLevel() - 1];
    }

    /**
     * Resets the current map to a new empty map.
     * This method is called when the player loads a game from a lower level.
     * It clears the current map and initializes it with new empty cells.
     */
    private void resetCurrMap() {
        // Reset the current map to a new empty map
        maps[getLevel() - 1] = new Cell[getSize()][getSize()];
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                maps[getLevel() - 1][i][j] = new Cell();
            }
        }
    }

    /**
     * Returns the current level of the game.
     *
     * @return the current level number.
     */
    public int getCurrSteps() {
        return currSteps;
    }

    /**
     * Sets the current number of steps taken by the player.
     * This method is used to update the step count, especially when loading a saved game.
     *
     * @param currSteps the new number of steps taken by the player.
     */
    private void setCurrSteps(int currSteps) {
        this.currSteps = currSteps;
    }

    /**
     * Returns the current level of the game.
     *
     * @return the current level number.
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Returns the current level of the game.
     *
     * @return the current level number.
     */
    public int getPlayerHP() {
        return playerHP;
    }

    /**
     * Returns the maximum HP of the player.
     *
     * @return the maximum HP value.
     */
    public int getPlayerMaxHP() {
        return playerMaxHP;
    }

    /**
     * Sets the player's HP.
     * If the HP is less than or equal to 0, it triggers a game over state.
     * If the HP exceeds the maximum, it caps it to the maximum value.
     *
     * @param playerHP the new HP value for the player.
     */
    public void setPlayerHP(int playerHP) {
        this.playerHP = playerHP;
        if (this.playerHP <= 0 && !isGameOver()) {
            this.playerHP = 0; // Prevent negative HP

            if(!GUI)
                System.out.print(BOLD);
            else
                gameController.endGame("You died!\n Game over.", "red");

            System.out.println();
            printEvent("You died! Game over.", RED);
            setScore(-1);
            gameOver();
        } else if (this.playerHP > playerMaxHP) {
            this.playerHP = playerMaxHP; // Cap HP to a maximum value
        }
    }

    /**
     * Returns the current score of the game.
     * The score can be negative, which indicates a game over state.
     *
     * @return the current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the game.
     * The score can be negative, which indicates a game over state.
     *
     * @param score the new score to set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the current difficulty of the game.
     *
     * @return the difficulty level, capped between 0 and 10.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty of the game.
     * The difficulty is capped between 0 and 10.
     *
     * @param difficulty the new difficulty level.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        if (this.difficulty < 0) {
            this.difficulty = 0; // Prevent negative difficulty
            System.out.println("Difficulty capped at 0.");
        } else if (this.difficulty > 10) {
            this.difficulty = 10; // Cap difficulty to a maximum value
            System.out.println("Difficulty capped at 10.");
        }
    }

    /**
     * Returns the current level of the game.
     *
     * @return the current level number.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Increases the number of steps taken by the player.
     * If the maximum number of steps is reached, it ends the game.
     */
    private void addStep() {
        currSteps++;
        if (currSteps >= maxSteps) {
            if(!GUI)
                System.out.print(BOLD);
            else
                gameController.endGame("You ran out of steps!\n Game over.", "red");

            System.out.println();
            printEvent("You have reached the maximum number of steps! Game over.", RED);
            setScore(-1); // Game over, no score
            gameOver();
        }
    }

    /**
     * Moves the player to the next level if they are on a ladder.
     * If the player is on the last level, it prints a victory message.
     */
    public void nextLevel() {
        if (getLevel() == maxLevel) {
            if (!GUI)
                System.out.print(BOLD);
            else
                gameController.endGame("You escaped!\n Final score: " + getScore(), "green");

            System.out.println();
            printEvent("You have escaped the dungeon!", CYAN);
            gameOver();
        } else {
            printEvent("You climbed the ladder to the next level!", GameEngine.BLUE);
            level++;
            setDifficulty(getDifficulty() + 2);
            generateMap();
            if (!GUI) {
                System.out.println("--------------------------------------");
                printMap();
            }
        }
    }

    /**
     * Returns whether the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets the game over state to true and prints the final score.
     * If GUI is true, it adds the score to the leaderboard file.
     */
    public void gameOver() {
        this.gameOver = true;
        printEvent("Your score was " + getScore() + ".", YELLOW);
        if (GUI)
            addToFile(getScore() + " " + LocalDate.now());
    }

    /**
     * Adds the current score to the leaderboard file.
     * The score is only added if it is non-negative.
     * The file is appended with the new score and the current date.
     *
     * @param content The content to be added to the file, typically the score and date.
     */
    private void addToFile(String content) {
        // If the score is higher than the 5th highest score, add it to the file
        if (getScore() < 0) return; // Don't write negative scores

        // Append the score to the file
        try (FileWriter writer = new FileWriter("ict221-mini-dungeon-usc-TP111\\src\\main\\resources\\data\\leaderboard.txt", true)) {
            writer.write(content + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Saves the current game state to a file.
     * The file will contain the level, player position, steps, HP, score, difficulty, and map state.
     */
    public void saveGame () {
        // Save the current game state to a file
        try (FileWriter writer = new FileWriter("ict221-mini-dungeon-usc-TP111\\src\\main\\resources\\data\\savegame.txt")) {
            writer.write("Level: " + level + "\n");
            writer.write("PlayerPos: " + player.getRow() + "," + player.getCol() + "\n");
            writer.write("Steps: " + getCurrSteps() + "\n");
            writer.write("HP: " + getPlayerHP() + "\n");
            writer.write("Score: " + getScore() + "\n");
            writer.write("Difficulty: " + getDifficulty() + "\n");
            // Save map
            for (int y = 0; y < getSize(); y++) {
                for (int x = 0; x < getSize(); x++) {
                    Cell cell = getMap()[y][x];
                    GameObject go = cell.getGameObject();
                    String objectInfo = (go != null) ? go.getClass().getSimpleName() : "None";
                    writer.write("Cell (" + y + "," + x + "): " + objectInfo + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads the game state from a file.
     * The file should contain the level, player position, steps, HP, score, difficulty, and map state.
     */
    public void loadGame () {
        // Load the game state from a file
        try (BufferedReader reader = new BufferedReader(new FileReader("ict221-mini-dungeon-usc-TP111\\src\\main\\resources\\data\\savegame.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "Level:":
                        if(Integer.parseInt(parts[1].trim() ) < getLevel()) {
                            resetCurrMap(); // Reset the map if loading a lower level
                        }
                        level = Integer.parseInt(parts[1].trim());
                        break;
                    case "PlayerPos:":
                        String[] pos = parts[1].trim().split(",");
                        player.moveTo(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
                        break;
                    case "Steps:":
                        setCurrSteps(Integer.parseInt(parts[1].trim()));
                        break;
                    case "HP:":
                        setPlayerHP(Integer.parseInt(parts[1].trim()));
                        break;
                    case "Score:":
                        setScore(Integer.parseInt(parts[1].trim()));
                        break;
                    case "Difficulty:":
                        setDifficulty(Integer.parseInt(parts[1].trim()));
                        break;
                    case "Cell":
                        // This case handles the loading of game objects in cells
                        if (parts.length < 3) {
                            System.err.println("Invalid cell format in save file: " + line);
                            continue;
                        }
                        // Example: Cell (0,0): Entrance
                        // We expect parts[1] to be "Cell" and parts[2] to be "(x,y): ObjectType"
                        // So we need to parse the cell coordinates and the object type
                        String[] cellCoords = parts[1].trim().replace("(", "").replace("):", "").split(",");
                        int row = Integer.parseInt(cellCoords[0]);
                        int col = Integer.parseInt(cellCoords[1]);
                        String objectType = parts[2].trim();

                        // Clear the cell before setting the new object
                        getMap()[row][col].setGameObject(null);

                        // Set the game object based on the type
                        switch (objectType) {
                            case "None":
                                getMap()[row][col].setGameObject(null);
                                break;
                            case "Entrance":
                                getMap()[row][col].setGameObject(new Entrance());
                                break;
                            case "Ladder":
                                getMap()[row][col].setGameObject(new Ladder());
                                break;
                            case "Trap":
                                getMap()[row][col].setGameObject(new Trap());
                                break;
                            case "Gold":
                                getMap()[row][col].setGameObject(new Gold());
                                break;
                            case "MeleeMutant":
                                getMap()[row][col].setGameObject(new MeleeMutant());
                                break;
                            case "RangedMutant":
                                getMap()[row][col].setGameObject(new RangedMutant());
                                break;
                            case "HealthPotion":
                                getMap()[row][col].setGameObject(new HealthPotion());
                                break;
                            default:
                                System.err.println("Unknown game object in save file: " + line);
                        }
                        break;
                    default:
                        System.err.println("Unknown line in save file: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
    }

    /**
     * Prints an event to the console or GUI event log.
     * If GUI is true, it appends the event to the event log with an order number.
     * If GUI is false, it prints the event to the console with a specified color.
     *
     * @param event The event message to print.
     * @param COLOR The color code for console output (if GUI is false).
     */
    public void printEvent (String event, String COLOR) {
        if (GUI) {
            eventOrder++;
            gameController.getEventLog().setText(eventOrder + ". " + event + "\n" + gameController.getEventLog().getText());
        } else {
            System.out.println("> " + COLOR + event + RESET + " <");
        }
    }

    /**
     * Generates the game map with various game objects.
     * The entrance is placed at the player's position, and other objects are randomly placed in free cells.
     */
    private void generateMap() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();

        // Generate the entrance based on the player's position (bottom left by default, variable on second level)
        getMap()[player.getRow()][player.getCol()].setGameObject(new Entrance());

        // Add objects
        addMultiple(gameObjects, Ladder::new, 1);
        addMultiple(gameObjects, Trap::new, 5);
        addMultiple(gameObjects, Gold::new, 5);
        addMultiple(gameObjects, MeleeMutant::new, 3);
        addMultiple(gameObjects, RangedMutant::new, difficulty);
        addMultiple(gameObjects, HealthPotion::new, 2);

        // Shuffle and place at free positions
        List<Point> freePositions = new ArrayList<>();
        for (int y = 0; y < getSize(); y++) {
            for (int x = 0; x < getSize(); x++) {
                if (getMap()[y][x].getGameObject() == null) {
                    freePositions.add(new Point(x, y));
                }
            }
        }

        Collections.shuffle(freePositions);

        for (GameObject go : gameObjects) {
            Point p = freePositions.removeFirst();
            getMap()[p.y][p.x].setGameObject(go);
        }

        // If GUI is true, set random floor tiles for each cell
        if (GUI) {
            Random rand = new Random();
            for (int y = 0; y < getSize(); y++) {
                for (int x = 0; x < getSize(); x++) {
                    Image floorTile = gameController.getFloorTileImages().get(rand.nextInt(gameController.getFloorTileImages().size()));
                    getMap()[y][x].setFloorTile(floorTile);
                }
            }
        }
    }

    /**
     * Adds multiple instances of a game object to a list.
     *
     * @param list    the list to add the game objects to.
     * @param supplier a supplier that creates new instances of the game object.
     * @param count   the number of instances to add.
     */
    private void addMultiple(ArrayList<GameObject> list, Supplier<GameObject> supplier, int count) {
        for (int i = 0; i < count; i++) {
            list.add(supplier.get());
        }
    }

    /**
     * Prints the current game map to the console.
     * The player is represented by '@', walls by '#', and empty cells by '.'.
     */
    private void printMap() {
        for (int x = -1; x < getSize() + 1; x++) {
            for (int y = -1; y < getSize() + 1; y++) {
                if (x < 0 || y < 0 || x == getSize() || y == getSize()) {
                    // Print borders
                    System.out.print("#");
                    continue;
                }

                if (x == player.getRow() && y == player.getCol()) {
                    // If this cell is the player's location, show @ instead of the game object
                    System.out.print("@");
                    continue;
                }

                GameObject go = getMap()[x][y].getGameObject();
                System.out.print(go != null ? go.getSymbol() : ".");
            }
            System.out.println();
        }
    }

    /**
     * Main method to run the game in console mode.
     */
    public static void main(String[] args) {
        System.out.print("Enter difficulty (0-10, default is 3): ");
        int difficulty = 3;
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            try {
                difficulty = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using default difficulty 3.");
            }
        }

        GameEngine engine = new GameEngine(SIZE, difficulty);
        engine.runConsoleLoop();
    }
}
