package server;

import lombok.Data;
import lombok.EqualsAndHashCode;


// ????
@Data
@EqualsAndHashCode
public class GameRoom {

    public int idRoom;

    public SocketClient client1;
    public SocketClient client2;

    public GameRoom() {

    }

    public static GameRoom create(int idRoom){
        GameRoom room = new GameRoom();
        room.idRoom = idRoom;
        return room;
    }

    public void addClient1(SocketClient client1){
        this.client1 = client1;
        client1.setNumberOfGameRoom(this.idRoom);
    }

    public void addClient2(SocketClient client2){
        this.client2 = client2;
        client2.setNumberOfGameRoom(this.idRoom);
    }

}
