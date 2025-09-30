package ru.itis.game;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ball extends GameObject {

    public static final double BALL_R = 10;
    private Point2D lastCheckPoint;
    private double x;
    private double y;
    private double ballXSpeed;
    private double ballYSpeed;
    private double mass;

    private final GameLoop GAME_LOOP = GameLoop.getInstance();

    public double getBallXSpeed() {
        return ballXSpeed;
    }

    public void setBallXSpeed(double ballXSpeed) {
        this.ballXSpeed = ballXSpeed;
    }

    public double getBallYSpeed() {
        return ballYSpeed;
    }

    public void setBallYSpeed(double ballYSpeed) {
        this.ballYSpeed = ballYSpeed;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public Ball(double x, double y) {
        super(new Circle(BALL_R));

        this.mass = 1;

        this.ballYSpeed = 0;
        this.ballXSpeed = 0;


        this.getView().setFill(Color.rgb(0, 0, 0));

        this.x = x;
        this.y = y;

        this.lastCheckPoint = new Point2D(x, y);

    }


    public void tpBall() {
        this.x += ballXSpeed;
        this.y += ballYSpeed;

        if (x < 0) {
            x = 0;
            ballXSpeed *= -1;
        }
        if (x + Ball.BALL_R > GameLoop.WIDTH) {
            x = GameLoop.WIDTH - BALL_R;
            ballXSpeed *= -1;
        }

        this.ballXSpeed *= 0.999;
        this.ballYSpeed *= 0.999;

    }

    public synchronized void render(double x, double y) {
        this.getView().setCenterY(y);
        this.getView().setCenterX(x);
    }

}
