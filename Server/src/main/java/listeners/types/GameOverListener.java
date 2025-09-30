package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;

public class GameOverListener extends AbstractMessageListener {

    public GameOverListener() {
        super(TypesOfMessages.GAME_OVER);
    }

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        MessagePacket answer = MessagePacket.create(getTypeOFMessage());

        SocketClient opponent = server.getAllRooms().findOpponentByClientId(socketClient.getId());

        server.sendMessage(answer, opponent);

    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }
}
