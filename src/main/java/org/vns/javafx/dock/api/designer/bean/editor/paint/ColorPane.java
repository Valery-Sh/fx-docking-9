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

import java.util.HashMap;
import java.util.Map;
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
import static javafx.scene.paint.Color.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.vns.javafx.dock.api.designer.DesignerLookup;
//import static 

/**
 *
 * @author Nastia
 */
//@DefaultProperty("content")
public class ColorPane extends Control {
    
    
    public static final Map<Color, String> COLORS = createColorNameMap();
            
    private StackPane content;
    private Region colorIndicator;

    private final ObjectProperty<Color> currentColor = new SimpleObjectProperty<>(Color.TRANSPARENT);
    private final ObjectProperty<Paint> chosenColor = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private final DoubleProperty hue = new SimpleDoubleProperty(-1);
    private final DoubleProperty saturation = new SimpleDoubleProperty(-1);
    private final DoubleProperty brightness = new SimpleDoubleProperty(-1);
    private DoubleProperty alpha = new SimpleDoubleProperty(100) {

        @Override
        protected void invalidated() {
            Color c = (Color) getChosenColor();
            setChosenColor(new Color(c.getRed(),
                    c.getGreen(),
                    c.getBlue(),
                    clamp(alpha.get() / 100)));
        }
    };

    public ColorPane() {
        this(null);
    }

    public ColorPane(Color currentColor) {
        init(currentColor);
    }

