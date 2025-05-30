package dungeon.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the main menu of the Dungeon Game.
 * Handles starting the game and opening the leaderboard.
 */
public class MenuController {
    @FXML
    private Button btStart;

    @FXML
    private Slider difficultySlider;

    @FXML
    private Button btLeaderboard;

    @FXML
    private void initialize() {
        btStart.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("game_gui.fxml"));
                Parent root = loader.load();

                GameController gameController = loader.getController();
                gameController.startGame(getSelectedDifficulty());

                Stage gameStage = new Stage();
                gameStage.setScene(new Scene(root));
                gameStage.setTitle("Dungeon Game");
                gameStage.setResizable(false);
                gameStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        btLeaderboard.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderboard_gui.fxml"));
                Parent root = loader.load();

                Stage leaderboardStage = new Stage();
                leaderboardStage.setScene(new Scene(root));
                leaderboardStage.setTitle("Leaderboard");
                leaderboardStage.setResizable(false);
                leaderboardStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public int getSelectedDifficulty() {
        return (int) difficultySlider.getValue();
    }
}
