package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

public abstract class GameObject {
    protected char symbol;

    public GameObject(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() { return symbol; }

    public abstract void onPlayerEnter(GameEngine engine);
}

