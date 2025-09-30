package server;

import lombok.Data;
import org.example.MessagePacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

@Data
public class SocketClient implements Runnable{

    Socket mySocket;
    Server server;
    int id;
    int numberOfGameRoom;

    OutputStream outputStream;
    InputStream inputStream;


    private SocketClient() {}

    // в классе Server id генерируется просто, как порядковый номер среди всех подключений на сервер
    public static SocketClient create(Server server, Socket mySocket, int id){
        SocketClient client = new SocketClient();
        client.id = id;
        client.mySocket = mySocket;
        client.server = server;

        try {
            client.inputStream = mySocket.getInputStream();
            client.outputStream = mySocket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    @Override
    public void run() {

        // считывание сообщений
        while(true){

            try {
                byte[] inputData = readInput(inputStream);
                System.out.println(Arrays.toString(inputData));

                MessagePacket messagePacket = MessagePacket.parse(inputData);

                server.acceptMessage(messagePacket, this);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    // для считывания с InputStream
    private byte[] extendArray(byte[] oldArray) {
        int oldSize = oldArray.length;
        byte[] newArray = new byte[oldSize * 2];
        System.arraycopy(oldArray, 0, newArray, 0, oldSize);
        return newArray;
    }

    private byte[] readInput(InputStream stream) throws IOException {
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
