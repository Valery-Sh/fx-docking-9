/*
 * Copyright 2018 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.scene.control.paint.skin;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.vns.javafx.scene.control.paint.ColorPane;
import static org.vns.javafx.scene.control.paint.ColorPane.clamp;

/**
 *
 * @author Nastia
 */
public class ColorPaneSkin extends SkinBase<ColorPane> {

    private Pane content;
    private Region colorIndicator;

    public ColorPaneSkin(ColorPane control) {
        super(control);
        content = control.getContent();
        colorIndicator = (Region) content.getChildren().get(0);
        content.getChildren().clear();

        colorIndicator.setId("color-pane-indicator");
        colorIndicator.getStyleClass().add("color-indicator");
        colorIndicator.setManaged(false);
        colorIndicator.setMouseTransparent(true);
        colorIndicator.setCache(true);
        colorIndicator.layoutXProperty().bind(
                control.saturationProperty().divide(100).multiply(content.widthProperty())
        );
        colorIndicator.layoutYProperty().bind(
                Bindings.subtract(1, control.brightnessProperty().divide(100)).multiply(content.heightProperty()));

        final Pane colorContainer = new StackPane();
//            colorContainer.setStyle("-fx-border-width: 4; -fx-border-color: aqua; -fx-padding:5 5 5 5; ");
        final Pane huePane = new Pane();
        huePane.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
                bind(control.hueProperty());
            }

            @Override
            protected Background computeValue() {
                Background b;
                return new Background(new BackgroundFill(
                        Color.hsb(control.hueProperty().getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        final Pane overlayOnePane = new Pane();

        overlayOnePane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        final Pane overlayTwoPane = new Pane();
        overlayTwoPane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> mouseHandler = event -> {
            final double x = event.getX();
            final double y = event.getY();
            control.saturationProperty().set(clamp(x / content.getWidth()) * 100);
            control.brightnessProperty().set(100 - (clamp(y / content.getHeight()) * 100));
            control.updateChosenColor();
        };

        overlayTwoPane.setOnMouseDragged(mouseHandler);
        overlayTwoPane.setOnMousePressed(mouseHandler);

        colorContainer.getChildren().setAll(huePane, overlayOnePane, overlayTwoPane);
        content.getChildren().setAll(colorContainer, colorIndicator);

//            overlayOnePane.getStyleClass().add("content");
//            overlayTwoPane.getStyleClass().add("content");
        //control
        getChildren().add(content);
    }
}//ColorPaneSkin