import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGameEngine {

    // Pokud chceš jít *above and beyond*, tak můžeš implementovat unit testy. Nevím pro co, ale je to možnost.

    @Test
    void testGetSize() {
        GameEngine ge = new GameEngine(10);

        assertEquals(10, ge.getSize());

    }
}
