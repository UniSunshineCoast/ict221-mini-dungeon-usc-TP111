import dungeon.engine.Cell;
import dungeon.engine.gameobjects.Gold;
import dungeon.engine.gameobjects.GameObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void testSetGameObject_andGetGameObject() {
        Cell cell = new Cell();
        GameObject gold = new Gold();
        cell.setGameObject(gold);
        assertSame(gold, cell.getGameObject());
    }

    @Test
    void testSetGameObjectToNull() {
        Cell cell = new Cell();
        cell.setGameObject(null);
        assertNull(cell.getGameObject());
    }
}