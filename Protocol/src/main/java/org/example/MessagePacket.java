package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class MessagePacket {

    private static final byte HEADER_1 = (byte) 0xe4;
    private static final byte HEADER_2 = (byte) 0x15;

    private static final byte FOOTER_1 = (byte) 0x00;
    private static final byte FOOTER_2 = (byte) 0x90;

    byte type;

    private List<Field> fields = new ArrayList<>();

    private MessagePacket() {}

    // статический метод, который возвращает объект
    // (создадим пакет по определенному типу, а поля потом будет передавать)
    public static MessagePacket create(byte type){
        MessagePacket packet = new MessagePacket();

        // проверка, что корректный тип пакета
        if(TypesOfMessages.findTypeBy(type) != null){
            packet.setType(type);
        } else{
            throw new IllegalArgumentException("there is no package with this type");
        }

        return packet;
    }

    // перевод пакета в потом байтов
    public byte[] toByteArrayMy(){

        // чтобы сделать список байтов(битов) из чего либо, нужно записать в потмок

        try(ByteArrayOutputStream writer = new ByteArrayOutputStream()){
            // мы хотим зашифровать наш пакет, сначала записываем хэдера
            writer.write(new byte[] {HEADER_1, HEADER_2, type});

            // записываем все поля
            for(Field field: fields){
                writer.write(new byte[]{field.getId(), field.getSize()});
                writer.write(field.getContent());
            }

            writer.write(new byte[]{FOOTER_1, FOOTER_2});

            return writer.toByteArray();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    // из потока байтов в пакет
    public static MessagePacket parse(byte[] data){

        // проверка является ли наш пакет данных именно тем, что мы хотим видеть
        // если хедеры и футеры не совпадают, то тогда пакет не наш
        if(data[0] != HEADER_1 && data[1] != HEADER_2
                || data[data.length -1] != FOOTER_2 && data[data.length -2] != FOOTER_1){
            throw new IllegalArgumentException("this is another packet");
        }

        // type точно третий элемент в массиве
        byte type = data[2];

        // по нему создаем объект и потом его заполняем
        MessagePacket messagePacket = MessagePacket.create(type);

        // перед этим уже записаны: HEADER_1, HEADER_2, type
        int offset = 3; // это поля
        while(true){
            if(data.length -2 <= offset){
                return messagePacket;
            }

            byte fieldId = data[offset]; // это гарантированно эти значения
            byte fieldSize = data[offset + 1]; // после них уже считывание контента

            byte[] content = new byte[Byte.toUnsignedInt(fieldSize)];

            if(fieldSize != 0){
                System.arraycopy(data, offset + 2, content, 0, Byte.toUnsignedInt(fieldSize));
            }

            Field field = new Field(fieldId, fieldSize, content);
            messagePacket.getFields().add(field);

            offset += 2 + fieldSize;

        }
    }

    public static boolean compareEndOfPacket(byte[] arr, int lastItem){
        return arr[lastItem - 1] == FOOTER_1 && arr[lastItem] == FOOTER_2;
    }

    // получать поле пакета
    public Field getField(int id){

        Optional<Field> field = getFields().stream().filter(f -> f.getId() == (byte) id).findFirst();

        if(field.isEmpty()){
            throw new IllegalArgumentException("No field with that id");
        }
        return field.get();
    }

    // кладем значение в поле
    public void setContentInField(int id, Object value){
        Field field;

        // пытаемся получить поле, если его нет, то создаем поле с таким id
        try {
            field = getField((byte) id);
        } catch (IllegalArgumentException e){
            field = new Field((byte) id);
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)){

            oos.writeObject(value);

            byte[] data = bos.toByteArray();

            field.setSize((byte) data.length);
            field.setContent(data);

        } catch (IOException e){
            throw new RuntimeException(e);
        }

        getFields().add(field);
    }

    public <T> T getContentFromField(int id, Class<T> tClass){
        // мы по id находим само поле с помощью метода выше
        Field field = getField(id);

        // потом у этого поля смотрим контент
        try(ByteArrayInputStream bis = new ByteArrayInputStream(field.getContent());
            ObjectInputStream ois = new ObjectInputStream(bis)){

            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }




    @Data
    @AllArgsConstructor
    public static class Field{

        private byte id;
        private byte size;
        byte[] content = new byte[size];

        public Field(byte id){this.id = id;}

    }

}
