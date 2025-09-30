package server;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllRooms {

    static List<GameRoom> rooms;

    public AllRooms() {
        rooms = new ArrayList<>();
    }

    public static GameRoom findRoomById(int id){
        for(GameRoom room: rooms){
            if(room.idRoom == id){
                return room;
            }
        }
        return null;
    }

    public GameRoom createNewRoom(){

        // генерируем случайный номер комнаты
        int idForRoom = (int) ((Math.random() * ( 1000 - 1 )) + 1);

        // проверяем, что нет комнаты с таким id, если есть, то генерируем новый id
        while (findRoomById(idForRoom) != null){
            idForRoom = (int) ((Math.random() * ( 1000 - 1 )) + 1);
        }

        // создаем комнату и добавляем в список всех комнат
        GameRoom gameRoom = GameRoom.create(idForRoom);
        rooms.add(gameRoom);
        return gameRoom;
    }

    public GameRoom findRoomByClientId(int id){
        for(GameRoom room: rooms){
            if(room.client1 != null && room.client1.id == id || room.client2 != null && room.client2.id == id){
                return room;
            }
        }
        return null;
    }

    // проходимся по всем комнатам и ищем, в какой комнате наш клиент, возращаем соперника
    // нужно для того, чтобы в листенерах знали, кому перенаправлять сообщение
    public SocketClient findOpponentByClientId(int id){
        for(GameRoom room: rooms){
            if(room.client2.id == id){
                return room.client1;
            }
            if(room.client1.id == id){
                return room.client2;
            }
        }
        return null;
    }

    public GameRoom searchFreeRoom(){

        for(GameRoom room: rooms){
            if(room.client2 == null || room.client1 == null){
                return room;
            }
        }

        return null;
    }

    public void deleteRoom(GameRoom gameRoom){
        rooms.remove(gameRoom);
    }

}
