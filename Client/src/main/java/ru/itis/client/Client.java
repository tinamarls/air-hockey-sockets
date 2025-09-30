package ru.itis.client;

import javafx.stage.Stage;
import lombok.Data;
import lombok.ToString;
import org.example.MessagePacket;

import java.io.IOException;
import java.net.Socket;

@Data
@ToString
public class Client {

    private Socket socket;
    private ClientThread clientThread;
    private Stage stage;
    private Boolean priorityStatus = false;

    public void sendMessage(MessagePacket messagePacket) {

        try {
            socket.getOutputStream().write(messagePacket.toByteArrayMy());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        socket = new Socket("localhost", 7777);

        clientThread = new ClientThread(this);

        new Thread(clientThread).start();
    }

    public Boolean getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(Boolean priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

}
