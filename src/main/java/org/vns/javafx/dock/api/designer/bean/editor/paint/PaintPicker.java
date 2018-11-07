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

import static java.util.Collections.list;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.designer.DesignerLookup;
import org.vns.javafx.dock.api.designer.bean.editor.paint.PaintPane.Options;
import static org.vns.javafx.dock.api.designer.bean.editor.paint.PaintPane.Options.COLOR;
import static org.vns.javafx.dock.api.designer.bean.editor.paint.PaintPane.Options.LINEAR_GRADIENT;
import static org.vns.javafx.dock.api.designer.bean.editor.paint.PaintPane.Options.RADIAL_GRADIENT;

/**
 *
 * @author Valery Shyshkin
 */
public class PaintPicker extends Control {
    
    
    private final ReadOnlyObjectWrapper<Paint> value = new ReadOnlyObjectWrapper<>(Color.TRANSPARENT);
    
    private ContentComboBox<Paint> combo;
    private Paint paint;

    private final PaintPane paintPane;
    
    public PaintPicker() {
        this(new Options[] {COLOR,LINEAR_GRADIENT,RADIAL_GRADIENT});
    }
    public PaintPicker(Options... options) {
        this(null,options);
    }

    public PaintPicker(Paint paint, Options... options) {
        paintPane = new PaintPane(options);
        List<Options> opts = paintPane.getOptions();
        if ( paint == null ) {
            paint = Color.TRANSPARENT;
        }
        if ( (paint instanceof Color) && ! opts.contains(COLOR)) {
            paint = createLinearGradient1();
        }
        if ( (paint instanceof LinearGradient) && ! opts.contains(LINEAR_GRADIENT)) {
            paint = createRadialGradient();
        }
        
        combo = new ContentComboBox();
        this.paint = paint;
        value.set(paint);
        //paintPane = new PaintPane(options);
        combo.setContent(paintPane);
        getStyleClass().add("paint-picker");
        combo.valueProperty().addListener((v,ov,nv) -> setValue(nv));
    }
    public ReadOnlyObjectProperty<Paint> valueProperty() {
        return value.getReadOnlyProperty();
    }
    public Paint getValue() {
        return value.get();
    }
    private void setValue(Paint paint) {
        this.value.set(paint);
    }    
    /**
     * Returns the instance of type {@code Paint} which is the start
     * initialising value.
     *
     * @return the instance of type {@code Paint} which is the start
     * initialising value.
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Sets the start initialising value specified by the parameter.
     *
     * @param paint the value used as a start initialising value
     */
    public void setPaint(Paint paint) {
        List<Options> opts = paintPane.getOptions();
        if ( (paint instanceof Color) && ! opts.contains(COLOR)) {
            return;
        }
        if ( (paint instanceof LinearGradient) && ! opts.contains(LINEAR_GRADIENT)) {
            return;
        }
        if ( (paint instanceof RadialGradient) && ! opts.contains(RADIAL_GRADIENT)) {
            return;
        }
        
        this.paint = paint;
        paintPane.currentPaintChanged(paint);
        combo.setValue(paint);
        setValue(paint);
    }

    
    protected LinearGradient createLinearGradient1() {
            return new LinearGradient(0.3,0.3,
                    1,1,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0d,Color.BLACK),
                    new Stop(1d,Color.WHITE));
    }
    protected RadialGradient createRadialGradient() {
            return new RadialGradient(0,0,
                    0.5,0.5,
                    0.5,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0d,Color.BLACK),
                    new Stop(1d,Color.WHITE));
                            
    }
    
    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PaintPickerSkin(this);
    }
    public static class PaintPickerSkin extends SkinBase<PaintPicker> {

        ContentComboBox<Paint> combo;
        PaintPicker control;

        public PaintPickerSkin(PaintPicker control) {
            super(control);
            this.control = control;
            //control.combo = new ContentComboBox();
            combo = control.combo;
           
            init();
          
            getChildren().add(combo);
        }

     
        private void init() {

            String lbText = "";
            Paint paint = control.getPaint();
            if ( paint == null ) {
                paint = Color.TRANSPARENT;
            }
            if (control.getPaint() instanceof Color) {
                if (ColorPane.COLORS.get((Color) paint) != null) {
                    lbText = ColorPane.COLORS.get((Color) paint).toUpperCase();
                } else {
                    lbText = ((Color) paint).toString();
                }
            } else if (paint instanceof LinearGradient) {
                lbText = "Linear Gradient";
            } else if (paint instanceof RadialGradient) {
                lbText = "Radial Gradient";
            }

            Label displayNode = new Label(lbText);

            combo.setDisplayNode(displayNode);

            displayNode.getStyleClass().add("display-node");
            Rectangle inner = new Rectangle(16, 12);

            inner.fillProperty().bindBidirectional(combo.valueProperty());
            inner.getStyleClass().clear();
            inner.getStyleClass().add("color-rect");
            displayNode.setGraphic(inner);
            PaintPane paintPane = control.paintPane;
            //combo.setContent(paintPane);

            paintPane.setMouseTransparent(false);

            control.setPaint(paint);
            
            combo.valueProperty().bindBidirectional(paintPane.chosenPaintProperty());
            combo.setValue(paint);
            

            paintPane.chosenPaintProperty().addListener((v, ov, nv) -> {
                inner.setFill(nv);
                updateText(nv);
                //setValue(nv);
            });

            combo.valueProperty().addListener((v, ov, nv) -> {

       
                updateText(nv);
                System.err.println("PainPicker VALUE = " + combo.getValue());
                //setPaint(nv);

            });

        }

        private void updateText(Paint paint) {
            String text = "TRANSPARENT";
            if (paint != null) {
                if (paint instanceof LinearGradient) {
                    text = "Linear Gradient";
                } else if (paint instanceof RadialGradient) {
                    text = "Radial Gradient";
                } else if (paint instanceof Color) {
                    text = ColorPane.COLORS.get((Color) paint);
                    if (text == null) {
                        text = ((Color) paint).toString();
                    } else {
                        text = text.toUpperCase();
                    }
                }
            }

            ((Labeled) combo.getDisplayNode()).setText(text);
        }

  
        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.minWidth(height);
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.minHeight(width);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.prefWidth(height) + leftInset + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return combo.prefHeight(width) + topInset + bottomInset;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            combo.resizeRelocate(x, y, w, h);
        }
    }//skin

}//PaintPicker
