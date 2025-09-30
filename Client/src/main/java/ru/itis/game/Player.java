package ru.itis.game;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import ru.itis.client.Client;

import static ru.itis.game.GameLoop.HEIGHT;
import static ru.itis.game.GameLoop.WIDTH;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends GameObject {

    public static final double PLAYER_R = 30;

    private Point2D lastCheckPoint;
    private Client client;

    private double x;
    private double y;
    private double mass;

    private double playerXSpeed = 5;
    private double playerYSpeed = 5;

    private int score;

    public Player(double x, double y) {

        super(new Circle(PLAYER_R));

        this.x = x;
        this.y = y;

        this.score = 0;

        this.mass = 100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d;

        this.getView().setFill(Color.rgb(128, 0, 128));

        this.lastCheckPoint = new Point2D(x, y);

        render(x, y);

    }

    //возможно еще тут synchronized поставить
    public synchronized void render(double x, double y) {

        this.getView().setCenterX(x);
        this.getView().setCenterY(y);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public  void speedMessageToAnotherPlayer(){

        MessagePacket messagePacket = MessagePacket.create(TypesOfMessages.SPEED_ANOTHER_PLAYER.getType());
        messagePacket.setContentInField(0, playerXSpeed);
        messagePacket.setContentInField(1, playerYSpeed);
        client.sendMessage(messagePacket);

    }

    public void moveMessageToAnotherPlayer() {

        MessagePacket messagePacket = MessagePacket.create(TypesOfMessages.MOVE_OPPONENT.getType());
        messagePacket.setContentInField(0, x);
        messagePacket.setContentInField(1, y);
        client.sendMessage(messagePacket);

    }

    public void moveForward(MouseEvent mouseEvent) {

        if (mouseEvent.getX() <= x) {
            if (playerXSpeed > 0) {
                playerXSpeed*=-1;
            }
            x = mouseEvent.getX();
        }

        if (mouseEvent.getX() > x) {
            if (playerXSpeed < 0) {
                playerXSpeed*=-1;
            }
            x = mouseEvent.getX();
        }

        if (mouseEvent.getY() >= y) {

            if (playerYSpeed < 0) {
                playerYSpeed*=-1;
            }
            y=mouseEvent.getY();
        }

        if (mouseEvent.getY() < y) {
            if (playerYSpeed > 0 ) {
                playerYSpeed*=-1;
            }
            y=mouseEvent.getY();
        }

        if (x -PLAYER_R< 0) {
            x = PLAYER_R;
            playerXSpeed*=-1;
        }
        if (x+ PLAYER_R > WIDTH) {
            x = WIDTH - PLAYER_R;
            playerXSpeed *= -1;

        }

        if (y - PLAYER_R < HEIGHT /2) {
            y = HEIGHT /2+PLAYER_R;
            playerYSpeed*=-1;

        }
        if (y + PLAYER_R > HEIGHT) {

            y = HEIGHT - PLAYER_R;
            playerYSpeed *= -1;
        }


    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }



}
