package listeners.types;

import listeners.AbstractMessageListener;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import server.GameRoom;
import server.SocketClient;

public class ClientLeftFromWaitListener extends AbstractMessageListener {

    public ClientLeftFromWaitListener() {super(TypesOfMessages.CLIENT_LEFT_FROM_WAIT);}

    @Override
    public void handleMessage(MessagePacket messagePacket, SocketClient socketClient) {

        int id = socketClient.getId();
        GameRoom gameRoom = server.getAllRooms().findRoomByClientId(id);

        if(gameRoom != null){
            if(gameRoom.client1 == socketClient){
                gameRoom.client1 = null;
            }

            server.getAllRooms().deleteRoom(gameRoom);
        }
    }

    @Override
    public byte getTypeOFMessage() {
        return type.getType();
    }

}
