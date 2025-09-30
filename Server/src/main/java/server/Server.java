package server;

import listeners.MessageListener;
import lombok.Data;
import org.example.MessagePacket;
import org.example.TypesOfMessages;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


@Data
public class Server {

    protected ServerSocket serverSocket;
    protected final int port;

    protected List<MessageListener> listeners;
    protected List<Socket> sockets;
    protected List<SocketClient> clients;

    // не надо
    protected List<GameRoom> rooms;
    protected AllRooms allRooms;

    public Server(int port){
        this.port = port;
        initServer();
    }

    public void initServer() {

        try{
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        listeners = new ArrayList<>();
        sockets = new ArrayList<>();
        rooms = new ArrayList<>();
        clients = new ArrayList<>();
        allRooms = new AllRooms();

    }

    public void registerListener(MessageListener messageListener){
        messageListener.init(this);
        listeners.add(messageListener);
    }

    public void start(){

        try {
            while(true){
                Socket socket = serverSocket.accept();
                processingSocket(socket);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void processingSocket(Socket socket){

        SocketClient client = SocketClient.create(this, socket, clients.size() + 1);
        clients.add(client);

        GameRoom freeRoom = allRooms.searchFreeRoom();

        if(freeRoom == null){

            GameRoom gameRoom = allRooms.createNewRoom();

            gameRoom.addClient1(client);

            // запускаем поток клиента
            new Thread(client).start();

            MessagePacket messagePriorityPlayer = MessagePacket.create(TypesOfMessages.PRIORITY_PLAYER.getType());
            sendMessage(messagePriorityPlayer, client);

        } else {

            // запускаем поток клиента
            new Thread(client).start();

            freeRoom.addClient2(client);

            // если комната создалась, то обоим клиентам рассылаем, пакет с типом сообщения 1
            MessagePacket messageForStart = MessagePacket.create(TypesOfMessages.GAME_START.getType());
            messageForStart.setContentInField(0, "Летс гоооу, игра началась");

            sendMessage(messageForStart, freeRoom.client1);
            sendMessage(messageForStart, freeRoom.client2);

        }

    }

    // отправка пакета какому-то клиенту
    // здесь сокетКлиент это то, КОМУ мы отправляем сообщение
    public void sendMessage(MessagePacket messagePacket, SocketClient socketClient){

        try {
            socketClient.outputStream.write(messagePacket.toByteArrayMy());
            socketClient.outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // после считывания сообщения, обрабатываем его в зависимости от типа
    // проходимся по всем листенерам
    public void acceptMessage(MessagePacket messagePacket, SocketClient sender){

        for(MessageListener listener: listeners){
            if(messagePacket.getType() == listener.getTypeOFMessage()){
                listener.handleMessage(messagePacket, sender);
            }
        }

    }

}
