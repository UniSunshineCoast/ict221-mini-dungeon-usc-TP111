package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Represents a trap in the dungeon that damages the player when entered.
 */
public class Trap extends GameObject{
    private static final int DAMAGE = 2; // Damage dealt by the trap

    public Trap() {
        super('T');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player enters the trap
        engine.printEvent("You stepped on a trap! You take " + DAMAGE + " damage.", GameEngine.RED);
        engine.setPlayerHP(engine.getPlayerHP() - DAMAGE); // Reduce player's HP by trap damage
    }
}
