package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

public class Ladder extends GameObject{

    public Ladder() {
        super('L');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        engine.nextLevel(); // Move the player to the next level
    }
}
