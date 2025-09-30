package ru.itis.game;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;

import javafx.scene.layout.Pane;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.MessagePacket;
import org.example.TypesOfMessages;
import ru.itis.client.Client;
import ru.itis.view.EndGame;

import java.io.IOException;

import static ru.itis.game.Player.PLAYER_R;

public class GameLoop {
    private Text scoreThisPlayer;
    private Text scoreAnotherPlayer;

    public static final double WIDTH = 400;
    public static final double HEIGHT = 600;

    private static Pane root;

    private Player player;
    private AnotherPlayer anotherPlayer;

    private Ball ball;

    // текущий клиент с которого мы играем
    private Client client;

    private Stage stage;
    private static final GameLoop GAME_LOOP = new GameLoop();

    public static GameLoop getInstance() {
        return GAME_LOOP;
    }

    public void startGame(Stage stage, Client client) {

        this.stage = stage;
        this.client = client;

        Platform.runLater(() -> {

            try {
                stage.setScene(new Scene(createContent()));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

//    public synchronized void setAnotherPlayerSpeed(double xSpeed, double ySpeed) {
//        anotherPlayer.setPlayerXSpeed(-1 * xSpeed);
//        anotherPlayer.setPlayerYSpeed(-1 * ySpeed);
//    }

    public synchronized void tpAnotherPlayer(double x, double y) {
        anotherPlayer.setX(WIDTH - x);
        anotherPlayer.setY(HEIGHT - y);
        Platform.runLater(() -> anotherPlayer.tpPlayer(WIDTH - x, HEIGHT - y));
    }

    public void createAnotherPlayer() {

        anotherPlayer = new AnotherPlayer(WIDTH / 2, HEIGHT - HEIGHT / 2 - HEIGHT / 4);

        Platform.runLater(() -> root.getChildren().add(anotherPlayer.getView()));
    }

    public void imageBall(double x, double y) {

        Platform.runLater(() -> ball.render(x, y));

    }

    private void onUpdate() {

        stage.getScene().setOnMouseDragged(mouseEvent -> {
            player.moveForward(mouseEvent);

            player.speedMessageToAnotherPlayer();
            player.moveMessageToAnotherPlayer();
            Platform.runLater(() -> player.render(player.getX(), player.getY()));
        });

    }

    private void collision(Ball ball, Player player) {

        double xDifference;
        double yDifference;

        xDifference = player.getX() - ball.getX();
        yDifference = player.getY() - ball.getY();

        double d = Math.sqrt(xDifference * xDifference + yDifference * yDifference);

        //обнаружили коллизию, если расстояние меньше или равно сумме радиусов то значит они столкнулись
        if (d <= PLAYER_R + Ball.BALL_R) {

            //это для начала игры, в начальный момент времени скорость шайбы 0, когда игрок ее толкает скорость становится 1
            if (ball.getBallXSpeed() == 0.0 && ball.getBallYSpeed() == 0.0) {
                ball.setBallXSpeed(1);
                ball.setBallYSpeed(1);
            }

            resolveCollision(ball, player);

            ball.tpBall();

            imageBall(ball.getX(), ball.getY());
            MessagePacket message = MessagePacket.create(TypesOfMessages.MOVE_BALL.getType());
            message.setContentInField(0, ball.getX());
            message.setContentInField(1, ball.getY());

            client.sendMessage(message);

        }

    }

    private <T> void resolveCollision(Ball ball, T player2) {
        Player player = null;
        if (player2.getClass().equals(Player.class)) {
            player = (Player) player2;
        }
        if (player2.getClass().equals(AnotherPlayer.class)) {
            player = (AnotherPlayer) player2;
        }

        //у нас упругий нецентральный удар
        double vByXDifference = ball.getBallXSpeed() - player.getPlayerXSpeed();
        double vByYDifference = ball.getBallYSpeed() - player.getPlayerYSpeed();

        //считаем дельты
        double distanceX = player.getX() - ball.getX();
        double distanceY = player.getY() - ball.getY();

        if (vByXDifference * distanceX + vByYDifference * distanceY >= 0) {
            //это арктангенс угла
            double angle = -Math.atan2(distanceY, distanceX);

            //удар нецентральный и скорости учитавая угол
            double ux1 = rotate(ball.getBallXSpeed(), ball.getBallYSpeed(), angle, 'x');
            double ux2 = rotate(player.getPlayerXSpeed(), player.getPlayerYSpeed(), angle, 'x');
            double uy1 = rotate(ball.getBallXSpeed(), ball.getBallYSpeed(), angle, 'y');


            //это считаем скорость которая будет после соударения по x, это физика там из импульсов это все выходит
            //если бы был центральный удар, то ux1 можно было взять просто скорость шайбы по оси x, но у нас удар не центральный, поэтому ux1 это скорость с учетом того что удар нецентральный
            //не центральный это (направление движения не совпадает с
            //линией, соединяющей центры шаров )
            double vx1 = (ux1 * (ball.getMass() - player.getMass()) + 2 * ux2 + player.getMass()) / (player.getMass() + ball.getMass());

            // впроекции на ось y скорость не изменилась, вообще тут еще можно скорость нашего игрока
            // посчитать, но мы скорость игрока не меняем поэтому только скорость шайбы
            double vy1 = uy1;

            double changeBallVx = rotate(vx1, vy1, -angle, 'x');

            double changeBallVy = rotate(vx1, vy1, -angle, 'y');

            ball.setBallXSpeed(-1 * changeBallVx);
            ball.setBallYSpeed(-1 * changeBallVy);

        }

    }

    //это нужно когда считаем скорость так как у нас удар нецентральный нам надо учитывать угол
    public double rotate(double vx, double vy, double angle, char type) {
        double velX = vx * Math.cos(angle) - vy * Math.sin(angle);
        double velY = vx * Math.sin(angle) + vy * Math.cos(angle);


        if (type == 'x') {
            return velX;
        } else {
            return velY;
        }

    }

    public void renderScore() {

        scoreThisPlayer.setText(Integer.toString(player.getScore()));

        scoreAnotherPlayer.setText(Integer.toString(anotherPlayer.getScore()));

    }

    //перемешение мячика и отслеживание коллизий происходит у приоритетного игрока,
    // если кто то забивает гол, то приоритетный игрок отправляет второму игроку новый счет
    public void setScoreForSecondPlayer(int thisPlayerScore, int anotherPlayerScore) {
        player.setScore(thisPlayerScore);
        anotherPlayer.setScore(anotherPlayerScore);

        renderScore();

    }

    public synchronized void newRound() {
        ball.setBallXSpeed(0);
        ball.setBallYSpeed(0);
        ball.setX(WIDTH / 2);
        ball.setY(HEIGHT / 2);

        player.setX(WIDTH / 2);
        player.setY(HEIGHT - HEIGHT / 4);
        player.setPlayerXSpeed(5);
        player.setPlayerYSpeed(5);

        anotherPlayer.setX(WIDTH / 2);
        anotherPlayer.setY(HEIGHT - HEIGHT / 2 - HEIGHT / 4);

        anotherPlayer.setPlayerYSpeed(5);
        anotherPlayer.setPlayerXSpeed(5);
        player.render(player.getX(), player.getY());
        anotherPlayer.render(anotherPlayer.getX(), anotherPlayer.getY());


    }

    //этот метод только для приоритетного игрока
    public void moveBall() {

        ball.tpBall();

        MessagePacket message1 = MessagePacket.create((byte) 3);
        message1.setContentInField(0, ball.getX());
        message1.setContentInField(1, ball.getY());
        client.sendMessage(message1);
        imageBall(ball.getX(), ball.getY());

        //он проверяет коллизии сразу для двух игроков и если произошло столкновение то отправляет инфу об этом второму игроку
        collision(ball, player);
        collision(ball, anotherPlayer);

        boolean scoreUpdated = false;
        if (ball.getY() + Ball.BALL_R < 0) {

            scoreUpdated = true;
            player.setScore(player.getScore() + 1);

        }
        if (ball.getY() > HEIGHT) {

            anotherPlayer.setScore(anotherPlayer.getScore() + 1);
            scoreUpdated = true;

        }
        if (scoreUpdated) {

            newRound();
            MessagePacket message = MessagePacket.create(TypesOfMessages.NEW_ROUND.getType());

            //сообщение о том что начинается новый раунд
            client.sendMessage(message);

            //сообщение для второго игрока с изменением счета
            MessagePacket messageSecond = MessagePacket.create(TypesOfMessages.SET_SCORE.getType());

            messageSecond.setContentInField(0, anotherPlayer.getScore());
            messageSecond.setContentInField(1, player.getScore());
            client.sendMessage(messageSecond);

            //отображает счет для приоритетного игрока
            renderScore();

        }
        if (player.getScore() == 4 || anotherPlayer.getScore() == 4) {

            MessagePacket messageSecond = MessagePacket.create(TypesOfMessages.GAME_OVER.getType());

            client.sendMessage(messageSecond);

            EndGame endGame = new EndGame(client.getStage());
            endGame.show();

        }

        MessagePacket message = MessagePacket.create(TypesOfMessages.MOVE_BALL.getType());
        message.setContentInField(0, ball.getX());
        message.setContentInField(1, ball.getY());

        client.sendMessage(message);
        imageBall(ball.getX(), ball.getY());

    }

    public Pane createContent() throws IOException {

        if (ball == null) {
            ball = new Ball(200, 300);
        }

        root = new Pane();
        root.setPrefSize(400, 600);
        player = new Player(WIDTH / 2, HEIGHT - HEIGHT / 4);
        player.setClient(client);

        createAnotherPlayer();

        scoreThisPlayer = new Text(WIDTH - 20, Double.parseDouble(String.valueOf(HEIGHT / 2)) + 20, String.valueOf(player.getScore()));
        scoreAnotherPlayer = new Text(WIDTH - 20, Double.parseDouble(String.valueOf(HEIGHT / 2)) - 20, String.valueOf(anotherPlayer.getScore()));
        root.getChildren().add(scoreThisPlayer);
        root.getChildren().add(scoreAnotherPlayer);

        // постоянное отображение шайбы и колотушки игроков
        Platform.runLater(() -> {
            // do your GUI stuff here
            root.getChildren().add(player.getView());
        });


        Platform.runLater(() -> {
            // do your GUI stuff here
            root.getChildren().add(ball.getView());
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
                if (client.getPriorityStatus()) {
                    moveBall();
                }
                //collision(ball, anotherPlayer);
            }
        };

        stage.setOnCloseRequest(we -> {

            MessagePacket message1 = MessagePacket.create(TypesOfMessages.LEFT_FROM_GAME.getType());
            client.sendMessage(message1);

        });

        timer.start();

        return root;
    }

    //не приоритетный у нас есть приоритетный игрок, то есть ему отправляется перемещение второго игрока, он смотрит и
    // решает коллизии и затем отправляет свое перемещение и перемещение мячика второму не приоритетному игроку
    public void tpBall(double x, double y) {

        Platform.runLater(() -> ball.render(WIDTH - x, HEIGHT - y));

    }

    //когда другой игрок перемещается он отправляет не только координаты кула он переместился,
    // но и свою скорость, это надо для коллизий
    public synchronized void setAnotherPlayerSpeed(double xSpeed, double ySpeed) {
        anotherPlayer.setPlayerXSpeed(-1 * xSpeed);
        anotherPlayer.setPlayerYSpeed(-1 * ySpeed);
    }




}