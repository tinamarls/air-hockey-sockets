package ru.itis.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EndGame {

    private final Stage stage;
    private static Pane pane;

    public EndGame(Stage stage) {

        this.stage = stage;
        pane = new AnchorPane();

        Platform.runLater(() -> {

            pane.setPrefSize(400, 600);
            pane.setPrefSize(400, 600);
            pane.setStyle("-fx-background-color:#fff3f0");

            Text textForOurPlayer = new Text(120, 280, "Игра завершена");
            textForOurPlayer.setStyle("-fx-font: 22 arial;");
            pane.getChildren().add(textForOurPlayer);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {

                    System.exit(0);

                }
            });
        });


    }

    public void show(){

        Platform.runLater(() -> {
            Scene scene = new Scene(pane);

            stage.setScene(scene);
            stage.show();
        });
    }

}
