package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

public class MeleeMutant extends GameObject{
    private static final int SCORE = 2; // Score value for killing the melee mutant
    private static final int DAMAGE = 2; // Damage dealt by the melee mutant

    public MeleeMutant() {
        super('M');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player kills the melee mutant
        engine.printEvent("You take " + DAMAGE + " damage from a melee mutant.", GameEngine.RED);
        engine.setPlayerHP(engine.getPlayerHP() - DAMAGE); // Reduce player's HP by mutant damage

        engine.printEvent("You killed the melee mutant! You receive " + SCORE + " score.", GameEngine.GREEN);
        engine.setScore(engine.getScore() + SCORE); // Increase player's score by score value
    }
}