    private void init(Color currentColor) {
        if (currentColor == null) {
            setCurrentColor(Color.TRANSPARENT);
        } else {
            setCurrentColor(currentColor);
            setChosenColor(currentColor);
        }
        colorIndicator = new Region();
        getStyleClass().add("color-pane");
        content = createContent();
        content.getStyleClass().add("content");
        updateValues();
        currentColorProperty().addListener((v, ov, nv) -> {
            updateValues();
        });
    }
    
    
    protected StackPane createContent() {
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
     * Gets the brightness component of the chosen {@code Color}. 
     * 
     * @return brightness value in the range in the range 0.0-1.0
     */
    public double getBrightness() {
        return brightness.get();
    }

    /**
     * Sets the brightness component of the chosen {@code Color}.
     * @param brightness brightness value in the range in the range 0.0-1.0
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
//        System.err.println("colorPane: setCurrentColor color = " + currentColor);
        this.currentColor.set(currentColor);
    }

    public ObjectProperty<Paint> chosenColorProperty() {
        return chosenColor;
    }

    public Paint getChosenColor() {
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
    
    public void currentPaintChanged(Paint paint) {
        if ( ! (paint instanceof Color)) {
            return;
        }
        Color c = paint == null ? Color.TRANSPARENT : (Color)paint;
        setCurrentColor(c);
        updateValues();
    }
    
    private void updateValues() {
/*        System.err.println("ColorPane: UPDATE VALUES");
        System.err.println(" before  --- hue = " + getHue());
        System.err.println("         --- sat = " + getSaturation());
        System.err.println("         --- bri = " + getBrightness());
        System.err.println("         --- alf = " + getAlpha());
*/
        setHue(getCurrentColor().getHue());
        setSaturation(getCurrentColor().getSaturation() * 100);
        setBrightness(getCurrentColor().getBrightness() * 100);
        setAlpha(getCurrentColor().getOpacity() * 100);
  /*      System.err.println(" after   --- hue = " + getHue());
        System.err.println("         --- sat = " + getSaturation());
        System.err.println("         --- bri = " + getBrightness());
        System.err.println("         --- alf = " + getAlpha());
*/        
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

//            overlayOnePane.getStyleClass().add("content");
//            overlayTwoPane.getStyleClass().add("content");

            //control
            getChildren().add(content);
        }

    }////ColorPaneSkin
    
    
    private static Map<Color, String> createColorNameMap() {
        Map<Color,String> names = new HashMap<>();
        Map<String, Color> namedColors = createNamedColors();
        namedColors.forEach((name,color) -> names.put(color,name));
        return names;
    }
    private static Map<String, Color> createNamedColors() {
        Map<String, Color> colors = new HashMap<>();
        
        colors.put("aliceblue", ALICEBLUE);
        colors.put("antiquewhite", ANTIQUEWHITE);
        colors.put("aqua", AQUA);
        colors.put("aquamarine", AQUAMARINE);
        colors.put("azure", AZURE);
        colors.put("beige", BEIGE);
        colors.put("bisque", BISQUE);
        colors.put("black", BLACK);
        colors.put("blanchedalmond", BLANCHEDALMOND);
        colors.put("blue", BLUE);
        colors.put("blueviolet", BLUEVIOLET);
        colors.put("brown", BROWN);
        colors.put("burlywood", BURLYWOOD);
        colors.put("cadetblue", CADETBLUE);
        colors.put("chartreuse", CHARTREUSE);
        colors.put("chocolate", CHOCOLATE);
        colors.put("coral", CORAL);
        colors.put("cornflowerblue", CORNFLOWERBLUE);
        colors.put("cornsilk", CORNSILK);
        colors.put("crimson", CRIMSON);
        colors.put("cyan", CYAN);
        colors.put("darkblue", DARKBLUE);
        colors.put("darkcyan", DARKCYAN);
        colors.put("darkgoldenrod", DARKGOLDENROD);
        colors.put("darkgray", DARKGRAY);
        colors.put("darkgreen", DARKGREEN);
        colors.put("darkgrey", DARKGREY);
        colors.put("darkkhaki", DARKKHAKI);
        colors.put("darkmagenta", DARKMAGENTA);
        colors.put("darkolivegreen", DARKOLIVEGREEN);
        colors.put("darkorange", DARKORANGE);
        colors.put("darkorchid", DARKORCHID);
        colors.put("darkred", DARKRED);
        colors.put("darksalmon", DARKSALMON);
        colors.put("darkseagreen", DARKSEAGREEN);
        colors.put("darkslateblue", DARKSLATEBLUE);
        colors.put("darkslategray", DARKSLATEGRAY);
        colors.put("darkslategrey", DARKSLATEGREY);
        colors.put("darkturquoise", DARKTURQUOISE);
        colors.put("darkviolet", DARKVIOLET);
        colors.put("deeppink", DEEPPINK);
        colors.put("deepskyblue", DEEPSKYBLUE);
        colors.put("dimgray", DIMGRAY);
        colors.put("dimgrey", DIMGREY);
        colors.put("dodgerblue", DODGERBLUE);
        colors.put("firebrick", FIREBRICK);
        colors.put("floralwhite", FLORALWHITE);
        colors.put("forestgreen", FORESTGREEN);
        colors.put("fuchsia", FUCHSIA);
        colors.put("gainsboro", GAINSBORO);
        colors.put("ghostwhite", GHOSTWHITE);
        colors.put("gold", GOLD);
        colors.put("goldenrod", GOLDENROD);
        colors.put("gray", GRAY);
        colors.put("green", GREEN);
        colors.put("greenyellow", GREENYELLOW);
        colors.put("grey", GREY);
        colors.put("honeydew", HONEYDEW);
        colors.put("hotpink", HOTPINK);
        colors.put("indianred", INDIANRED);
        colors.put("indigo", INDIGO);
        colors.put("ivory", IVORY);
        colors.put("khaki", KHAKI);
        colors.put("lavender", LAVENDER);
        colors.put("lavenderblush", LAVENDERBLUSH);
        colors.put("lawngreen", LAWNGREEN);
        colors.put("lemonchiffon", LEMONCHIFFON);
        colors.put("lightblue", LIGHTBLUE);
        colors.put("lightcoral", LIGHTCORAL);
        colors.put("lightcyan", LIGHTCYAN);
        colors.put("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW);
        colors.put("lightgray", LIGHTGRAY);
        colors.put("lightgreen", LIGHTGREEN);
        colors.put("lightgrey", LIGHTGREY);
        colors.put("lightpink", LIGHTPINK);
        colors.put("lightsalmon", LIGHTSALMON);
        colors.put("lightseagreen", LIGHTSEAGREEN);
        colors.put("lightskyblue", LIGHTSKYBLUE);
        colors.put("lightslategray", LIGHTSLATEGRAY);
        colors.put("lightslategrey", LIGHTSLATEGREY);
        colors.put("lightsteelblue", LIGHTSTEELBLUE);
        colors.put("lightyellow", LIGHTYELLOW);
        colors.put("lime", LIME);
        colors.put("limegreen", LIMEGREEN);
        colors.put("linen", LINEN);
        colors.put("magenta", MAGENTA);
        colors.put("maroon", MAROON);
        colors.put("mediumaquamarine", MEDIUMAQUAMARINE);
        colors.put("mediumblue", MEDIUMBLUE);
        colors.put("mediumorchid", MEDIUMORCHID);
        colors.put("mediumpurple", MEDIUMPURPLE);
        colors.put("mediumseagreen", MEDIUMSEAGREEN);
        colors.put("mediumslateblue", MEDIUMSLATEBLUE);
        colors.put("mediumspringgreen", MEDIUMSPRINGGREEN);
        colors.put("mediumturquoise", MEDIUMTURQUOISE);
        colors.put("mediumvioletred", MEDIUMVIOLETRED);
        colors.put("midnightblue", MIDNIGHTBLUE);
        colors.put("mintcream", MINTCREAM);
        colors.put("mistyrose", MISTYROSE);
        colors.put("moccasin", MOCCASIN);
        colors.put("navajowhite", NAVAJOWHITE);
        colors.put("navy", NAVY);
        colors.put("oldlace", OLDLACE);
        colors.put("olive", OLIVE);
        colors.put("olivedrab", OLIVEDRAB);
        colors.put("orange", ORANGE);
        colors.put("orangered", ORANGERED);
        colors.put("orchid", ORCHID);
        colors.put("palegoldenrod", PALEGOLDENROD);
        colors.put("palegreen", PALEGREEN);
        colors.put("paleturquoise", PALETURQUOISE);
        colors.put("palevioletred", PALEVIOLETRED);
        colors.put("papayawhip", PAPAYAWHIP);
        colors.put("peachpuff", PEACHPUFF);
        colors.put("peru", PERU);
        colors.put("pink", PINK);
        colors.put("plum", PLUM);
        colors.put("powderblue", POWDERBLUE);
        colors.put("purple", PURPLE);
        colors.put("red", RED);
        colors.put("rosybrown", ROSYBROWN);
        colors.put("royalblue", ROYALBLUE);
        colors.put("saddlebrown", SADDLEBROWN);
        colors.put("salmon", SALMON);
        colors.put("sandybrown", SANDYBROWN);
        colors.put("seagreen", SEAGREEN);
        colors.put("seashell", SEASHELL);
        colors.put("sienna", SIENNA);
        colors.put("silver", SILVER);
        colors.put("skyblue", SKYBLUE);
        colors.put("slateblue", SLATEBLUE);
        colors.put("slategray", SLATEGRAY);
        colors.put("slategrey", SLATEGREY);
        colors.put("snow", SNOW);
        colors.put("springgreen", SPRINGGREEN);
        colors.put("steelblue", STEELBLUE);
        colors.put("tan", TAN);
        colors.put("teal", TEAL);
        colors.put("thistle", THISTLE);
        colors.put("tomato", TOMATO);
        colors.put("transparent", TRANSPARENT);
        colors.put("turquoise", TURQUOISE);
        colors.put("violet", VIOLET);
        colors.put("wheat", WHEAT);
        colors.put("white", WHITE);
        colors.put("whitesmoke", WHITESMOKE);
        colors.put("yellow", YELLOW);
        colors.put("yellowgreen", YELLOWGREEN);

        return colors;
    }
}//ColorPane
