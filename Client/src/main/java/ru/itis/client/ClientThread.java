package ru.itis.client;

import org.example.MessagePacket;
import ru.itis.game.GameLoop;
import ru.itis.view.EndGame;

import java.io.IOException;
import java.io.InputStream;

public class ClientThread implements Runnable{
    private final Client client;

    private final GameLoop gameLoop;
    private boolean isWorking = true;

    public ClientThread(Client client) {

        this.client = client;
        this.gameLoop = new GameLoop();

    }

    @Override
    public void run() {

        // здесь у нас считывание сообщений и обработка в зависимости от типа сообщений

        try {
            while (isWorking) {

                MessagePacket message = MessagePacket.parse(readInput(client.getSocket().getInputStream()));
                byte type = message.getType();

                switch (type) {

                    case 2 -> gameLoop.startGame(client.getStage(), client);

                    case 5 -> {
                        double x1 = message.getContentFromField(0, double.class);
                        double y1 = message.getContentFromField(1, double.class);
                        gameLoop.tpAnotherPlayer(x1, y1);

                    }

                    case 3 -> {
                        double x2 = message.getContentFromField(0, double.class);
                        double y2 = message.getContentFromField(1, double.class);
                        gameLoop.tpBall(x2, y2);
                    }

                    case 7 -> client.setPriorityStatus(true);

                    case 9 -> {
                        double x3 = message.getContentFromField(0, double.class);
                        double y3 = message.getContentFromField(1, double.class);
                        gameLoop.setAnotherPlayerSpeed(x3, y3);
                    }

                    case 10 -> gameLoop.newRound();

                    case 11 -> {
                        int thisPlayerScore = message.getContentFromField(0, int.class);
                        int anotherPlayerScore = message.getContentFromField(1, int.class);
                        gameLoop.setScoreForSecondPlayer(thisPlayerScore, anotherPlayerScore);
                    }

                    case 12 -> {
                        EndGame endGame = new EndGame(client.getStage());
                        endGame.show();

                    }

                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // для считывания массива байт с inputStream
    private static byte[] extendArray(byte[] oldArray) {
        int oldSize = oldArray.length;
        byte[] newArray = new byte[oldSize * 2];
        System.arraycopy(oldArray, 0, newArray, 0, oldSize);
        return newArray;
    }

    private static byte[] readInput(InputStream stream) throws IOException {
        int b;
        byte[] buffer = new byte[10];
        int counter = 0;
        while ((b = stream.read()) > -1) {
            buffer[counter++] = (byte) b;
            if (counter >= buffer.length) {
                buffer = extendArray(buffer);
            }
            if (counter > 1 && MessagePacket.compareEndOfPacket(buffer, counter - 1)) {
                break;
            }
        }
        byte[] data = new byte[counter];
        System.arraycopy(buffer, 0, data, 0, counter);
        return data;
    }

}
