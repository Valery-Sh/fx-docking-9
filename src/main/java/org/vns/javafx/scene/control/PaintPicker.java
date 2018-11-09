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
package org.vns.javafx.scene.control;

import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.vns.javafx.scene.control.paint.ColorPane;
import org.vns.javafx.scene.control.paint.PaintPane;
import org.vns.javafx.scene.control.paint.PaintPane.Options;
import static org.vns.javafx.scene.control.paint.PaintPane.Options.COLOR;
import static org.vns.javafx.scene.control.paint.PaintPane.Options.LINEAR_GRADIENT;
import static org.vns.javafx.scene.control.paint.PaintPane.Options.RADIAL_GRADIENT;
import org.vns.javafx.scene.control.skin.PaintPickerSkin;

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
        return ColorPane.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PaintPickerSkin(this, combo, paintPane);
    }

}//PaintPicker
