package dungeon.engine;

import dungeon.engine.gameobjects.GameObject;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * Represents a cell in the dungeon grid.
 * Each cell can contain a GameObject and has a floor tile image.
 */
public class Cell extends StackPane {

    private GameObject go = null;
    private Image floorTile;

    public void setGameObject(GameObject go) {
        this.go = go;
    }

    public GameObject getGameObject() {
        return go;
    }

    public void setFloorTile(Image tile) {
        this.floorTile = tile;
    }

    public Image getFloorTile() {
        return floorTile;
    }

}
