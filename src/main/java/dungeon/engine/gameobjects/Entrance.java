package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

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
