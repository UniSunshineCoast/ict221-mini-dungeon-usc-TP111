package dungeon.gui;

import dungeon.engine.ScoreEntry;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the leaderboard GUI.
 * Displays the top 5 scores from the game.
 */
public class LeaderboardController {
    @FXML
    private Label top1, top2, top3, top4, top5;

    private List<Label> leaderboardLabels;

    private final List<ScoreEntry> scores = new ArrayList<>();
    private final List<ScoreEntry> topScores = new ArrayList<>();

    @FXML
    private void initialize() {
        leaderboardLabels = List.of(top1, top2, top3, top4, top5);

        // Read existing scores from the file
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("ict221-mini-dungeon-usc-TP111\\src\\main\\resources\\data\\leaderboard.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(" ");
                    if (parts.length < 2) continue; // Skip invalid lines
                    int score = Integer.parseInt(parts[0]);
                    String date = parts[1];
                    scores.add(new ScoreEntry(score, date));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score format in file: " + line);
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // Sort and add top 5 scores to the leaderboard
        scores.sort(ScoreEntry::compareTo);
        for (int i = 0; i < Math.min(5, scores.size()); i++) {
            topScores.add(scores.get(i));
        }

        // Populate ListView with top scores
        populateLeaderboard();
    }

    /**
     * Populates the labels with the top 5 scores.
     */
    private void populateLeaderboard() {
        for (int i = 0; i < leaderboardLabels.size(); i++) {
            if (i < topScores.size()) {
                ScoreEntry entry = topScores.get(i);
                leaderboardLabels.get(i).setText(entry.toString());
            } else {
                leaderboardLabels.get(i).setText("---------------"); // Unused labels
            }
        }
    }
}
