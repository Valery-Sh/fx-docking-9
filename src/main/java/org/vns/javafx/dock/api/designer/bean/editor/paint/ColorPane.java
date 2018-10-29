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
package org.vns.javafx.dock.api.designer.bean.editor.paint;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
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
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Nastia
 */
//@DefaultProperty("content")
public class ColorPane extends Control {

    private Pane content;
    private Region colorIndicator;

    private final ObjectProperty<Color> currentColor = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> chosenColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private final DoubleProperty hue = new SimpleDoubleProperty(-1);
    private final DoubleProperty saturation = new SimpleDoubleProperty(-1);
    private final DoubleProperty brightness = new SimpleDoubleProperty(-1);
    private DoubleProperty alpha = new SimpleDoubleProperty(100) {

        @Override
        protected void invalidated() {
            setChosenColor(new Color(getChosenColor().getRed(),
                    getChosenColor().getGreen(),
                    getChosenColor().getBlue(),
                    clamp(alpha.get() / 100)));
        }
    };

    public ColorPane() {
        this(Color.TRANSPARENT);
    }
    public ColorPane(Color currentColor) {
        
        init(currentColor);
    }

    private void init(Color currentColor) {
        setCurrentColor(currentColor);
        colorIndicator = new Region();
        getStyleClass().add("color-pane");
        content = createContent();
        content.getStyleClass().add("content");
        updateValues();
    }

    protected Pane createContent() {
        return new StackPane(colorIndicator) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                colorIndicator.autosize();
            }
        };
    }

    public Pane getContent() {
        return content;
    }

    public DoubleProperty alphaProperty() {
        return alpha;
    }

    public double getAlpha() {
        return alpha.get();
    }

    public void setAlpha(double alpha) {
        this.alpha.set(alpha);
    }

    public DoubleProperty hueProperty() {
        return hue;
    }

    public double getHue() {
        return hue.get();
    }

    public void setHue(double hue) {
        this.hue.set(hue);
    }

    public DoubleProperty saturationProperty() {
        return saturation;
    }

    public double getSaturation() {
        return saturation.get();
    }

    public void setSaturation(double saturation) {
        this.saturation.set(saturation);
    }

    public DoubleProperty brightnessProperty() {
        return brightness;
    }

    /**
     * Gets the brightness component of the chosen {@code Color}. return
     * brightness value in the range in the range 0.0-1.0.
     */
    public double getBrightness() {
        return brightness.get();
    }

    /**
     * Sets the brightness component of the chosen {@code Color}.
     */
    public void setBrightness(double brightness) {
        this.brightness.set(brightness);
    }

    public ObjectProperty<Color> currentColorProperty() {
        return currentColor;
    }

    public Color getCurrentColor() {
        return currentColor.get();
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor.set(currentColor);
    }

    public ObjectProperty<Color> chosenColorProperty() {
        return chosenColor;
    }

    public Color getChosenColor() {
        return chosenColor.get();
    }

    public void setChosenColor(Color chosenColor) {
        this.chosenColor.set(chosenColor);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ColorPaneSkin(this);
    }

    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    public void updateChosenColor() {
        Color newColor = Color.hsb(getHue(), clamp(getSaturation() / 100),
                clamp(getBrightness() / 100), clamp(getAlpha() / 100));
        setChosenColor(newColor);
    }

    private void updateValues() {
        setHue(getCurrentColor().getHue());
        setSaturation(getCurrentColor().getSaturation() * 100);
        setBrightness(getCurrentColor().getBrightness() * 100);
        setAlpha(getCurrentColor().getOpacity() * 100);
        setChosenColor(Color.hsb(getHue(), clamp(getSaturation() / 100),
        clamp(getBrightness() / 100), clamp(getAlpha() / 100)));
    }

    public static class ColorPaneSkin extends SkinBase<ColorPane> {

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

            overlayOnePane.getStyleClass().add("content");
            overlayTwoPane.getStyleClass().add("content");

            getChildren().add(content);
        }

    }////ColorPaneSkin
}//ColorPane
