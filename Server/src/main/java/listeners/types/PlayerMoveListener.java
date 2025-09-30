package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;

public class PlayerMoveListener extends AbstractMessageListener {

    public PlayerMoveListener() {
        super(TypesOfMessages.MOVE_OPPONENT);
    }

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        // считали координаты. которые передал соперник, отправляем ему эти же координаты
        double x = messagePacket.getContentFromField(0, double.class);
        double y = messagePacket.getContentFromField(1, double.class);

        MessagePacket answer = MessagePacket.create(TypesOfMessages.MOVE_OPPONENT.getType());
        answer.setContentInField(0, x);
        answer.setContentInField(1, y);

        // поиск соперника
        SocketClient opponent = server.getAllRooms().findOpponentByClientId(socketClient.getId());

        server.sendMessage(answer, opponent);

    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }

}
