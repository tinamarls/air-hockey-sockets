package listeners;

import org.example.MessagePacket;
import server.Server;
import server.SocketClient;

public interface MessageListener {

    void init(Server server);

    void handleMessage(MessagePacket messagePacket, SocketClient socketClient);

    byte getTypeOFMessage();

}
