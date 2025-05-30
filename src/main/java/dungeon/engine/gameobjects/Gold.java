package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

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
