package dungeon.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main GUI class for the MiniDungeon game.
 * This class initializes the JavaFX application and loads the main menu GUI.
 */
public class GameGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu_gui.fxml"));
        BorderPane root = loader.load();

        primaryStage.setTitle("MiniDungeon Game");
        primaryStage.setScene(new Scene(root, 700, 460));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /** In IntelliJ, do NOT run this method.  Run 'RunGame.main()' instead. */
    public static void main(String[] args) {
        launch(args);
    }
}
