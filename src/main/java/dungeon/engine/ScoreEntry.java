package dungeon.engine;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private final int score;
    private final String date;

    public ScoreEntry(int score, String date) {
        this.score = score;
        this.date = date;
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
