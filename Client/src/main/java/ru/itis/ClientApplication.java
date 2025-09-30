package ru.itis;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.itis.view.StartGame;


import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        StartGame startGame = new StartGame();
        startGame.show();
    }

    public static void main(String[] args) {
        launch();
    }
}