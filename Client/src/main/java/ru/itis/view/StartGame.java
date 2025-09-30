package ru.itis.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import ru.itis.client.Client;

import java.io.IOException;

public class StartGame {
    private final Pane pane;

    private final Button startGameButton;

    private Stage stage;


    public StartGame() {
        pane = new AnchorPane();
        pane.setPrefSize(400, 600);
        pane.setStyle("-fx-background-color:#E6E6FA");

        Font font = Font.font("Calibre Light", FontWeight.MEDIUM, 22);

        startGameButton = new Button("Начать игру");
        startGameButton.setMaxSize(200, 200);
        startGameButton.setFont(font);
        startGameButton.setStyle("-fx-background-color:#F8F8FF");

        startGameButton.setTranslateX(130);
        startGameButton.setTranslateY(250);
        EventHandler<ActionEvent> startGameEvent = new EventHandler<ActionEvent>() {
            @Override

            public void handle(ActionEvent actionEvent) {
                if (startGameButton == actionEvent.getSource()) {

                    Client client = new Client();

                    WaitRoom waitRoom = new WaitRoom(stage, client);
                    waitRoom.show();

                    try {
                        client.start();
                        client.setStage(waitRoom.getStage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        startGameButton.setOnAction(startGameEvent);

        pane.getChildren().add(startGameButton);

    }

    public void show(){
        Scene scene = new Scene(pane);

        stage = new Stage();

        stage.setScene(scene);
        stage.show();

    }

}
