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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Nastia
 */
public class PaintPane1 extends Control {

    private ColorChooserPane colorChooserPane;
    private LinearGradientPane linearGradientPane;
    private RadialGradientPane radialGradientPane;

    private final ReadOnlyObjectWrapper<Paint> chosenPaint = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<Paint> currentPaint = new SimpleObjectProperty<>();

    public PaintPane1() {
        this(null);
    }

    public PaintPane1(Paint paint) {
        if (paint == null) {
            currentPaint.set(Color.TRANSPARENT);
        } else {
            currentPaint.set(paint);
        }
        setChosenPaint(currentPaint.get());
        init();
    }

    private void init() {
        Color color = (getCurrentPaint() instanceof Color) ? (Color) getCurrentPaint() : Color.TRANSPARENT;
        colorChooserPane = new ColorChooserPane(color);
        linearGradientPane = new LinearGradientPane((getCurrentPaint() instanceof LinearGradient) ? (LinearGradient) getCurrentPaint() : null);
        radialGradientPane = new RadialGradientPane((getCurrentPaint() instanceof RadialGradient) ? (RadialGradient) getCurrentPaint() : null);

        getStyleClass().add("paint-pane");

    }

    public ColorChooserPane getColorChooserPane() {
        return colorChooserPane;
    }

    public LinearGradientPane getLinearGradientPane() {
        return linearGradientPane;
    }

    public void setLinearGradientPane(LinearGradientPane linearGradientPane) {
        this.linearGradientPane = linearGradientPane;
    }

    public RadialGradientPane getRadialGradientPane() {
        return radialGradientPane;
    }

    public void setRadialGradientPane(RadialGradientPane radialGradientPane) {
        this.radialGradientPane = radialGradientPane;
    }

    public ObjectProperty<Paint> currentPaintProperty() {
        return currentPaint;
    }

    public Paint getCurrentPaint() {
        return currentPaint.get();
    }

    public void setCurrentPaint(Paint paint) {
        System.err.println("PaintPane setCurrentPaint = " + paint);
        
        this.currentPaint.set(paint);
    }
//

    public ReadOnlyObjectProperty<Paint> chosenPaintProperty() {
        return chosenPaint.getReadOnlyProperty();
    }

    public Paint getChosenPaint() {
        return chosenPaint.get();
    }

    private void setChosenPaint(Paint paint) {
        System.err.println("PaintPane setChosenPaint = " + paint);
        this.chosenPaint.set(paint);
    }
    public void currentPaintChanged(Paint paint) {
        setCurrentPaint(paint);
        if ( paint instanceof Color) {
            colorChooserPane.currentPaintChanged(paint);
        } if ( paint instanceof LinearGradient) {
            linearGradientPane.currentPaintChanged(paint);
        } if ( paint instanceof RadialGradient) {
            radialGradientPane.currentPaintChanged(paint);
        }
    }

    protected void updatePaint(Paint paint) {
        setChosenPaint(paint);
    }

