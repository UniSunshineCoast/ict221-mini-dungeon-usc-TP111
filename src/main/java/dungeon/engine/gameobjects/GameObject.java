package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Abstract class representing a game object in the dungeon.
 * Each game object has a symbol and defines behavior when the player interacts with it.
 */
public abstract class GameObject {
    protected char symbol;

    public GameObject(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() { return symbol; }

    public abstract void onPlayerEnter(GameEngine engine);
}

