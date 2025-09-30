package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;

public class NewRoundListener extends AbstractMessageListener {

    public NewRoundListener() {
        super(TypesOfMessages.NEW_ROUND);
    }

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        MessagePacket answer = MessagePacket.create(TypesOfMessages.NEW_ROUND.getType());

        SocketClient opponent = server.getAllRooms().findOpponentByClientId(socketClient.getId());

        server.sendMessage(answer, opponent);

    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }

}
