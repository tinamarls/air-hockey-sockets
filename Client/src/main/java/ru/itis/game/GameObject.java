package ru.itis.game;

import javafx.scene.shape.Circle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@NoArgsConstructor
@SuperBuilder
@Data
public class GameObject {
    private Circle view;

    public GameObject(Circle view) {
        this.view = view;
    }

    public Circle getView() {
        return view;
    }

}
