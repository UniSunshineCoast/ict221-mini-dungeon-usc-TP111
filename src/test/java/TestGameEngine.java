import dungeon.engine.GameEngine;
import dungeon.engine.gameobjects.*;
import dungeon.engine.Cell;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;

public class TestGameEngine {

    private GameEngine engine;

    /**
     * Set up the game engine before each test.
     * This initializes the game with default parameters.
     */
    @BeforeEach
    void setUp() {
        // Using default size and difficulty, console mode (no GameController)
        engine = new GameEngine(10, 3);
    }

    /**
     * Test that the game engine sets the player's initial position to (9, 0).
     */
    @Test
    void testInitialPlayerPosition() {
        Player player = engine.getPlayer();
        assertEquals(9, player.getRow());
        assertEquals(0, player.getCol());
    }

    /**
     * Test that the player can move up from the initial position (9, 0) to (8, 0).
     * The player should be able to move up at the start of the game.
     */
    @Test
    void testMovePlayerValidMove() {
        // Move up (should succeed at the start position)
        engine.movePlayer(true, true); // up
        assertEquals(8, engine.getPlayer().getRow());
        assertEquals(0, engine.getPlayer().getCol());
    }

    /**
     * Test that moving the player left in the bottom left corner does not change position.
     * The player should remain at (9, 0) since there's no valid move left.
     */
    @Test
    void testMovePlayerInvalidMove() {
        // Start at bottom left, moving left should not change position
        engine.movePlayer(false, false); // left
        assertEquals(9, engine.getPlayer().getRow());
        assertEquals(0, engine.getPlayer().getCol());
    }

    /**
     * Test that the game ends when HP reaches 0.
     */
    @Test
    void testSetPlayerHP_Death() {
        engine.setPlayerHP(0);
        assertTrue(engine.isGameOver());
        assertEquals(0, engine.getPlayerHP());
        assertTrue(engine.getScore() < 0); // score set to -1 on death
    }

    /**
     * Test that the difficulty is set correctly within bounds (0 - 10).
     */
    @Test
    void testSetDifficultyBounds() {
        engine.setDifficulty(-5);
        assertEquals(0, engine.getDifficulty());
        engine.setDifficulty(20);
        assertEquals(10, engine.getDifficulty());
    }

    /**
     * Test that the game ends when the player reaches max steps.
     */
    @Test
    void testAddStepGameOver() {
        // Simulate max steps
        for (int i = 0; i < engine.getMaxSteps() / 2; i++) {
            engine.movePlayer(true, true);
            engine.movePlayer(true, false);
        }
        assertTrue(engine.isGameOver());
    }

    /**
     * Test that the player can go to the next level.
     */
    @Test
    void testNextLevel() {
        int currLevel = engine.getLevel();
        engine.nextLevel();
        assertEquals(currLevel + 1, engine.getLevel());
    }

    /**
     * Test that the map is generated correctly with the expected number of game objects.
     */
    @Test
    void testGenerateMap_containsEntranceAndLadder() {
        Cell[][] map = engine.getMap();
        int foundEntrance = 0;
        int foundLadder = 0;
        int foundGold = 0;
        int foundTrap = 0;
        int foundPotion = 0;
        int foundMMutant = 0;
        int foundRMutant = 0;

        for (Cell[] row : map) {
            for (Cell cell : row) {
                if (cell.getGameObject() instanceof Entrance) foundEntrance++;
                if (cell.getGameObject() instanceof Ladder) foundLadder++;
                if (cell.getGameObject() instanceof Gold) foundGold++;
                if (cell.getGameObject() instanceof Trap) foundTrap++;
                if (cell.getGameObject() instanceof HealthPotion) foundPotion++;
                if (cell.getGameObject() instanceof MeleeMutant) foundMMutant++;
                if (cell.getGameObject() instanceof RangedMutant) foundRMutant++;
            }
        }

        assertEquals(1, foundEntrance);
        assertEquals(1, foundLadder);
        assertEquals(5, foundGold);
        assertEquals(5, foundTrap);
        assertEquals(2, foundPotion);
        assertEquals(3, foundMMutant);
        assertEquals(3, foundRMutant); // At difficulty 3
    }

    /**
     * Test that the game engine correctly handles saving and loading game state.
     * This includes player position, HP, score, difficulty, steps, level and map state.
     */
    @Test
    void testSaveAndLoadGame() {
        // Set game state
        engine.nextLevel();
        engine.movePlayer(true, true); // Move up once
        engine.setCurrSteps(10);
        engine.setPlayerHP(8);
        engine.setScore(123);
        engine.setDifficulty(6);

        // Save game state to file
        engine.saveGame();

        // Create a new engine and load the game state
        GameEngine loadedEngine = new GameEngine(10, 3, null);
        loadedEngine.loadGame();

        // Assert saved values match loaded values
        assertEquals(engine.getLevel(), loadedEngine.getLevel());
        assertEquals(engine.getPlayer().getRow(), loadedEngine.getPlayer().getRow());
        assertEquals(engine.getPlayer().getCol(), loadedEngine.getPlayer().getCol());
        assertEquals(engine.getCurrSteps(), loadedEngine.getCurrSteps());
        assertEquals(engine.getPlayerHP(), loadedEngine.getPlayerHP());
        assertEquals(engine.getScore(), loadedEngine.getScore());
        assertEquals(engine.getDifficulty(), loadedEngine.getDifficulty());

        // Compare random map positions
        assertEquals(getCellSymbol(engine.getMap(), 3, 7), getCellSymbol(loadedEngine.getMap(), 3, 7));
        assertEquals(getCellSymbol(engine.getMap(), 4, 6), getCellSymbol(loadedEngine.getMap(), 4, 6));
        assertEquals(getCellSymbol(engine.getMap(), 5, 5), getCellSymbol(loadedEngine.getMap(), 5, 5));
        assertEquals(getCellSymbol(engine.getMap(), 6, 4), getCellSymbol(loadedEngine.getMap(), 6, 4));
        assertEquals(getCellSymbol(engine.getMap(), 7, 3), getCellSymbol(loadedEngine.getMap(), 7, 3));
    }

    private String getCellSymbol(Cell[][] map, int row, int col) {
        Cell cell = map[row][col];
        return (cell != null && cell.getGameObject() != null) ? String.valueOf(cell.getGameObject().getSymbol()) : null;
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up the game file after tests
        File saveFile = new File("ict221-mini-dungeon-usc-TP111/src/main/resources/data/savegame.txt");
        if (saveFile.exists()) {
            Files.delete(saveFile.toPath());
        }
    }
}