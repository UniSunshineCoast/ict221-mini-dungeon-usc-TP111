package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Represents an entrance in the dungeon.
 * It does not have any special behavior when the player enters it,
 */
public class Entrance extends GameObject{

    public Entrance() {
        super('E');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player enters the entrance again
        engine.printEvent("This is the entrance.", GameEngine.BLUE);
    }
}
