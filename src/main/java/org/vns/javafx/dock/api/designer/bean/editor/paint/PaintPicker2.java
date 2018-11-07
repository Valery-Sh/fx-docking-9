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

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.designer.DesignerLookup;

/**
 *
 * @author Valery Shyshkin
 */
public class PaintPicker2 extends ContentComboBox<Paint> {
    
   // private ContentComboBox<Paint> combo;
    //private final ReadOnlyObjectWrapper<Paint> paint = new ReadOnlyObjectWrapper<>(Color.TRANSPARENT);
    //private final ObjectProperty<Paint> paint = new SimpleObjectProperty<>(Color.TRANSPARENT);
    private Paint paint;

  //  private final ObjectProperty<Paint> picked = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private final PaintPane paintPane;

    public PaintPicker2() {
        this(null);
    }

    public PaintPicker2(Paint paint) {
//        combo = new ContentComboBox<>();
        //content.getStyleClass().add("combo-combo-box");
        if (paint == null) {
            paint = Color.TRANSPARENT;
        }        
        this.paint = paint;
        //setValue(paint);
        
        getStyleClass().add("paint-picker");
        paintPane = new PaintPane();
        init();
    }

    private void init() {
       
//        setPaint(paint);

        String lbText = "";
   

        if (paint instanceof Color) {
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
        
        setDisplayNode(displayNode);
        displayNode.getStyleClass().add("display-node");
        Rectangle inner = new Rectangle(16, 12);
//        inner.setFill(Color.RED);
        inner.fillProperty().bindBidirectional(valueProperty());
        inner.getStyleClass().clear();
        inner.getStyleClass().add("color-rect");
        displayNode.setGraphic(inner);

        setContent(paintPane);

        paintPane.setMouseTransparent(false);
        
        setPaint(paint);
        valueProperty().bindBidirectional(paintPane.chosenPaintProperty());
        setValue(paint);
        
        paintPane.chosenPaintProperty().addListener((v, ov, nv) -> {
            inner.setFill(nv);
            updateText(nv);
            //setValue(nv);
        });

        valueProperty().addListener((v, ov, nv) -> {
            
            if (nv == null) {
                //nv = Color.TRANSPARENT;
            }
            updateText(nv);
            System.err.println("PainPicker VALUE = " + getValue());
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
                text = ColorPane.COLORS.get((Color)paint);
                if (text == null) {
                    text = ((Color) paint).toString();
                } else {
                    text = text.toUpperCase();
                }
            }
        }

        ((Labeled) getDisplayNode()).setText(text);
    }

    /**
     * Returns the instance of type {@code ObjectProperty } which contains the
     * start object of type {@code Paint}
     *
     * @return the instance of type {@code ObjectProperty } which contains the
     * start object of type {@code Paint}
     */
    /*    public ObjectProperty<Paint> paintProperty() {
        return paint;
    }
     */
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
     * @param paint the value used as a start initialising value
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        setValue(paint);
        paintPane.currentPaintChanged(paint);
    }

/*    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }
*/
/*    @Override
    protected Skin<?> createDefaultSkin() {
        return new PaintPickerSkin(this);
    }

    public static class PaintPickerSkin extends SkinBase<PaintPicker> {
        
        public PaintPickerSkin(PaintPicker2 control) {
            super(control);
            //control.combo.getContent().getStyleClass().clear();
            control.combo.setStyle("-fx-background-color: yellow");
            //StackPane pane = new StackPane(control.combo);
            //StackPane.setAlignment(control.combo, Pos.CENTER_LEFT);
            //pane.setStyle("-fx-background-color: aqua");
            //getChildren().add(new AnchorPane(control.combo));
            getChildren().add(control.combo);
        }
        
    }
  */  
}//PaintPicker
