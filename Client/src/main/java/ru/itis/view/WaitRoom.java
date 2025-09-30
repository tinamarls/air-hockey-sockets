package ru.itis.view;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import ru.itis.client.Client;

public class WaitRoom {
    private final Stage stage;
    private static Pane pane;

    public WaitRoom(Stage stage, Client client) {

        this.stage = stage;

        pane = new AnchorPane();
        pane.setPrefSize(400, 600);
        pane.setPrefSize(400, 600);
        pane.setStyle("-fx-background-color:#e9fae9");

        Text textForOurPlayer = new Text(100, 280, "Ждемс противника...");
        textForOurPlayer.setStyle("-fx-font: 22 arial;");
        pane.getChildren().add(textForOurPlayer);

        // если клиент закрыл окно, то на сервер отправляем сообщение, что он ушел
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {

                MessagePacket message = MessagePacket.create(TypesOfMessages.CLIENT_LEFT_FROM_WAIT.getType());
                client.sendMessage(message);

            }
        });
    }

    public Stage getStage(){
        return stage;
    }

    public void show(){

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();
    }
}
