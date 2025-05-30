package dungeon.gui;

import dungeon.engine.ScoreEntry;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        // Používáme importy, nemusíme specifikovat cestu k classce
        // Plus, v jakémkoliv jiném případě by jsi nikdy neodkazovala na něco v /src složce. Když se to zkompiluje do .jaru, neuvidí to tvůj leaderboard.txt
        // + všiml jsem si, že v GameEngine pak načítáš taky leaderboard, proč nepoužít tuto classku všude? #load() #save() metody pro načítání/ukládání, apod.
        // Pozdější edit: ty celé cesty k classkám sem dal copilot, co? :D:D:D
        try (BufferedReader reader = new BufferedReader(new FileReader("ict221-mini-dungeon-usc-TP111\\src\\main\\resources\\data\\leaderboard.txt"))) {
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
        } catch (IOException e) {
            //   Používáme importy, nemusíme specifikovat cestu k classce
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
