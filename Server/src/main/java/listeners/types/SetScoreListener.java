package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.SocketClient;

public class SetScoreListener extends AbstractMessageListener {

    public SetScoreListener() {
        super(TypesOfMessages.SET_SCORE);
    }

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        MessagePacket answer = MessagePacket.create(TypesOfMessages.SET_SCORE.getType());

        int score = messagePacket.getContentFromField(0, int.class);
        int opponentScore = messagePacket.getContentFromField(1, int.class);

        answer.setContentInField(0, score);
        answer.setContentInField(1, opponentScore);

        SocketClient opponent = server.getAllRooms().findOpponentByClientId(socketClient.getId());

        server.sendMessage(answer, opponent);

    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }

}