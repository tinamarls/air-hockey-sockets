package listeners;

import org.example.TypesOfMessages;
import server.Server;

public abstract class AbstractMessageListener implements MessageListener {

    protected Server server;
    protected TypesOfMessages type;

    public AbstractMessageListener(TypesOfMessages type) {
        this.type = type;
    }

    @Override
    public void init(Server server) {
        this.server = server;
    }
}
