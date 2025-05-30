package dungeon.engine;

public class HealthPotion extends GameObject{
    private static final int HEAL = 4; // Amount of health restored by the potion

    public HealthPotion() {
        super('H');
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Logic for when the player picks up the health potion
        engine.printEvent("You picked up a health potion! You heal " + HEAL + " HP.", GameEngine.GREEN);
        engine.setPlayerHP(engine.getPlayerHP() + HEAL); // Increase player's HP by health potion heal
    }
}
