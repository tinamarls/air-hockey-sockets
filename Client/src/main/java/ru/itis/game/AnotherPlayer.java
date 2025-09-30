package ru.itis.game;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AnotherPlayer extends Player {

    public AnotherPlayer(double x, double y) {
        super(x, y);
    }

    public void tpPlayer(double x, double y){

        this.getView().setCenterY(y);
        this.getView().setCenterX(x);

    }

}