    @Override
    public String
            getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PaintPaneSkin(this);

    }

    public static class PaintPaneSkin extends SkinBase<PaintPane1> {

        private final PaintPane1 control;
        private final StackPane paintPanes;
        private final ToggleGroup tougleGroup;
        private final ToggleButton colorButton;
        private final ToggleButton linearGradientButton;
        private final ToggleButton radialGradientButton;
        
        private final ColorChooserPane colorChooserPane;
        private final LinearGradientPane linearGradientPane;
        private final RadialGradientPane radialGradientPane;

        public PaintPaneSkin(PaintPane1 control) {
            super(control);
            this.control = control;
            paintPanes = new StackPane();
            paintPanes.getStyleClass().add("content");
            
            colorButton = new ToggleButton("Color");
            linearGradientButton = new ToggleButton("Linear Gradient");
            radialGradientButton = new ToggleButton("Radial Gradient");
            
            tougleGroup = new ToggleGroup();
            tougleGroup.getToggles().addAll(colorButton, linearGradientButton, radialGradientButton );

            colorButton.getStyleClass().add("color-button");
            linearGradientButton.getStyleClass().add("linear-gradient-button");
            radialGradientButton.getStyleClass().add("gradial-gradient-button");
            //Color color = (control.getCurrentPaint() instanceof Color) ? (Color) control.getCurrentPaint() : Color.BLACK;

            colorChooserPane = control.getColorChooserPane();
            linearGradientPane = control.getLinearGradientPane();
            radialGradientPane = control.getRadialGradientPane();

            StackPane sp = new StackPane(colorChooserPane);
            sp.getStyleClass().add("color-chooser-pane");
            if (control.getCurrentPaint() instanceof Color) {
                paintPanes.getChildren().add(sp);
                tougleGroup.selectToggle(colorButton);
            } else if (control.getCurrentPaint() instanceof LinearGradient) {
                paintPanes.getChildren().add(linearGradientPane);
                tougleGroup.selectToggle(linearGradientButton);
            } else if (control.getCurrentPaint() instanceof RadialGradient) {
                paintPanes.getChildren().add(radialGradientPane);
                tougleGroup.selectToggle(radialGradientButton);
            }

            GridPane grid = new GridPane();
            StackPane root = new StackPane(grid);
            root.getStyleClass().add("content");
            //root.setStyle("-fx-background-color:yellow");
            //HBox buttonBox = new HBox(colorButton, linearGradientButton, radialGradientButton);
            grid.add(colorButton, 0, 0);
            grid.add(linearGradientButton, 1, 0);
            grid.add(radialGradientButton, 2, 0);
            grid.add(paintPanes, 0, 3, 3, 1);

            GridPane.setHgrow(colorButton, Priority.ALWAYS);
            GridPane.setHgrow(linearGradientButton, Priority.ALWAYS);
            GridPane.setHgrow(radialGradientButton, Priority.ALWAYS);


            control.getLinearGradientPane().chosenPaintProperty().addListener((v, ov, nv) -> {
                control.updatePaint(nv);
            });
            colorChooserPane.getColorPane().chosenColorProperty().addListener((v, ov, nv) -> {
                control.updatePaint(nv);
            });

            control.getRadialGradientPane().chosenPaintProperty().addListener((v, ov, nv) -> {
                control.updatePaint(nv);
            });

            tougleGroup.selectedToggleProperty().addListener((v, oldBtn, newBtn) -> {
                if (oldBtn != null && newBtn == null) {
                    tougleGroup.selectToggle(oldBtn);
                } else if (newBtn != null) {
                    if (newBtn == colorButton) {
                        paintPanes.getChildren().set(0, colorChooserPane);
                        control.updatePaint(colorChooserPane.getColorPane().getChosenColor());
                    } else if (newBtn == linearGradientButton) {
                        paintPanes.getChildren().set(0, linearGradientPane);
                        control.updatePaint(linearGradientPane.getChosenPaint());
                    } else if (newBtn == radialGradientButton) {
                        System.err.println("SELECTED RADIAL " + paintPanes.getWidth());
                        paintPanes.getChildren().set(0, radialGradientPane);
                        control.updatePaint(radialGradientPane.getChosenPaint());
                    }
                    colorButton.prefWidthProperty().unbind();
                    linearGradientButton.prefWidthProperty().unbind();
                    radialGradientButton.prefWidthProperty().unbind();
                    
                    
                    colorButton.prefWidthProperty().bind(paintPanes.widthProperty().divide(3));
                    linearGradientButton.prefWidthProperty().bind(paintPanes.widthProperty().divide(3));
                    radialGradientButton.prefWidthProperty().bind(paintPanes.widthProperty().divide(3));
                    
                    colorButton.prefWidthProperty().addListener((va,ov,nv) -> {
                        if ( tougleGroup.getSelectedToggle() == radialGradientButton ) {
                            System.err.println("   --- WIDTH = " + colorButton.getPrefWidth());
                        }
                        System.err.println("         --- w = " + colorButton.getPrefWidth());
                    });
                }
            });
            control.currentPaintProperty().addListener((v, ov, nv) -> {
                Paint paint = nv;
                if (nv == null) {
                    paint = Color.TRANSPARENT;
                } else {
                    if ((nv instanceof Color)) {
                        colorChooserPane.setCurrentColor((Color) nv);
                    } else if ((nv instanceof LinearGradient)) {
                        linearGradientPane.setCurrentPaint(nv);
                    } else if ((nv instanceof RadialGradient)) {
                        radialGradientPane.setCurrentPaint(nv);
                    }
                }
                updateButtonBind();
                control.updatePaint(control.getCurrentPaint());
            });
            if (control.getCurrentPaint() != null) {
                if (control.getCurrentPaint() instanceof Color) {
                    colorChooserPane.setCurrentColor((Color) control.getCurrentPaint());
                } else if (control.getCurrentPaint() instanceof LinearGradient) {
                    linearGradientPane.setCurrentPaint(control.getCurrentPaint());
                } else if (control.getCurrentPaint() instanceof RadialGradient) {
                    radialGradientPane.setCurrentPaint(control.getCurrentPaint());
                }
                updateButtonBind();
                control.updatePaint(control.getCurrentPaint());
            }
            
            updateButtonBind();
            control.updatePaint(control.getCurrentPaint());
            getChildren().add(root);

        }

        private void updateButtonBind() {
 System.err.println("1. updateButton bind LinearGradient currentPaint = " + control.getCurrentPaint());            
            ReadOnlyDoubleProperty prop = colorChooserPane.widthProperty();
            if ( control.getCurrentPaint()instanceof Color ) {
                System.err.println("1.0. updateButton bind LinearGradient is Color");               
                tougleGroup.selectToggle(colorButton);

            } else if (control.getCurrentPaint() instanceof LinearGradient) {
 System.err.println("2. updateButton bind LinearGradient");               
                prop = linearGradientPane.getContent().widthProperty();
                tougleGroup.selectToggle(linearGradientButton);

            } else  if (control.getCurrentPaint() instanceof RadialGradient) {
                prop = radialGradientPane.getContent().widthProperty();
                tougleGroup.selectToggle(radialGradientButton);

            }
            colorButton.prefWidthProperty().bind(prop.divide(3));
            linearGradientButton.prefWidthProperty().bind(prop.divide(3));
            radialGradientButton.prefWidthProperty().bind(prop.divide(3));

        }
    }

}
