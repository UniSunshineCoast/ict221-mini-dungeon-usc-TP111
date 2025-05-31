import dungeon.engine.ScoreEntry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestScoreEntry {

    @Test
    void testEquals() {
        ScoreEntry entry1 = new ScoreEntry(10, "14-05-2024");
        ScoreEntry entry2 = new ScoreEntry(10, "14-05-2024");
        ScoreEntry entry3 = new ScoreEntry(20, "08-01-2025");

        assertEquals(entry1.getScore(), entry2.getScore());
        assertNotEquals(entry1.getScore(), entry3.getScore());
        assertEquals(0, entry1.compareTo(entry2));
        assertEquals(1, entry2.compareTo(entry3));
    }

    @Test
    void testToString() {
        ScoreEntry entry = new ScoreEntry(30, "20-05-2025");
        String str = entry.toString();
        assertEquals("30    20-05-2025", str);
    }
}