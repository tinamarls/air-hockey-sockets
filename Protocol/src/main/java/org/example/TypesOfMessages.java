package org.example;

import java.util.Arrays;
import java.util.List;

public enum TypesOfMessages {

    CLIENT_LEFT_FROM_WAIT( (byte) 1),
    GAME_START((byte) 2),
    MOVE_BALL((byte) 3),// движение главной шайбы
    MY_MOVE ((byte) 4), // сообщение от клиента о его движении
    MOVE_OPPONENT ((byte) 5),// сообщение клиенту о движении соперника
    PRIORITY_PLAYER ((byte) 7), // сообщение о назначении приоритетного статуса
    NEW_ROUND ( (byte) 10), // для начала нового раунда, после удара

    SPEED_ANOTHER_PLAYER((byte) 9),
    SET_SCORE ((byte) 11), // счет
    GAME_OVER ( (byte) 12), // игра завершена
    LEFT_FROM_GAME ( (byte) 13); // игрок закрыл окно во время игры





    private final byte type;

    TypesOfMessages(byte type) {
        this.type = type;
    }

    public byte getType(){return type;}


    public static TypesOfMessages findTypeBy(byte type){
        TypesOfMessages typesOfMessages = null;
        for(TypesOfMessages types : values()){
            if(types.type == type){
                typesOfMessages = types;
                break;
            }
        }
        return typesOfMessages;
    }

//    public static String findTypeBy(byte type) {
//        for (MyType myType : allTypesMessage) {
//            if (myType.id == type) {
//                return myType.name;
//            }
//        }
//        return null;
//    }
}




