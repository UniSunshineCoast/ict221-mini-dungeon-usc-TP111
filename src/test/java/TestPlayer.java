import dungeon.engine.gameobjects.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestPlayer {

    @Test
    void testMoveTo() {
        Player player = new Player(5, 5);
        player.moveTo(2, 3);
        assertEquals(2, player.getRow());
        assertEquals(3, player.getCol());
    }

    @Test
    void testSymbol() {
        Player player = new Player(1, 2);
        char s = player.getSymbol();
        assertEquals('@', s);
    }
}