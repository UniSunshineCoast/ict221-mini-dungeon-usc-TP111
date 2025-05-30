package dungeon.engine;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class Cell extends StackPane {

    GameObject go = null;
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
