package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;


public class ClientLeftFromGameListener extends AbstractMessageListener {

    public ClientLeftFromGameListener() {super(TypesOfMessages.LEFT_FROM_GAME);}

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        int id = socketClient.getId();
        SocketClient opponent = server.getAllRooms().findOpponentByClientId(id);

        MessagePacket answer = MessagePacket.create(TypesOfMessages.GAME_OVER.getType());

        server.sendMessage(answer, opponent);
    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }

}
