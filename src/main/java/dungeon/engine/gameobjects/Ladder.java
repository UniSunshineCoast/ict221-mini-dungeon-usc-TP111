package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Represents a ladder in the dungeon that allows the player to move to the next level.
 * When the player enters the ladder, they are moved to the next level of the dungeon.
 */
public class Ladder extends GameObject{

    public Ladder() {
        super('L');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        engine.nextLevel();
    }
}
