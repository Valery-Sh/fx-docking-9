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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
//@DefaultProperty("content")
public class HueBar2 extends Control {
    
    private static final PseudoClass VERTICAL_PSEUDO_CLASS = PseudoClass.getPseudoClass("vertical");

    private final ColorPane colorPane;

    private Pane content;

    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.HORIZONTAL);
    
    private ReadOnlyDoubleProperty correction;
    
            
    public HueBar2() {
        this(new ColorPane());
    }    
    public HueBar2(ColorPane colorPane) {
        this(colorPane,null);
    }
    public HueBar2(ColorPane colorPane, ReadOnlyDoubleProperty correction) {
        this.colorPane = colorPane;
        this.correction = correction;
        
        init();
    }
    private void init() {
        getStyleClass().add("hue-bar");
        content = createContent();
        content.getStyleClass().add("content");
        //content.setManaged(false);
        
        orientation.addListener((v,ov,nv) -> {
            pseudoClassStateChanged(VERTICAL_PSEUDO_CLASS, nv.equals(Orientation.VERTICAL) ? true : false);
        });
    }

    public ColorPane getColorPane() {
        return colorPane;
    }

    protected Pane createContent() {
        return new Pane();
    }

    public Pane getContent() {
        return content;
    }

    /**
     * Returns orientation for the HueBar2.
     *
     * @return The orientation for the HueBar2.
     */
    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    /**
     * Returns orientation for the HueBar2.
     *
     * @return The orientation for the HueBar2.
     */
    public Orientation getOrientation() {
        return orientation.get();
    }

    /**
     * This property controls how the HueBar2 should be displayed to the user. 
     * @param orientation the orientation to be set.
     */
    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    public ReadOnlyDoubleProperty getCorrection() {
        return correction;
    }

    public void setCorrection(ReadOnlyDoubleProperty correction) {
        this.correction = correction;
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new HueBarSkin(this);
    }

    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    public static class HueBarSkin extends SkinBase<HueBar2> {
        DoubleBinding db;// = null;
        
        private ColorPane colorPane;
        private final Pane content;
        private final Region hueIndicator;

        public HueBarSkin(HueBar2 control) {
            super(control);
            colorPane = control.getColorPane();
            content = control.getContent();
            if ( Orientation.VERTICAL.equals(control.getOrientation()) ) {
                //content.setRotate(90);
            }
            hueIndicator = new Region();
            //hueIndicator.setManaged(false);
            hueIndicator.setId("hue-bar-indicator");
            hueIndicator.getStyleClass().add("hue-indicator");

            hueIndicator.setMouseTransparent(true);
            hueIndicator.setCache(true);
            
            
            if ( control.getCorrection() == null || control.getCorrection().get() <= 0 ) {            
                //db = control.getCorrection().divide(content.widthProperty());
                db = content.widthProperty().divide(control.getCorrection());
                System.err.println("VVV = " + db.getValue());
            }
/*            if ( control.getCorrection() == null || control.getCorrection().get() <= 0 ) {
                hueIndicator.layoutXProperty().bind(
                    colorPane.hueProperty().divide(360).multiply(content.widthProperty())
                );            
                
            } else {
//                DoubleBinding db = control.getCorrection().divide(content.widthProperty());
                hueIndicator.layoutXProperty().bind(
                    colorPane.hueProperty().divide(360).multiply(content.widthProperty().multiply(0.95))
                );            
                
            }
*/            
            //content.prefWidthProperty().bind(colorPane.getContent().widthProperty());
            EventHandler<MouseEvent> mouseHandler = event -> {
                double size;
                double pos;
                System.err.println("db.value = " + db.getValue());
                System.err.println("   -- cor.value = " + control.getCorrection().get());
                System.err.println("   -- content.width = " + content.getWidth());
                
                if ( control.getOrientation().equals(Orientation.HORIZONTAL)) {
                    pos = event.getX();
                    if ( db != null ) {
                        size = colorPane.getContent().getWidth(); //*db.getValue();
                        System.err.println("   ---  size = " + size);
                    } else {
                        size = colorPane.getContent().getWidth();
                    }
                } else {
/*                    pos = event.getY();
                    size = colorPane.getHeight();
*/
                    pos = event.getX();
                    size = colorPane.getContent().getWidth();

                }
                double indWidth = hueIndicator.getWidth();// - hueIndicator.getInsets().getLeft() - hueIndicator.getInsets().getRight();
                double contentWidth = content.getWidth();// - content.getInsets().getLeft() - content.getInsets().getRight();
                
                System.err.println("hueIndicator.getWidth() = " + hueIndicator.getWidth());
                System.err.println("hueIndicator.bound.getWidth() = " + hueIndicator.getBoundsInParent().getWidth());
                System.err.println("content.getWidth() = " + content.getWidth());
                System.err.println("content.bound.getWidth() = " + content.getBoundsInParent().getWidth());
                System.err.println("ev.X = " + event.getX());
                
                System.err.println("(content.getWidth() - 1) - indWidth = " + ((content.getWidth() - 1) - indWidth));
                System.err.println("content.insets = " + content.getInsets());
                System.err.println("hueIndic.insets = " + hueIndicator.getInsets());
                if ( pos >= (contentWidth - 1) - indWidth  ) {
                    pos = contentWidth - 1;
                    hueIndicator.setLayoutX(contentWidth - indWidth - 2 );
                    System.err.println("layoutX = " + hueIndicator.getLayoutX());;
                } else if ( pos < 0 ) {
                    pos = 0;
                } else {
                    hueIndicator.setLayoutX(pos);
                }
                System.err.println("1) hueIndicator.getLayoutX() = " + hueIndicator.getLayoutX());
                System.err.println("1) hueIndicator.getLayoutY() = " + hueIndicator.getLayoutY());
                
//                hueIndicator.setLayoutY(3);
                
                hueIndicator.toFront();
                System.err.println("2) hueIndicator.getLayoutX() = " + hueIndicator.getLayoutX());
                System.err.println("2) hueIndicator.getLayoutY() = " + hueIndicator.getLayoutY());
                
                colorPane.setHue(clamp(pos / size) * 360);
                colorPane.updateChosenColor();
                
/*                final double x = event.getX();
                colorPane.setHue(clamp(x / colorPane.getWidth()) * 360);
                colorPane.updateChosenColor();
*/
            };

            content.setOnMouseDragged(mouseHandler);
            content.setOnMousePressed(mouseHandler);

            content.setBackground(new Background(new BackgroundFill(createHueGradient(),
                    CornerRadii.EMPTY, Insets.EMPTY)));

            content.getChildren().setAll(hueIndicator);
            //Group group = new Group(content);
            //StackPane stackPane = new StackPane();
            //StackPane.setAlignment(group, Pos.BASELINE_LEFT);
            
            //group.getStyleClass().add("content-parent");
            //getChildren().add(content);
/*            if ( Orientation.VERTICAL.equals(control.getOrientation()) ) {
                ToolBar toolBar = new ToolBar(group);
                toolBar.getStyleClass().clear();
                content.setRotate(90);
                toolBar.setOrientation(Orientation.VERTICAL);
                getChildren().add(toolBar);
            } else {
                stackPane.getChildren().add(content);
                getChildren().add(stackPane);
            }
*/            
            
            //stackPane.setStyle("-fx-background-color: aqua");
            getChildren().add(content);
        }
        private static LinearGradient createHueGradient() {
            double offset;
            Stop[] stops = new Stop[255];
            for (int x = 0; x < 255; x++) {
                offset = (double) ((1.0 / 255) * x);
                int h = (int) ((x / 255.0) * 360);
                stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
            }
            return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
        }
    }////ColorPaneSkin
}//ColorPane
