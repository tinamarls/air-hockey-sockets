package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;

public class MoveBallListener extends AbstractMessageListener {

    public MoveBallListener() {
        super(TypesOfMessages.MOVE_BALL);
    }

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        double x = messagePacket.getContentFromField(0, double.class);
        double y = messagePacket.getContentFromField(1, double.class);

        MessagePacket answer = MessagePacket.create(getTypeOFMessage());
        answer.setContentInField(0, x);
        answer.setContentInField(1, y);


        SocketClient opponent = server.getAllRooms().findOpponentByClientId(socketClient.getId());

        server.sendMessage(answer, opponent);

    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }
}
