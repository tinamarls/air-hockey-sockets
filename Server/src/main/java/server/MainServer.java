package server;

import listeners.types.*;

public class MainServer {

    public static void main(String[] args) {
        Server server = new Server(7777);
        server.registerListener(new MoveBallListener());
        server.registerListener(new PlayerMoveListener());
//        server.registerListener(new MoveSetSpeedListener());
        server.registerListener(new SetSpeedAnotherPlayerListener());
        server.registerListener(new NewRoundListener());
        server.registerListener(new SetScoreListener());
        server.registerListener(new GameOverListener());
        server.registerListener(new ClientLeftFromWaitListener());
        server.registerListener(new ClientLeftFromGameListener());
        server.start();
    }




}
