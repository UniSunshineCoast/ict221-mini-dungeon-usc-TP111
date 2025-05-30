package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

import java.util.Random;

public class RangedMutant extends GameObject{
    private static final Random rand = new Random();
    private static final int ATTACK_RANGE = 2; // Ranged attack range
    private static final int DAMAGE = 2; // Damage dealt by the ranged mutant
    private static final int SCORE = 2; // Score for killing the ranged mutant

    public RangedMutant() {
        super('R');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player kills the ranged mutant
        engine.printEvent("You killed the ranged mutant! You receive " + SCORE + " score.", GameEngine.GREEN);
        engine.setScore(engine.getScore() + SCORE); // Increase player's score by score value
    }

    // Toto je taková nějaká basic metoda, co by mohlo mít víc monster.
    // Co takto udělat MonsterGameObject, která bude mít abstraktní metodu tryAttackPlayer?
    public void tryAttackPlayer(GameEngine engine, int myRow, int myCol) {
        int playerRow = engine.getPlayer().getRow();
        int playerCol = engine.getPlayer().getCol();

        // Attack if 2 cells away (orthogonally) but not directly on the cell
        boolean canAttack =
                (myRow == playerRow && Math.abs(myCol - playerCol) <= ATTACK_RANGE && Math.abs(myCol - playerCol) > 0)
                        || (myCol == playerCol && Math.abs(myRow - playerRow) <= ATTACK_RANGE && Math.abs(myRow - playerRow) > 0);

        if (canAttack) {
            if(rand.nextBoolean()) {
                engine.printEvent("A ranged mutant hits you! You take " + DAMAGE + " damage.", GameEngine.RED);
                engine.setPlayerHP(engine.getPlayerHP() - DAMAGE); // Implement this method in GameEngine
            } else {
                engine.printEvent("A ranged mutant tried to attack you but missed!", GameEngine.GREEN);
            }
        }
    }
}
