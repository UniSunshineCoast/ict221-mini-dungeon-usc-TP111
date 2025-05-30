package dungeon.engine.gameobjects;

import dungeon.engine.GameEngine;

/**
 * Represents the player in the dungeon.
 * The player can move around the dungeon and interact with various game objects.
 */
public class Player extends GameObject {
    private int row;
    private int col;

    public Player(int row, int col) {
        super('@');
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public void moveTo(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    @Override
    public void onPlayerEnter(GameEngine engine) {
        // Doesn't apply to the player
    }
}
