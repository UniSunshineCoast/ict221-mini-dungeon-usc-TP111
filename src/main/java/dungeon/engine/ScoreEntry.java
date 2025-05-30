package dungeon.engine;

/**
 * Represents a score entry in the game.
 * Each entry consists of a score and the date it was achieved.
 */
public class ScoreEntry implements Comparable<ScoreEntry> {
    private final int score;
    private final String date;

    public ScoreEntry(int score, String date) {
        this.score = score;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Sort descending by score
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return score + "    " + date;
    }
}
