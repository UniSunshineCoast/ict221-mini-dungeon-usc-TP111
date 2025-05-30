package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Represents a gold coin in the dungeon that can be picked up by the player.
 * When the player picks up the gold, they receive a score increase.
 */
public class Gold extends GameObject{
    private static final int SCORE = 2; // Score value for picking up gold

    public Gold() {
        super('G');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player picks up the gold
        engine.printEvent("You picked up a gold coin! You receive " + SCORE + " score.", GameEngine.YELLOW);
        engine.setScore(engine.getScore() + SCORE); // Increase player's score by score value
    }
}
